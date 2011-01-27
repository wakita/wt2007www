package jp.ac.titech.is.socialnet.clustering.original.CNM;

import java.io.IOException;
import java.nio.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Vector;
public class Mixi_DB_Handler {
	static final int INTEGER_SIZE = 4;
	static final int LONG_SIZE = 8;
	static final int SURPLUS_SIZE = 2 * INTEGER_SIZE;
	private boolean compact_flag = false;
	public static final int NULL_NUM = 0;
	private final int    MAX_ID;
	private String Index_name, DB_name;

	public void setCompactFlag(boolean flag){
		compact_flag = flag;
	}
	
	public Mixi_DB_Handler(String idx, String db, int max_id){
		Index_name = idx;
		DB_name = db;
		MAX_ID = max_id; 
	}
	
	public void make_dQ_DB(long[][] neighborsDQ)throws IOException{
		System.out.println("Creating the dqDB and dqIndex");
		long[][] dq_arr = neighborsDQ;
		int dq_info_num = 0;
		int current_id;
		for(long[] dqs : dq_arr){
			dq_info_num += dqs.length;
		}
		int IDXSIZE;
		IDXSIZE = dq_arr.length * INTEGER_SIZE + SURPLUS_SIZE;
		int DBSIZE       = dq_info_num * LONG_SIZE;
		FileChannel idx_chan = new RandomAccessFile(Index_name, "rw").getChannel();
	    FileChannel db_chan = new RandomAccessFile(DB_name, "rw").getChannel();
	    MappedByteBuffer idx = idx_chan.map(FileChannel.MapMode.READ_WRITE, 0, IDXSIZE);
	    MappedByteBuffer db = db_chan.map(FileChannel.MapMode.READ_WRITE, 0, DBSIZE);

	    idx.putInt(dq_arr.length);
	    
	    db.position(0);
	    
	    current_id = 1;
	    for (long[] dqs : dq_arr) {
	    	idx.putInt(db.position());
	    	for (long dq : dqs) {
	   			db.putLong( dq );
	   		}
	   		current_id++;
		}
	    idx.putInt(db.position());
	    idx_chan.close();
	    db_chan.close();
	    System.out.println("Created the dqDB and dqIndex");
	}
	
	public void make_DB(int[][] sorted_link)throws IOException{
		System.out.println("Creating the DB and Index");
		int[][] link_arr = sorted_link;
		int cell_info_num = 0;
		int current_id = 1;
		for(int[] link : link_arr){
			if (current_id <= MAX_ID){
				for(int id : link){
					if(id <= MAX_ID && 0 < id && NULL_NUM != id){
						cell_info_num++;
					}
				}
				current_id++;
			}else{
				break;
			}
		}
		int IDXSIZE;
		if(link_arr.length <= MAX_ID){
			IDXSIZE = link_arr.length * INTEGER_SIZE + SURPLUS_SIZE;
		}else{
			IDXSIZE = MAX_ID * INTEGER_SIZE + SURPLUS_SIZE;
		}
		int DBSIZE       = cell_info_num * INTEGER_SIZE;
		FileChannel idx_chan = new RandomAccessFile(Index_name, "rw").getChannel();
	    FileChannel db_chan = new RandomAccessFile(DB_name, "rw").getChannel();
	    MappedByteBuffer idx = idx_chan.map(FileChannel.MapMode.READ_WRITE, 0, IDXSIZE);
	    MappedByteBuffer db = db_chan.map(FileChannel.MapMode.READ_WRITE, 0, DBSIZE);

	    if(link_arr.length <= MAX_ID){
	    	idx.putInt(link_arr.length);
	    }else{
	    	idx.putInt(MAX_ID);
	    }
	    
	    db.position(0);
	    
	    current_id = 1;
	    for (int[] link : link_arr) {
	    	if (current_id <= MAX_ID){
	    		idx.putInt(db.position());
	    		for (int id : link) {
	    			if(0 < id && id <= MAX_ID && NULL_NUM != id){
	    				db.putInt( id );
	    			}
	    		}
	    		current_id++;
	    	}else{
	    		break;
	    	}
		}
	    idx.putInt(db.position());
	    idx_chan.close();
	    db_chan.close();
	    System.out.println("Created the DB and Index");
	}
	
