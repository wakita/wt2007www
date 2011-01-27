package jp.ac.titech.is.socialnet.clustering;

import java.util.Calendar;

public class Analyze {

  protected static Parameters parseArgs(String[] args) {
    Parameters params = new Parameters();

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.equalsIgnoreCase("--graph")) params.setGraphPath(args[++i]);
      else if (arg.equals("--cluster")) params.setClusterPath(args[++i]);
      else if (arg.equals("--history")) params.setHistoryPath(args[++i]);
      // else if (arg.equals("--size")) params.setSize(args[++i]);
    }

    return params;
  }

  private static long n_joins = 0;

  public static void showDiagnosis(long[] event) {
    n_joins++;
    if (n_joins % 100000 > 0) return;

    Calendar now = Calendar.getInstance();
    int off = 0;
    @SuppressWarnings("unused")
	long id1 = event[off++], id2 = event[off++], id = event[off++], n1 = event[off++], n2 = event[off++], dq = event[off++];
    System.out.printf("%s: still computing.  %d joins has completed.\n", now
        .getTime(), n_joins);
  }
}
