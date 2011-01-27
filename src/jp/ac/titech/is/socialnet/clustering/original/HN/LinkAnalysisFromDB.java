package jp.ac.titech.is.socialnet.clustering.original.HN;

/**
 * @author Tsurumi
 *
 */
public class LinkAnalysisFromDB {
	/**
	 * @param args
	 */

	public static void main(String[] args) throws Exception{
		String dir_path         = args[0];
		int max_id             = new Integer(args[1]);
		CommunityHandler commuHand;
		Mixi_DB_Handler db_handler;
		
		//���͊J�n
		db_handler = new Mixi_DB_Handler(dir_path + "repaired_link"+max_id+".idx", dir_path + "repaired_link"+max_id+".db", max_id);
		
		//int[][] repaired_link = db_handler.load();
		commuHand = new CommunityHandler(db_handler.load());
		int[][] result = commuHand.run();
		System.out.println("size = " + result.length);
		//���ʕۑ�
		db_handler = new Mixi_DB_Handler(dir_path + "result_communities"+max_id+".idx", dir_path + "result_communities"+max_id+".db", max_id*2);

		db_handler.make_DB(result);
		
		
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
