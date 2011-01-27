package jp.ac.titech.is.socialnet.clustering.HN;

import java.io.IOException;

import jp.ac.titech.is.socialnet.clustering.DB_Handler;
import jp.ac.titech.is.socialnet.clustering.Parameters;

public class Analyze extends jp.ac.titech.is.socialnet.clustering.Analyze {
	public static final boolean DEBUG = true;
	
	public static void main(String[] args) {
		Parameters params = parseArgs(args);
		
		if (DEBUG)
			System.out.printf("database path: %s\ncluster path: %s\nhistory path: %s\n",
					params.graphPath(), params.clusterPath(), params.historyPath());
		
		DB_Handler graphDB =
			new DB_Handler(params.graphPath());
		
		try {
			CommunityHandler communityStructureAnalyser = new CommunityHandler(graphDB.load());
			int[][] clusters = communityStructureAnalyser.run();
			
			System.out.println("#clusters = " + clusters.length);
			new DB_Handler(params.clusterPath(), graphDB.MAX_ID * 2).make_DB(clusters);
			
			new DB_Handler(params.historyPath(), graphDB.MAX_ID).make_dQ_DB(communityStructureAnalyser.getHistory());
		} catch (IOException e) {
			System.err.println("Fatal error due to I/O exception.");
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
