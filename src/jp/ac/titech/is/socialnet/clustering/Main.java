package jp.ac.titech.is.socialnet.clustering;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.PrintStream;

public class Main {
  public static final boolean debug = false;

  enum options {
    HELP, COMMAND, INPUT, ALGORITHM, SYMBOLIC
  };

  StringBuffer sbuf = new StringBuffer();
  LongOpt[] options = new LongOpt[] {
      new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
      new LongOpt("command", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
      new LongOpt("input", LongOpt.REQUIRED_ARGUMENT, null, 'i'),
      new LongOpt("algorithm", LongOpt.REQUIRED_ARGUMENT, null, 'a'),
      new LongOpt("symbolic", LongOpt.NO_ARGUMENT, null, 's') };

  void run(String[] args) {
    Getopt g = new Getopt(Main.class.getName(), args, "hc:i:a:s:", options);
    g.setOpterr(false);
    String command = null, path = null, algorithm = null;
    boolean symbolic = false;

    int c;
    while ((c = g.getopt()) != -1) {
      switch (c) {
      case 'h':
        usage();
        System.exit(0);
        break;
      case 'c':
        command = g.getOptarg();
        break;
      case 'i':
        path = g.getOptarg();
        break;
      case 'a':
        algorithm = g.getOptarg();
        break;
      case 's':
        symbolic = true;
        break;
      }
    }

    PrintStream err = System.err;

    if (command == null) {
      err.println("Error: Please Specify a command.");
      usage();
    } else if (command.equals("am2sdb") && path != null) {
      AM2SDB.main(new String[] { "--am", path + ".am", "--sdb", path });
    } else if (command.equals("csv2sdb") && path != null) {
      CSV2SDB.main(new String[] { "--csv", path + ".csv", "--sdb", path });
    } else if (command.equals("em2sdb") && path != null) {
      Symbolic2SDB.main(new String[] { "--em", path + ".em", "--sdb", path });
    } else if (command.equals("sym2sdb") && path != null) {
      Symbolic2SDB.main(new String[] { "--sym", path + ".sym", "--sdb", path });
    } else if (command.equals("analyze") && path != null) {
      if (algorithm == null || algorithm.equals("HN")) {
        jp.ac.titech.is.socialnet.clustering.HN.Analyze.main(new String[] {
            "--graph", path, "--cluster", path + "-cluster", "--history",
            path + "-history" });
      } else if (algorithm.equals("HE")) {
        jp.ac.titech.is.socialnet.clustering.HE.Analyze.main(new String[] {
            "--graph", path, "--cluster", path + "-cluster", "--history",
            path + "-history" });
      } else {
        err.println("Error: Unsupported algorithm [" + algorithm
            + "] is specified.");
        usage();
      }
    } else if (command.equals("view-history") && path != null) {
      new View().showHistory(path + "-history");
    } else if (command.equals("view-clusters") && path != null) {
      if (symbolic) new View().showClustersSymbolic(path + "-history", path
          + ".sym");
      else new View().showClusters(path + "-history", null);
    } else {
      err.println("Error: Unknown command [" + command + "].");
      usage();
    }
  }

  void usage() {
    PrintStream err = System.err;
    err.println("Usage:");
    err.println("  java Main --command am2sdb --input karate");
    err.println("  java Main --command csv2sdb --input karate");
    err.println("  java Main --command sym2sdb --input karate");
    err
        .println("  java Main --command analyze --input karate [--algorithm (HN|HE)]");
    err.println("  java Main --command view-history --input karate");
    err.println("  java Main --command view-clusthers --input karate");
  }

  public static void main(String[] args) {
    new Main().run(args);
  }
}
