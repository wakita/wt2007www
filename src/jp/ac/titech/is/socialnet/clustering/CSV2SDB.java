package jp.ac.titech.is.socialnet.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jp.ac.wakhok.tomoharu.csv.CSVTokenizer;

public class CSV2SDB extends SDBBuilder {
  enum Parameters {
    CSV, SDB
  }

  Map<Parameters, String> params = new TreeMap<Parameters, String>();

  private void usage() {
    StringBuilder error_message = new StringBuilder();
    error_message
        .append("java jp.ac.titech.is.socialnet.clustering.CSV2SDB --csv <path> --sdb <path>\n");
    error_message.append("-----\n");
    error_message
        .append("CSV2SDB converts a CSV file specified by the \"--csv\" paramter to a smard DB file that comprizes of an index file (*.idx) and a db file (*.db).  The path to the smart DB file is specified by removing the filename extension from the pathname to *.idx or *.db file.");
    System.err.println(error_message);
  }

  private int[][] parse(BufferedReader input) throws IOException {

    int node;
    Vector<int[]> adjacencyVector = new Vector<int[]>();

    for (node = 1; input.ready(); node++) {
      CSVTokenizer csv = new CSVTokenizer(input.readLine());
      int size = csv.countTokens();
      int[] neighbors = new int[size - 1];

      try {
        int n = Integer.parseInt(csv.nextToken());
        if (node != n) {
          System.err
              .printf(
                      "An input format error detected in the CSV file:\nConsult #line=%d.  Node id %d is expected but %d is specified.\n",
                      node, node, n);
          System.exit(1);
        }
        for (int i = 1; i < size; i++) {
          String token = csv.nextToken();
          try {
            neighbors[i - 1] = Integer.parseInt(token);
          } catch (NumberFormatException e) {
            System.err.printf("Numbers are expected in the .csv file: [%s].\n",
                              token);
            System.exit(1);
          }
        }
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
      File csv_path = new File(params.get(Parameters.CSV));
      BufferedReader csv_in = new BufferedReader(new InputStreamReader(
          new FileInputStream(csv_path)));
      int[][] adjacencyList = parse(csv_in);
      csv_in.close();

      convert(adjacencyList, params.get(Parameters.SDB));
    } catch (IOException e) {

    }
  }

  public static void main(String[] args) {
    new CSV2SDB().run(args);
  }
}
