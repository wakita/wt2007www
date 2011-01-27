package jp.ac.titech.is.socialnet.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jp.ac.wakhok.tomoharu.csv.CSVTokenizer;

public class Symbolic2SDB extends SDBBuilder {
  enum Parameters {
    SYM, SDB
  }

  Map<Parameters, String> params = new TreeMap<Parameters, String>();

  private void usage() {
    StringBuilder error_message = new StringBuilder();
    error_message
        .append("java jp.ac.titech.is.socialnet.clustering.Symbolic2SDB --sym <path> --sdb <path>\n");
    error_message.append("-----\n");
    error_message
        .append("Symbolic2SDB converts a symbolic CSV file specified by the \"--sym\" paramter to a smard DB file that comprizes of an index file (*.idx) and a db file (*.db).  The path to the smart DB file is specified by removing the filename extension from the pathname to *.idx or *.db file.");
    System.err.println(error_message);
  }

  private Hashtable<String, Integer> createMap(BufferedReader input)
      throws IOException {
    Hashtable<String, Integer> hash = new Hashtable<String, Integer>();

    for (int node = 1; input.ready(); node++) {
      CSVTokenizer csv = new CSVTokenizer(input.readLine());
      String nodeSymbol = csv.nextToken();
      if (hash.get(nodeSymbol) != null) {
        System.err
            .printf("Duplicate occurrence of \"%s\" detected on line %d.\n",
                    nodeSymbol, node);
      }
      hash.put(nodeSymbol, node);
    }

    return hash;
  }

  private int[][] parse(BufferedReader input, Hashtable<String, Integer> hash)
      throws IOException {

    int node;
    Vector<int[]> adjacencyVector = new Vector<int[]>();

    for (node = 1; input.ready(); node++) {
      CSVTokenizer csv = new CSVTokenizer(input.readLine());
      int size = csv.countTokens();
      int[] neighbors = new int[size - 1];

      try {
        String nodeSymbol = csv.nextToken();
        int n = hash.get(nodeSymbol);
        if (node != n) {
          System.err
              .printf(
                      "This cannot happen: \"%s\" on line number %d is mapped to %d.\n",
                      nodeSymbol, node, n);
          System.exit(1);
        }
        if (Main.debug) System.out.printf("%s:", nodeSymbol);
        for (int i = 1; i < size; i++) {
          String token = csv.nextToken();
          try {
            if (Main.debug) System.out.print(" " + token);
            neighbors[i - 1] = hash.get(token);
          } catch (NumberFormatException e) {
            System.err.printf("Numbers are expected in the .csv file: [%s].\n",
                              token);
            System.exit(1);
          }
        }
        if (Main.debug) System.out.println();
      } catch (NumberFormatException e) {
        System.err.printf("A number format error was detected in #line=%d.¥n",
                          node);
      }
      adjacencyVector.add(neighbors);
    }
    int[][] adjacencyList = new int[adjacencyVector.size() + 1][];
    int i = 1;
    for (int[] neighbors : adjacencyVector) {
      adjacencyList[i++] = neighbors;
    }

    return adjacencyList;
  }

  private void parseArgs(String[] args) {
    parse_args: for (int i = 0; i < args.length; i++) {
      for (Parameters p : Parameters.values()) {
        if (args[i].equals("--help")) {
          usage();
          return;
        }
        if (args[i].equals("--" + p.toString().toLowerCase())) {
          params.put(p, args[++i]);
          continue parse_args;
        }
      }
      System.out
          .printf(
                  "\nCommand line syntax error.  Unknown parameter [%s] detected.\n\n",
                  args[i]);
      usage();
      System.exit(1);
    }
  }

  private void run(String[] args) {
    parseArgs(args);

    try {
      File csv_path = new File(params.get(Parameters.SYM));
      BufferedReader csv_in = new BufferedReader(new InputStreamReader(
          new FileInputStream(csv_path)));
      Hashtable<String, Integer> hash = createMap(csv_in);
      csv_in = new BufferedReader(new InputStreamReader(new FileInputStream(
          csv_path)));
      int[][] adjacencyList = parse(csv_in, hash);
      csv_in.close();

      convert(adjacencyList, params.get(Parameters.SDB));
    } catch (IOException e) {

    }
  }

  public static void main(String[] args) {
    new Symbolic2SDB().run(args);
  }
}
