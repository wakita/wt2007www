package jp.ac.titech.is.socialnet.clustering.original.CNM;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.IOException;

//import java.util.BitSet;

public class CommunityHandler {
	int[][] link_arr;
    Queue<long[]> merge_history = new LinkedList<long[]>();
    SortedSet<Community> pqueue = new TreeSet<Community>();
    Queue<Community> soloCommunities = new LinkedList<Community>();
    double Q;
    int[] a;
    
	public CommunityHandler(int[][] link_arr){
		this.link_arr = link_arr;
	}
	
	private void initializedQ(){
		//TODO initialized Q
		Q = 0.0;
	}
	
	int[][] run()throws IOException{
		System.out.println("making the network");
		makeNetwork();
		System.out.println("maked the network");
		link_arr = null;
		System.out.println("initialize the Q");
		initializedQ();
		System.out.println("initialized the Q");
		
		//System.out.println(pqueue);
		System.out.println("\nfinding the communities");
		findCommunities();
		System.out.println("found the communities");
		//System.out.println(pqueue);
		//System.out.println(communities);
		return convert_communities2array();
	}
	
	int[][] pqueue2GraphArray(){
		int i = 0;
		int[][] convert_arr = new int[pqueue.size()][];
		for(Community com : pqueue){
			com.id = i++;
		}
		int id = 0;
		for(Community com: pqueue){
			int size = 1;
			CommunityPair pair = com.first;
			while(pair != null){
				size++;
				pair = pair.next(com);
			}
			convert_arr[id] = new int[size];
			int id2 = 0;
			convert_arr[id][id2++] = com.id;
			pair = com.first;
			while(pair != null){
				convert_arr[id][id2++] = pair.destCommunity(com).id;
				pair = pair.next(com);
			}
			id++;
		}
		return  convert_arr;
	}
	
	void save_progress(int count)throws IOException{
		Mixi_DB_Handler progress = new Mixi_DB_Handler("./data/pro_" + count + "link.idx",
														"./data/pro_" + count + "link.db",
														2000000);
		progress.make_DB(convert_pqueue2array());
		progress = new Mixi_DB_Handler("./data/pro_" + count + "DQ.idx",
				"./data/pro_" + count + "DQ.db",
				2000000);
		progress.make_dQ_DB(convert_pqueueDQ2array());
		
	}
	
	long[][] convert_pqueueDQ2array(){
		long[][] convert_arr = new long[pqueue.size()][];
		int id = 0;
		for(Community com: pqueue){
			int size = 0;
			CommunityPair pair = com.first;
			while(pair != null){
				size++;
				pair = pair.next(com);
			}
			convert_arr[id] = new long[size];
			int id2 = 0;
			//convert_arr[id][id2++] = com.id;
			pair = com.first;
			while(pair != null){
				convert_arr[id][id2++] = pair.DeltaQ;
				pair = pair.next(com);
			}
			id++;
		}
		return convert_arr;
	}
	
	int[][] convert_pqueue2array(){
		int[][] convert_arr = new int[pqueue.size() + soloCommunities.size()][];
		int id = 0;
		for(Community com: pqueue){
			int size = 1;
			CommunityPair pair = com.first;
			while(pair != null){
				size++;
				pair = pair.next(com);
			}
			convert_arr[id] = new int[size];
			int id2 = 0;
			convert_arr[id][id2++] = com.id;
			pair = com.first;
			while(pair != null){
				convert_arr[id][id2++] = pair.destCommunity(com).id;
				pair = pair.next(com);
			}
			id++;
		}
		for(Community com: soloCommunities){
			convert_arr[id] = new int[1];
			convert_arr[id][0] = com.id;
			id++;
		}
		return convert_arr;
	}
	
	
	int[][] convert_communities2array(){

		int[][] convert_arr = new int[pqueue.size() + soloCommunities.size()][];
		int id = 0;
		for(Community com: pqueue){
			int size = 1;
			CommunityPair pair = com.first;
			while(pair != null){
				size++;
				pair = pair.next(com);
			}
			convert_arr[id] = new int[size];
			int id2 = 0;
			convert_arr[id][id2++] = com.id;
			pair = com.first;
			while(pair != null){
				convert_arr[id][id2++] = pair.destCommunity(com).id;
				pair = pair.next(com);
			}
			id++;
		}
		for(Community com: soloCommunities){
			convert_arr[id] = new int[1];
			convert_arr[id][0] = com.id;
			id++;
		}
		return convert_arr;
	}
	
