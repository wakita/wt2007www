package jp.ac.titech.is.socialnet.clustering;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SmartDB {
  public enum Mode {
    Read, ReadWrite
  }

  MappedByteBuffer idx, db;

  private SmartDB(MappedByteBuffer idx, MappedByteBuffer db) {
    this.idx = idx;
    this.db = db;
  }

  public static SmartDB openIn(String path) throws IOException {
    File idx_path = new File(path + ".idx"), db_path = new File(path + ".db");
    long idx_size = idx_path.length(), db_size = db_path.length();

    FileChannel idx_chan = new RandomAccessFile(path + ".idx", "r")
        .getChannel();
    FileChannel db_chan = new RandomAccessFile(path + ".db", "r").getChannel();
    return new SmartDB(
        idx_chan.map(FileChannel.MapMode.READ_ONLY, 0, idx_size), db_chan
            .map(FileChannel.MapMode.READ_ONLY, 0, db_size));
  }

  public static SmartDB openOut(String path, long idx_size, long db_size)
      throws IOException {
    FileChannel idx_chan = new RandomAccessFile(path + ".idx", "rw")
        .getChannel();
    FileChannel db_chan = new RandomAccessFile(path + ".db", "rw").getChannel();
    return new SmartDB(idx_chan
        .map(FileChannel.MapMode.READ_WRITE, 0, idx_size), db_chan
        .map(FileChannel.MapMode.READ_WRITE, 0, db_size));
  }
}