	public void make_DB(String csv_name)throws IOException{
		System.out.println("Creating the DB and Index");
		CSV_Handler csv_handle = new CSV_Handler(csv_name);
		int[][] link_arr;
		if(compact_flag){
			link_arr = csv_handle.csv2arr_compact();
		}else{
			link_arr = csv_handle.csv2arr();
		}
		int cell_info_num = 0;
		int current_id = 1;
		for(int[] link : link_arr){
			if (current_id <= MAX_ID){
				for(int id : link){
					if(id <= MAX_ID || 0 < id || NULL_NUM != id){
						cell_info_num++;
					}
				}
				current_id++;
			}else{
				break;
			}
		}
		int IDXSIZE;
		if(link_arr.length <= MAX_ID){
			IDXSIZE = link_arr.length * INTEGER_SIZE + SURPLUS_SIZE;
		}else{
			IDXSIZE = MAX_ID * INTEGER_SIZE + SURPLUS_SIZE;
		}
		int DBSIZE       = cell_info_num * INTEGER_SIZE;
		FileChannel idx_chan = new RandomAccessFile(Index_name, "rw").getChannel();
	    FileChannel db_chan = new RandomAccessFile(DB_name, "rw").getChannel();
	    MappedByteBuffer idx = idx_chan.map(FileChannel.MapMode.READ_WRITE, 0, IDXSIZE);
	    MappedByteBuffer db = db_chan.map(FileChannel.MapMode.READ_WRITE, 0, DBSIZE);

	    if(link_arr.length <= MAX_ID){
	    	idx.putInt(link_arr.length);
	    }else{
	    	idx.putInt(MAX_ID);
	    }
	    
	    db.position(0);
	    
	    current_id = 1;
	    for (int[] link : link_arr) {
	    	if (current_id <= MAX_ID){
	    		idx.putInt(db.position());
	    		for (int id : link) {
	    			if(0 < id && id <= MAX_ID && NULL_NUM != id){
	    				db.putInt( id );
	    			}
	    		}
	    		current_id++;
	    	}else{
	    		break;
	    	}
		}
	    idx.putInt(db.position());
	    idx_chan.close();
	    db_chan.close();
	    System.out.println("Created the DB and Index");
	}
	public int[] neighbors(int i) throws IOException{
		FileChannel idx_chan = new RandomAccessFile(Index_name, "r").getChannel();
	    FileChannel db_chan = new RandomAccessFile(DB_name, "r").getChannel();
	    long IDXSIZE = new File(Index_name).length();
	    long DBSIZE  = new File(DB_name).length();
	    MappedByteBuffer idx = idx_chan.map(FileChannel.MapMode.READ_ONLY, 0, IDXSIZE);
	    MappedByteBuffer db = db_chan.map(FileChannel.MapMode.READ_ONLY, 0, DBSIZE);
	    long n_entries = idx.getInt(0);
	    if(i < 0 || n_entries < i){
	    	System.out.println("out of range!!");
	    	throw new IOException();
	    }
	     return neighbors(idx, db, i);
	}
	private int[] neighbors(MappedByteBuffer idx, MappedByteBuffer db, int i) {
	    idx.position((i + 1) * INTEGER_SIZE);
	    int off1 = idx.getInt(), off2 = idx.getInt();
//	    System.out.println("off1 = " + off1 + ", off2 = " + off2);
	    db.position(off1);
	    int[] neighbors;
	    if(off1 == off2){
	    	neighbors = new int[1];
	    	neighbors[0] = 0;
	    }else{
	    	neighbors = new int[off2/4 - off1/4];
	    	for (int j = 0; j < neighbors.length; j++) {
	    		neighbors[j] = db.getInt();
//	      System.out.println("neighbors["+j+"] = "+neighbors[j]);
	    	}
	    }
	   	return neighbors;
	  }
	public int[][] load() throws IOException{
		FileChannel idx_chan = new RandomAccessFile(Index_name, "r").getChannel();
	    FileChannel db_chan = new RandomAccessFile(DB_name, "r").getChannel();
	    long IDXSIZE = new File(Index_name).length();
	    long DBSIZE  = new File(DB_name).length();
	    MappedByteBuffer idx = idx_chan.map(FileChannel.MapMode.READ_ONLY, 0, IDXSIZE);
	    MappedByteBuffer db = db_chan.map(FileChannel.MapMode.READ_ONLY, 0, DBSIZE);
	    
	    long n_entries = idx.getInt(0);
	    System.out.println("#entries = " + n_entries);
	    Vector<int[]> vec = new Vector<int[]>();
	    for (int i = 0; i < n_entries; i++) {
	      int[] neighbors = neighbors(idx, db, i);
	      vec.add(neighbors);
	    }
	    
	    idx_chan.close();
	    db_chan.close();
	    return vec.toArray(new int[1][1]);		
	}
}
