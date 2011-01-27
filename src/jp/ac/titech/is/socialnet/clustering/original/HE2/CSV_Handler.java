package jp.ac.titech.is.socialnet.clustering.original.HE2;

/**
 * 
 */
import java.io.*; 

import jp.ac.wakhok.tomoharu.csv.CSVLine;
import jp.ac.wakhok.tomoharu.csv.CSVTokenizer;
import java.util.*;
/**
 * @author Tsurumi
 *
 */
public class CSV_Handler {
	private String file_name;
	private boolean MIXI_FLAG = false;
	public CSV_Handler(String file_name) {
		this.file_name = file_name;
	}
	public void setMixiFlag(boolean flag){
		MIXI_FLAG = flag;
	}
	public void setFile_name(String file_name){
		this.file_name = file_name;
	}
	public void arr2csv(int[][] link_arr) throws IOException{
		//�\�[�g�t�@�C��������
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file_name)));
		for(int[] each_link : link_arr){
			CSVLine csvline = new CSVLine();
			for(int str : each_link){
				csvline.addItem(new Integer(str).toString());
			}
			out.println(csvline.getLine());
		}
		out.close();
	}
	
	public int[][] csv2arr_compact() throws IOException{
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file_name), "Shift_JIS"));
			Vector<int[]> vec = new Vector<int[]>();
			int current_id = 1;
			while (in.ready()) {
		        String line = in.readLine();
		        CSVTokenizer csvt = new CSVTokenizer(line);
		        int size = csvt.countTokens();
		        if(MIXI_FLAG){
		        	if (csvt.hasMoreTokens()) {
		        		csvt.nextToken();
		        		size--;
		        	}
		        }
		        int[] line_arr;
		        if(size == 0){
		        	line_arr = new int[1];
		        	line_arr[0] = 0;
		        }else{
		        	Queue<Integer> queue = new LinkedList<Integer>();
		        	while (csvt.hasMoreTokens()) {
		        		String token = csvt.nextToken();
		        		if(!token.equals("")){
		        			Integer content = new Integer(token);
		        			if(content < current_id){
		        				queue.offer(content);
		        			}
		        		}
		        	}
		        	line_arr = new int[queue.size()];
		        	int i = 0;
		        	for(int content : queue ){
		        		line_arr[i] = content;
		        		i++;
		        	}
		        }
		        vec.add(line_arr);
		        current_id++;
			}
			return vec.toArray(new int[1][1]);			
		}catch(FileNotFoundException e){
			System.out.println(file_name + " is Not Found!!");
			throw e;
		}
	}
	
	public int[][] csv2arr() throws IOException{
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file_name), "Shift_JIS"));
			Vector<int[]> vec = new Vector<int[]>();
			while (in.ready()) {
		        String line = in.readLine();
		        CSVTokenizer csvt = new CSVTokenizer(line);
		        int size = csvt.countTokens();
		        if(MIXI_FLAG){
		        	if (csvt.hasMoreTokens()) {
		        		csvt.nextToken();
		        		size--;
		        	}
		        }
		        int[] line_arr;
		        if(size == 0){
		        	line_arr = new int[1];
		        	line_arr[0] = 0;
		        }else{
		        	line_arr = new int[size];
		        	int i = 0;
		        	while (csvt.hasMoreTokens()) {
		        		String token = csvt.nextToken();
		        		if(!token.equals("")){
		        			line_arr[i] = new Integer(token).intValue();
		        			i++;
		        		}
		        	}
		        }
		        vec.add(line_arr);
			}
			return vec.toArray(new int[1][1]);			
		}catch(FileNotFoundException e){
			System.out.println(file_name + " is Not Found!!");
			throw e;
		}
	}
}
