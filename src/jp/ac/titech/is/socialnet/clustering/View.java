package jp.ac.titech.is.socialnet.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.wakhok.tomoharu.csv.CSVTokenizer;

public class View {
  enum Parameters {
    Network, Clusters, History, ClustersSymbolic, HistorySymbolic
  }

  Map<Parameters, String> params = new TreeMap<Parameters, String>();

  private void usage() {
    StringBuilder error_message = new StringBuilder(
        "Command line syntax error.  Usage\n");
    error_message
        .append("java jp.ac.titech.is.socialnet.clustering.AM2SDB (--network <path> | --history <path> | --clusters <path>)\n");
    error_message.append("-----\n");
    error_message
        .append("View command displays the contents of three types of smard DB file: network, history, and clusters.  The path to the smart DB file is specified by removing the filename extension (.idx or .db) from the pathname.");
    System.err.println(error_message);
  }

  @SuppressWarnings("unused")
private void parseArgs(String[] args) {
    parse_args: for (int i = 0; i < args.length; i++) {
      for (Parameters p : Parameters.values()) {
        if (args[i].equals("--" + p.toString().toLowerCase())) {
          params.put(p, args[++i]);
          continue parse_args;
        }
      }
      usage();
      System.exit(1);
    }
  }

  void showNetwork(String path) {
    try {
      SmartDB sdb = SmartDB.openIn(path);
      MappedByteBuffer idx = sdb.idx, db = sdb.db;

      int n_nodes = idx.getInt(0);
      for (int i = 1; i <= n_nodes; i++) {
        idx.position(Constants.ISIZE * i);
        int off1 = idx.getInt(), off2 = idx.getInt();
        int n_neighbors = (off2 - off1) / Constants.ISIZE;
        db.position(off1);
        System.out.printf("%d:", i);
        for (int j = 1; j <= n_neighbors; j++)
          System.out.printf(" %d", db.getInt());
        System.out.println();
      }
    } catch (IOException e) {
      System.err.println("I/O exception");
      e.printStackTrace(System.err);
    }
  }

  private Hashtable<Long, String> createMap(File path) throws IOException {
    BufferedReader input = new BufferedReader(new InputStreamReader(
        new FileInputStream(path)));

    Hashtable<Long, String> hash = new Hashtable<Long, String>();

    for (long node = 1; input.ready(); node++) {
      CSVTokenizer csv = new CSVTokenizer(input.readLine());
      String nodeSymbol = csv.nextToken();
      if (hash.get(nodeSymbol) != null) {
        System.err
            .printf("Duplicate occurrence of \"%s\" detected on line %d.\n",
                    nodeSymbol, node);
      }
      hash.put(node, nodeSymbol);
    }

    if (Main.debug) dumpHash(hash);

    return hash;
  }

  private void dumpHash(Hashtable<Long, String> hash) {
    for (long key : hash.keySet()) {
      System.out.printf("%d -> %s\n", key, hash.get(key));
    }
  }

  void showHistory(String path) {
    try {
      SmartDB sdb = SmartDB.openIn(path);
      MappedByteBuffer idx = sdb.idx, db = sdb.db;

      int n_joins = idx.getInt(0);
      int fieldWidth = 0;
      for (int n = n_joins; n > 0; n /= 10)
        fieldWidth++;
      String defaultFormat = "%" + fieldWidth + "d: %d(%d) + %d(%d) -> %d\n";
      for (int i = 1; i <= n_joins; i++) {
        @SuppressWarnings("unused")
        long id1 = db.getLong() + 1, id2 = db.getLong() + 1, id = db.getLong() + 1, n1 = db
            .getLong() + 1, n2 = db.getLong() + 1, dQ = db.getLong(), time = db
            .getLong();
        System.out.printf(defaultFormat, i, id1, n1, id2, n2, id);
      }
    } catch (IOException e) {
      System.err.println("I/O exception:");
      e.printStackTrace(System.err);
    }
  }

  void showClusters(String path, Hashtable<Long, String> hash) {
    try {
      SmartDB sdb = SmartDB.openIn(path);
      MappedByteBuffer idx = sdb.idx, db = sdb.db;

      Hashtable<Long, Set<Long>> clusters = new Hashtable<Long, Set<Long>>();

      int n_joins = idx.getInt(0);
      for (int i = 1; i <= n_joins; i++) {
        db.position(idx.getInt(i * 4));
        long id1 = db.getLong() + 1, id2 = db.getLong() + 1, id = db.getLong() + 1;
        Set<Long> c1 = clusters.remove(id1), c2 = clusters.remove(id2);
        if (c1 == null) {
          c1 = new TreeSet<Long>();
          c1.add(id1);
        }
        if (c2 == null) {
          c2 = new TreeSet<Long>();
          c2.add(id2);
        }
        c1.addAll(c2);
        clusters.put(id, c1);
      }
      int cid = 1;
      for (Set<Long> c : clusters.values()) {
        System.out.printf("cluster[%d] = {", cid++);
        for (Long id : c) {
          if (hash == null) System.out.printf(" %d", id);
          else System.out.printf(" %s", hash.get(id));
        }
        System.out.println(" }");
      }
    } catch (IOException e) {
      System.err.println("I/O exception:");
      e.printStackTrace(System.err);
    }
  }

  void showClustersSymbolic(String historyPath, String networkPath) {
    try {
      showClusters(historyPath, createMap(new File(networkPath)));
    } catch (IOException e) {
      System.err
          .println("An I/O exception was raised in View::showClustersSymbolic");
      e.printStackTrace();
      System.exit(1);
    }
  }

  /*
   * private void run(String[] args) { parseArgs(args);
   * 
   * // System.out.printf("network = %s, history = %s, clusters = %s\n",
   * params.get(Parameters.Network), params.get(Parameters.History),
   * params.get(Parameters.Clusters));
   * 
   * String path; if ((path = params.get(Parameters.Network)) != null)
   * showNetwork(path); if ((path = params.get(Parameters.History)) != null)
   * showHistory(path); if ((path = params.get(Parameters.HistorySymbolic)) !=
   * null) showHistorySymbolic(path); if ((path =
   * params.get(Parameters.Clusters)) != null) showClusters(path); if ((path =
   * params.get(Parameters.ClustersSymbolic)) != null)
   * showClustersSymbolic(path); }
   * 
   * 
   * public static void main(String[] args) {
   * System.out.printf("clustering.View\n"); System.out.flush(); new
   * View().run(args); }
   */
}