	long[][] getHistory(){
		long[][] history = new long[merge_history.size()][6];
		for(int i = 0; i < history.length; i++){
			long[] merge_pair = merge_history.poll();
			history[i][0] = merge_pair[0];//id_i
			history[i][1] = merge_pair[1];//id_j
			history[i][2] = merge_pair[2];//merged_id
			history[i][3] = merge_pair[3];//node_num_i
			history[i][4] = merge_pair[4];//node_num_j
			history[i][5] = merge_pair[5];//deltaQ
		}
		return history;
	}
	
	private void findCommunities() throws IOException{
		int count = 1;
		//System.out.println("pqueue.size = " + pqueue.size());
		Community maxCommunity = pqueue.last();
		CommunityPair max_pair = maxCommunity.max;
		Community destCommunity = max_pair.destCommunity(maxCommunity);
		//System.out.println("pqueue = " + pqueue);
		long t1, t2, startTime;
		Community mergedCommunity;
		int sameCount = 0;
		startTime = t1 = System.currentTimeMillis();
		while(max_pair.DeltaQ > 0){
			if(count %10000 == 0){
				t2 = System.currentTimeMillis();
				
				System.out.println("count = " + count + " Time = " + ((t2 - t1)/1000.0) + " elapsed Time  = " + ((t2-startTime)/1000.0));
				t1 = t2;
				System.out.flush();
			}
			/*if(count % 100000 == 0){
				save_progress(count);
			}*/
			
			pqueue.remove(maxCommunity);
			pqueue.remove(destCommunity);
			mergedCommunity = merge(maxCommunity, destCommunity);
			//System.out.println(pqueue);
			if(pqueue.isEmpty()) break;
			maxCommunity = pqueue.last();
			if(maxCommunity == mergedCommunity){
				sameCount++;
				//if(sameCount%100==0)System.out.println("sameCount = " + sameCount);
				//System.out.println(maxCommunity.max.destCommunity(maxCommunity).size);
				System.out.flush();
				
				
			}else{
				//System.out.println("sameCount = " + sameCount);
				//System.out.flush();
				//mergedCommunity.attachTail();
				sameCount=0;
			}

			max_pair = maxCommunity.max;
			destCommunity = max_pair.destCommunity(maxCommunity);
			//System.out.println("\npqueue = " + pqueue);
			count++;
		}
		long finishTime = System.currentTimeMillis();
		System.out.println("Total Time = " +((finishTime - startTime)/1000.0));
		System.out.println("pqueue size = " + pqueue.size());
		System.out.println("solo size = " + soloCommunities.size());
		//save_progress(count);
	}
	
