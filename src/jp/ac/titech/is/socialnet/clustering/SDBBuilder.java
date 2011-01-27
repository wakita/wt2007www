package jp.ac.titech.is.socialnet.clustering;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.Arrays;

public abstract class SDBBuilder {

  protected void convert(int[][] adjacencyList, String path) {

    // Compute the size of the Smart DB idx and db files.
    int size = adjacencyList.length - 1;

    int n_neighbors = 0;
    for (int i = 1; i <= size; i++) {
      int[] neighbors = adjacencyList[i];
      for (int j = 0; j < neighbors.length; j++) {
        if (neighbors[j] < 1 || neighbors[j] > size) {
          System.err
              .printf(
                      "Ill-formed input: node[%d] has a reference to node[%d], while the range of node IDs are in [1:%d]\n",
                      i, neighbors[j], size);
        }
        n_neighbors++;
      }
    }

    int idx_size = Constants.ISIZE + Constants.ISIZE * (size + 1);
    int db_size = Constants.ISIZE * n_neighbors;

    // Consistency check policy
    // 1. Self-reference --> Warning (ignored)
    // 2. Not diagonal structure --> Fatal error

    try {
      SmartDB sdb = SmartDB.openOut(path, idx_size, db_size);

      MappedByteBuffer idx = sdb.idx, db = sdb.db;
      db.position(0); // Is this necessary?
      idx.putInt(size);

      for (int node = 1; node <= size; node++) {
        idx.putInt(db.position());

        for (int neighbor : adjacencyList[node]) {
          if (neighbor == node) {
            System.err
                .printf("Warning: node[%d] is referencing itself.  Ignored.\n",
                        node);
            continue;
          }
          if (Arrays.binarySearch(adjacencyList[node], neighbor) < 0) {
            System.err
                .printf("Fatal error: one-way reference from node[%d] to node[%d] is detected.  Please check the input data.\n");
            System.exit(1);
          }
          db.putInt(neighbor);
        }
      }
      idx.putInt(db.position());
    } catch (IOException e) {
      System.err.println("I/O exception.");
      e.printStackTrace();
      System.err.println("Exiting.");
      System.exit(1);
    }
  }
}
