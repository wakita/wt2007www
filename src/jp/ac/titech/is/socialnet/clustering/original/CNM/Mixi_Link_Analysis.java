package jp.ac.titech.is.socialnet.clustering.original.CNM;

/**
 * 
 */
import java.util.Arrays;
import java.util.Vector;
import java.util.TreeSet;
/**
 * @author Tsurumi
 *
 */
public class Mixi_Link_Analysis {
	/**
	 * @param args
	 */

	@SuppressWarnings("unused")
	private int[][] reconstruct_by_history(int[][] link_arr, int[][] history, int offset){
		int count;
		if(offset > history.length){
			count = 0;
		}else{
			count = history.length - offset;
		}
		Vector<TreeSet<Integer>> vec = new Vector<TreeSet<Integer>>();
		//����
		for(int i = 0; i < link_arr.length; i++){
			TreeSet<Integer> set;
			if(link_arr[i][0] == 0){
				set = new TreeSet<Integer>();
				set.add(0);
				vec.add(i, set);
				continue;
			}
			set = new TreeSet<Integer>();
			set.add(i + 1);
			vec.add(i, set);
		}
		for(int i = 0; i < count; i++){
			TreeSet<Integer> com1 = vec.get(history[i][0] - 1);
			TreeSet<Integer> com2 = vec.get(history[i][1] - 1);
			com1.addAll(com2);
			com2.clear();
			com2.add(0);
		}
		int[][] link_info = new int[vec.size()][];
		for(int i = 0; i < vec.size(); i++){
			Integer[] neighbors = vec.get(i).toArray(new Integer[0]);
			link_info[i] = new int[neighbors.length];
			for(int j = 0; j < neighbors.length; j++){
				link_info[i][j] = neighbors[j];
			}
		}
		return link_info;
	}
	
	@SuppressWarnings("unused")
	private int[][] mixi_sort(String file_name) throws Exception{
		CSV_Handler handle = new CSV_Handler(file_name);
		handle.setMixiFlag(true);
		
		int[][] link_arr = handle.csv2arr();
		//�����N�ԍ���\�[�g
		sortMatrix(link_arr);
		return link_arr;
	}
	private static void sortMatrix(int[][] matrix){
		for(int[] vec : matrix){
			Arrays.sort(vec);
		}
	}
	
	// args = 0:dir_path 1:csv 2:index_name 3:db_name 4:max_id 
	
	public static void main(String[] args) throws Exception{
		@SuppressWarnings("unused")
		Mixi_Link_Analysis mixi = new Mixi_Link_Analysis();
		String dir_path         = args[0];
		@SuppressWarnings("unused")
		String csv_name         = dir_path + args[1];
//		String sorted_csv_name  = args[1];
		@SuppressWarnings("unused")
		String index_name       = dir_path + args[4] + args[2];
		@SuppressWarnings("unused")
		String db_name          = dir_path + args[4] + args[3];
		int max_id             = new Integer(args[4]);
		@SuppressWarnings("unused")
		int[][] link_arr;
		CommunityHandler commuHand;
		Mixi_DB_Handler db_handler;
		//csv��\�[�e�B���O and �w�b�_�[�폜
		System.out.println("sorting!!");
//		link_arr = mixi.mixi_sort(csv_name);
		
		//CSV_Handler handle = new CSV_Handler(sorted_csv_name);
		//�\�[�g���������N��ۑ�
		//handle.arr2csv(link_arr);
//		db_handler = new Mixi_DB_Handler(index_name, db_name, max_id);
		//DB,index�쐬
		System.out.println("making db and index");
//		db_handler.make_DB(link_arr);
//		link_arr = db_handler.load();
		/*System.out.println("arr is");
		for(int[] each_link : link_arr){
			for(int num : each_link){
				System.out.print(num + " ");
			}
			System.out.println();
		}*/
		
//		commuHand = new CommunityHandler(link_arr);
//		System.out.println("repaireing");
//		commuHand.repairLinkInfo();
		
		/*for(int[] each_link : commuHand.link_arr){
			for(int num : each_link){
				System.out.print(num + " ");
			}
			System.out.println();
		}*/
		//store repaired db and index
		
		
//		System.out.println("storeing");
//		db_handler = new Mixi_DB_Handler(dir_path + "repaired_link"+max_id+".idx", dir_path + "repaired_link"+max_id+".db", max_id);
//		System.out.println("making db");
//		db_handler.make_DB(commuHand.link_arr);
		
		//���͊J�n
		db_handler = new Mixi_DB_Handler(dir_path + "repaired_link"+max_id+".idx", dir_path + "repaired_link"+max_id+".db", max_id);
		
		int[][] repaired_link = db_handler.load();
		commuHand = new CommunityHandler(repaired_link);
		int[][] result = commuHand.run();
		System.out.println("size = " + result.length);
		//���ʕۑ�
		db_handler = new Mixi_DB_Handler(dir_path + "result_communities"+max_id+".idx", dir_path + "result_communities"+max_id+".db", max_id*2);
		/*for(int[] each_link : result){
			for(int num : each_link){
				System.out.print(num + " ");
			}
			System.out.println();
		}*/
		
		db_handler.make_DB(result);
		
		System.out.println("result is");
		//int[][] tmp = db_handler.load();
		//GraphvizHandler viz = new GraphvizHandler(dir_path, "result.dot", result);
		//viz.makeDotFile();	
		
		/*for(int[] each_link : result){
			for(int num : each_link){
				System.out.print(num + " ");
			}
			System.out.println();
		}*/
		
		
		//int[][] tmp = db_handler.load();
		
		//����ۑ�
		long[][] history = commuHand.getHistory();
		db_handler = new Mixi_DB_Handler(dir_path + "marge_history"+max_id+".idx", dir_path +"marge_history"+max_id+".db", max_id);
		db_handler.make_dQ_DB(history);
		
		//�R�~���j�e�B��񒊏o
		/*db_handler = new Mixi_DB_Handler("./data/marge_history.idx", "./data/marge_history.db", max_id);
		history = db_handler.load();
		int[][] com_set = mixi.reconstruct_by_history(repaired_link, history, 0);
		//�R�~���j�e�B���ۑ�
		db_handler = new Mixi_DB_Handler("./data/com_member.idx", "./data/com_member.db", max_id);
		db_handler.make_DB(com_set);*/
	}
}