	private Community merge(Community i, Community j){
		//System.out.println("i =  " + i + " j = " + j);
		CommunityPair main_neighbor = i.first;
		CommunityPair absorbed_neighbor = j.first;
		Community merged_community = new Community(i.a + j.a, i.node_num+ j.node_num);
		Community main_dest = main_neighbor.destCommunity(i);
		Community absorbed_dest = absorbed_neighbor.destCommunity(j);

		//System.out.println(i.id +" "+j.id +" " + merged_community.id +" "+ i.node_num +" " +j.node_num+" "+ i.max.DeltaQ);
		long[] history = {i.id, j.id, merged_community.id, i.node_num, j.node_num, i.max.DeltaQ};//id_i, id_j, merged_community.id,node_num_i, node_num_j, dq;
		merge_history.offer(history);
		j.max.releaseMaxFlag(j);
		
		//System.out.println(i);System.out.println(j);
		long maxDelta = Long.MIN_VALUE;
		CommunityPair maxPair = null;
		
		//i.max.releaseMaxFlag(i);
		while(main_neighbor != null && absorbed_neighbor != null){
			main_dest = main_neighbor.destCommunity(i);
			absorbed_dest = absorbed_neighbor.destCommunity(j);
			
			if(main_dest == j){
				main_neighbor = main_neighbor.next(i);
				continue;
			}
			if(absorbed_dest == i){
				absorbed_neighbor = absorbed_neighbor.next(j);
				continue;
			}
			int relation = main_dest.compareID(absorbed_dest);
			if(relation == 0){
				main_dest.remove(main_neighbor);
				main_dest.remove(absorbed_neighbor);
				
				//todo update dQ
				long delta = main_neighbor.DeltaQ + absorbed_neighbor.DeltaQ;
				CommunityPair next_main_neighbor = main_neighbor.next(i);
				CommunityPair next_absorbed_neighbor =  absorbed_neighbor.next(j);
				if(maxDelta <= delta){
					maxPair = main_neighbor;
					maxDelta = delta;
				}
				
				//todo update pqueue
				if(i.destIsMax(main_neighbor) || 
					j.destIsMax(absorbed_neighbor) || 
					main_dest.max.DeltaQ <= delta){
					
					main_dest.max.releaseMaxFlag(main_dest);
					pqueue.remove(main_dest);
					main_neighbor.setCommunity(i, merged_community);
					main_neighbor.DeltaQ = delta;
					main_dest.addLast(main_neighbor);
					merged_community.addLast(main_neighbor);
					main_dest.max = main_neighbor;
					main_neighbor.setMaxFlag(main_dest);
					pqueue.add(main_dest);
				}else{
					main_neighbor.setCommunity(i, merged_community);
					main_neighbor.DeltaQ = delta;
					main_dest.addLast(main_neighbor);
					merged_community.addLast(main_neighbor);
				}
				
				main_neighbor = next_main_neighbor;
				absorbed_neighbor = next_absorbed_neighbor;
				continue;
			}else if(relation < 0){
				//todo update dQ
				long delta = main_neighbor.DeltaQ - 2 * main_dest.a * j.a;
				CommunityPair next_main_neighbor = main_neighbor.next(i);
				main_dest.remove(main_neighbor);
				if(maxDelta <= delta){
					maxPair = main_neighbor;
					maxDelta = delta;
				}
				//todo update pqueue
				if(i.destIsMax(main_neighbor)){
					//too slow serach for max
					pqueue.remove(main_dest);
					main_neighbor.setCommunity(i, merged_community);
					main_neighbor.DeltaQ = delta;
					main_dest.addLast(main_neighbor);
					merged_community.addLast(main_neighbor);
					main_dest.initMax();
					pqueue.add(main_dest);
				}else{
					main_neighbor.setCommunity(i, merged_community);
					main_neighbor.DeltaQ = delta;
					main_dest.addLast(main_neighbor);
					merged_community.addLast(main_neighbor);
				}
				main_neighbor = next_main_neighbor;
				continue;
			}else{
				//todo update dQ
				long delta = absorbed_neighbor.DeltaQ - 2 * absorbed_dest.a * i.a;
				CommunityPair next_absorbed_neighbor =  absorbed_neighbor.next(j);
				absorbed_dest.remove(absorbed_neighbor);
				if(maxDelta <= delta){
					maxPair = absorbed_neighbor;
					maxDelta = delta;
				}
				
				if(j.destIsMax(absorbed_neighbor)){
					//too slow serach for max
					pqueue.remove(absorbed_dest);
					absorbed_neighbor.setCommunity(j, merged_community);
					absorbed_neighbor.DeltaQ = delta;
					absorbed_dest.addLast(absorbed_neighbor);
					merged_community.addLast(absorbed_neighbor);
					absorbed_dest.initMax();
					pqueue.add(absorbed_dest);
				}else{
					absorbed_neighbor.setCommunity(j, merged_community);
					absorbed_neighbor.DeltaQ = delta;
					absorbed_dest.addLast(absorbed_neighbor);
					merged_community.addLast(absorbed_neighbor);
				}
				absorbed_neighbor = next_absorbed_neighbor;
				//todo update pqueue
				continue;
			}
		}
		while(main_neighbor != null){
			main_dest = main_neighbor.destCommunity(i);
			if(main_dest == j){
				main_neighbor = main_neighbor.next(i);
				continue;
			}
			//todo update dQ

			long delta = main_neighbor.DeltaQ - 2 * main_dest.a * j.a;
			CommunityPair next_main_neighbor = main_neighbor.next(i);
			main_dest.remove(main_neighbor);
			if(maxDelta <= delta){
				maxPair = main_neighbor;
				maxDelta = delta;
			}
			
			if(i.destIsMax(main_neighbor)){
				//too slow serach for max
				pqueue.remove(main_dest);
				main_neighbor.setCommunity(i, merged_community);
				main_neighbor.DeltaQ = delta;
				main_dest.addLast(main_neighbor);
				merged_community.addLast(main_neighbor);
				main_dest.initMax();
				pqueue.add(main_dest);
			}else{
				main_neighbor.setCommunity(i, merged_community);
				main_neighbor.DeltaQ = delta;
				main_dest.addLast(main_neighbor);
				merged_community.addLast(main_neighbor);
			}
			main_neighbor = next_main_neighbor;
			//todo update pqueue
		}
		
		while(absorbed_neighbor != null){
			absorbed_dest = absorbed_neighbor.destCommunity(j);
			if(absorbed_dest == i){
				absorbed_neighbor = absorbed_neighbor.next(j);
				continue;
			}
//			todo update dQ
			long delta = absorbed_neighbor.DeltaQ - 2 * absorbed_dest.a * i.a;
			CommunityPair next_absorbed_neighbor =  absorbed_neighbor.next(j);
			absorbed_dest.remove(absorbed_neighbor);
			if(maxDelta <= delta){
				maxPair = absorbed_neighbor;
				maxDelta = delta;
			}
			
			if(j.destIsMax(absorbed_neighbor)){
				//too slow serach for max
				pqueue.remove(absorbed_dest);
				absorbed_neighbor.setCommunity(j, merged_community);
				absorbed_neighbor.DeltaQ = delta;
				absorbed_dest.addLast(absorbed_neighbor);
				merged_community.addLast(absorbed_neighbor);
				absorbed_dest.initMax();
				pqueue.add(absorbed_dest);
			}else{
				absorbed_neighbor.setCommunity(j, merged_community);
				absorbed_neighbor.DeltaQ = delta;
				absorbed_dest.addLast(absorbed_neighbor);
				merged_community.addLast(absorbed_neighbor);
			}
			absorbed_neighbor = next_absorbed_neighbor;
		}
		merged_community.node_num = i.node_num + j.node_num;
		if(merged_community.first != null){
			merged_community.node_num = i.node_num + j.node_num;
			maxPair.setMaxFlag(merged_community);
			merged_community.max = maxPair;
			//merged_community.a = merged_community.size;
			pqueue.add(merged_community);
			
		}else{
			soloCommunities.add(merged_community);
		}
		return merged_community;
	}
	private void makeNetwork() {
        int[][] network = link_arr;
        int network_distance = network.length;
        Vector<Community> communities = new Vector<Community>(network_distance);
        a = new int[network.length];
        
        // Number of edges
        long m = 0;
        for (int id = 0; id < network_distance; id++) {
            //System.out.println("id = " + id);
            //System.out.println("network[id][0] = " + network[id][0]);
            
        	if (network[id][0] != 0) {
                m += network[id].length;
                communities.add(id, new Community(network[id].length, 1));
            }else{
            	communities.add(id, null);
            }
        }
        m /= 2;
        for (int id = 0; id < network_distance; id++) {
        	if(network[id][0] == 0){
        		continue;
        	}
        	if((network[id][0] == id+1) && network[id].length == 1){
        		communities.set(id, null);
        		continue;
        	}
            //a[id] = network[id].length / (2 * m);
        	a[id] = network[id].length;
            Community current_community = communities.get(id);
            int[] neighbors = network[id];
            //if(id % 10000 == 0)System.out.println(id);
            for (int id2 = neighbors.length - 1;  id2 >= 0 && id < (neighbors[id2] - 1); id2--) {	
            	if(id + 1 == neighbors[id2]){
            		continue;
            	}
            	long deltaQ = 2 * m - network[id].length * network[neighbors[id2]-1].length;
                Community destCommunity = communities.get(neighbors[id2]-1);
            	CommunityPair pair = new CommunityPair(current_community, destCommunity, deltaQ);
                current_community.add(pair);
                destCommunity.add(pair);
            }
        }
        @SuppressWarnings("unused")
		int count = 1;
        for(Community com : communities){
		//System.out.println(count++);
        	if(com != null){
	        	com.initMax();
        	}
        }
        count=1;
        for(Community com : communities){
        	if(com != null){
        		//com.initMax();
        		//if(count % 10000 == 0)System.out.println(count);count++;
        		//if(count== 8553)System.out.println(com.max.destCommunity(com).max.DeltaQ);
        		pqueue.add(com);
        	}
        }
    }
	
