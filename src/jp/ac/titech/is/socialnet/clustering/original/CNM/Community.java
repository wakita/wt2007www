package jp.ac.titech.is.socialnet.clustering.original.CNM;

import java.lang.Comparable;;

public class Community implements Comparable<Community>{
	static int id_allocater = 0;
	CommunityPair max;
	CommunityPair first;
	CommunityPair last;
	int id;
	int a;
	int size = 0;
	int node_num = 1;
	
	public boolean destIsMax(CommunityPair pair){
    	assert this == pair.i || this == pair.j;
    	if(this == pair.i){
    		switch(pair.maxFlag){
    		case J_IS_MAX:
    		case BOTH_MAX:
    			return true;
    		default:
    			return false;
    		}
    	}else{
    		switch(pair.maxFlag){
    		case I_IS_MAX:
    		case BOTH_MAX:
    			return true;
    		default:
    			return false;
    		}
    	}
    }
	
	public boolean isMax(CommunityPair pair){
    	assert this == pair.i || this == pair.j;
    	if(this == pair.i){
    		switch(pair.maxFlag){
    		case I_IS_MAX:
    		case BOTH_MAX:
    			return true;
    		default:
    			return false;
    		}
    	}else{
    		switch(pair.maxFlag){
    		case J_IS_MAX:
    		case BOTH_MAX:
    			return true;
    		default:
    			return false;
    		}
    	}
    }
	
	public Community(int a, int node_num){
		//first = new CommunityPair(null, null, Long.MIN_VALUE);
		this.a = a;
		this.node_num = node_num;
		id = id_allocater++;
	}
	
	public String toString(){
		CommunityPair current = first;
		String str = "[ ";
		while(current != null){
			str += current.DeltaQ + ", ";
			//str += current.destCommunity(this).id + new Boolean(isMax(current)).toString() + ", ";
			//str += current.destCommunity(this).id + " dq = " + current.DeltaQ+", ";
			//str += current.destCommunity(this).id + ", ";
			current = current.next(this);
		}
		str += "] ";
		return  Integer.toString(id) + str;
	}
	
	public void remove(CommunityPair pair){
		size--;
		CommunityPair next = pair.getNext(this);
    	CommunityPair preb = pair.getPreb(this);
    	if(next == null){
    		if(preb == null){
    			first = last = null;
    		}else{
    			preb.setNext(this, null);
    			last = preb;
    		}
    	}else{
    		if(preb == null){
    			next.setPreb(this, null);
    			first = next;
    		}else{
    			preb.setNext(this, next);
    			next.setPreb(this, preb);
    		}
    	}
	}
	
	public int compareID(Community com){
		return id - com.id;
	}
	
	public int compareTo(Community com){
		long diff = max.DeltaQ - com.max.DeltaQ;
//		double diff = max.DeltaQ/Math.max(max.i.a/max.j.a, max.j.a/max.i.a) - com.max.DeltaQ/Math.max(com.max.i.a/com.max.j.a, com.max.j.a/com.max.i.a);
		if(diff < 0.0){
			return -1;
		}else if(diff > 0.0){
			return 1;
		}else{
			return id - com.id;
		}
    }
	
	/*public int updateMax(){
		
	}*/
	
	public void attachTail(){
		CommunityPair current_pair = first;
		while(current_pair != null){
			Community dest = current_pair.destCommunity(this);
			dest.remove(current_pair);
			dest.addLast(current_pair);
			current_pair = current_pair.next(this);
		}
	}
	
	public void addLast(CommunityPair pair){
		size++;
		if(last!=null){
			last.setNext(this, pair);
			pair.setPreb(this, last);
			last = pair;
			pair.setNext(this, null);
		}else{
			first = last = pair;
			pair.setNext(this, null);
			pair.setPreb(this, null);
		}
		
	}
	
	public void initMax(){
		long max_dQ = Long.MIN_VALUE;
		CommunityPair max_pair = null;
		CommunityPair current_pair = first;
		while(current_pair != null){
			if(max_dQ <= current_pair.DeltaQ){
				max_dQ = current_pair.DeltaQ;
				max_pair = current_pair;
			}
			current_pair = current_pair.next(this);
		}
		assert max_pair != null;
		if(max!=null){
			max.releaseMaxFlag(this);
		}
		max = max_pair;
		max.setMaxFlag(this);
	}
	
	public void add(CommunityPair pair){
		size++;
		if(first == null){
			first = pair;
			pair.setNext(this, null);
			pair.setPreb(this, null);
			last = first;
			return;
		}else{
			CommunityPair current = first;
			CommunityPair preb = null;
			Community currentCommunity = first.destCommunity(this);
			Community destCommunity = pair.destCommunity(this);
			while(currentCommunity.compareID(destCommunity) < 0){
				preb = current;
				current = current.next(this);
				if(current == null){
					preb.setNext(this, pair);
					pair.setPreb(this, preb);
					pair.setNext(this, null);
					last = pair;
					return;
				}
				currentCommunity = current.destCommunity(this);
			}
			if(preb != null){
				current.setPreb(this, pair);
				preb.setNext(this, pair);
				pair.setPreb(this, preb);
				pair.setNext(this, current);
			}else{
				current.setPreb(this, pair);
				pair.setNext(this, current);
				pair.setPreb(this, null);
				first = pair;
			}
			return;
		}
		
	}
	
}
