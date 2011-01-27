package jp.ac.titech.is.socialnet.clustering;

import java.io.File;

public class Parameters {
  private File graphPath, clusterPath, historyPath;

  // private int size;

  public File graphPath() {
    return graphPath;
  }

  public void setGraphPath(String path) {
    graphPath = new File(path);
  }

  public File clusterPath() {
    return clusterPath;
  }

  public void setClusterPath(String path) {
    clusterPath = new File(path);
  }

  public File historyPath() {
    return historyPath;
  }

  public void setHistoryPath(String path) {
    historyPath = new File(path);
  }

  /*
   * public int size() { return size; }
   * 
   * public void setSize(String size) { try { int n = Integer.parseInt(size); if
   * (n <= 0) throw new NumberFormatException(); this.size = n; } catch
   * (NumberFormatException e) {
   * System.err.printf("Error: -size parameter accepts a number > 0: [%s]\n",
   * size); System.exit(1); } }
   */}