	public void repairLinkInfo(){
		int current_id = 1;
		for(int[] link : link_arr){
			for(int id : link){
				//System.out.println("id= " + id + " ,current= " + current_id);
				if(id <= 0){
					continue;
				}
				if(!is_connect(id, current_id)){
					insert(id, current_id);
				}
			}
			current_id++;
		}
	}
	
 	private boolean is_connect(int i, int j){
 		if(i <= 0){
 			return false;
 		}
 		int[] row = link_arr[i-1];
 		for(int id : row){
 			if(id == j){
 				return true;
 			}
 			if(id > j){
 				return false;
 			}
 		}
 		return false;
 	}
 	
	private void insert(int target, int injection_id){
		int[] friend_list = link_arr[target-1];
		int[] repair_list = new int[friend_list.length + 1];
		int i = 0;
		boolean flag = false;
		
		if(friend_list[0] == Mixi_DB_Handler.NULL_NUM){
			repair_list = new int[1];
			repair_list[0] = injection_id;
		}else{
			for(int friend_id : friend_list){
				if(friend_id < injection_id || flag){
					repair_list[i] = friend_id;
				}else{
					repair_list[i] = injection_id;
					i++;
					repair_list[i] = friend_id;
					flag = true;
				}
				i++;
			}
			if(!flag){
				repair_list[i] = injection_id;
			}
		}
		
		link_arr[target-1] = repair_list;
	}
 	
}
