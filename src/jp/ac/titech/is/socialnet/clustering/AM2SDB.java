package jp.ac.titech.is.socialnet.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.wakhok.tomoharu.csv.CSVTokenizer;

public class AM2SDB extends SDBBuilder {
  enum Parameters {
    AM, SDB
  }

  Map<Parameters, String> params = new TreeMap<Parameters, String>();

  private void usage() {
    StringBuilder error_message = new StringBuilder(
        "Command line syntax error.  Usage\n");
    error_message
        .append("java jp.ac.titech.is.socialnet.clustering.AM2SDB --am <path> --sdb <path>\n");
    error_message.append("-----\n");
    error_message
        .append("AM2SDB converts an adjacency matrix file specified by the \"-am\" paramter to a smard DB file that comprizes of an index file (*.idx) and a db file (*.db).  The path to the smart DB file is specified by removing the filename extension from the pathname to *.idx or *.db file.");
    System.err.println(error_message);
  }

  private int[][] parse(BufferedReader input) throws IOException {
    int max_id = 0, lineno;
    int[] neighbors = null;
    int[][] adjacencyList = null;
    for (lineno = 1; input.ready(); lineno++) {
      CSVTokenizer csv = new CSVTokenizer(input.readLine());
      if (max_id == 0) {
        max_id = csv.countTokens();
        neighbors = new int[max_id + 1];
        adjacencyList = new int[max_id + 1][];
      } else if (max_id != csv.countTokens()) {
        System.err.printf("Format error found in line %d.\n", lineno);
        System.exit(1);
      }
      int p = 0;
      for (int n = 1; n <= max_id; n++) {
        String acquaintance = csv.nextToken();
        if ("1".equals(acquaintance)) {
          neighbors[p++] = n;
        } else if (!"0".equals(acquaintance)) {
          System.err
              .printf(
                      "Acquantance matrix contains a data [%s] other than 0 or 1.\n",
                      acquaintance);
          System.exit(1);
        }
      }
      int[] buf = new int[p];
      for (int i = 0; i < p; i++)
        buf[i] = neighbors[i];
      adjacencyList[lineno] = buf;
    }
    lineno--;
    if (max_id != lineno) {
      System.err
          .printf(
                  "Format error.  The acquaintance matrix is ill-shaped %dx%d\n",
                  max_id, lineno);
      System.exit(1);
    }
    return adjacencyList;
  }

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

  private void run(String[] args) {
    parseArgs(args);

    File am_path = new File(params.get(Parameters.AM));

    try {
      BufferedReader am_in = new BufferedReader(new InputStreamReader(
          new FileInputStream(am_path)));
      int[][] adjacencyList = parse(am_in);

      am_in = new BufferedReader(new InputStreamReader(new FileInputStream(
          am_path)));
      convert(adjacencyList, params.get(Parameters.SDB));
    } catch (IOException e) {
      System.err.println("I/O exception:\n");
      e.printStackTrace(System.err);
    }
  }

  public static void main(String[] args) {
    new AM2SDB().run(args);
  }
}
