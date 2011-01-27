package jp.ac.titech.is.socialnet.clustering.original.HE;

import java.lang.Comparable;

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
		//System.out.println("pair.i = " + pair.i.id + " pair.j = " + pair.j.id + "pair.id _= "  + id);
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
		//System.out.println(Integer.toString(id));
		CommunityPair current = first;
		//System.out.println("pqueueid = " + id);
		String str = "[ ";
		//System.out.print(id + " " + size);
		while(current != null){
			//str += current.DeltaQ + ", ";
			//str += current.destCommunity(this).id + new Boolean(isMax(current)).toString() + ", ";
			//str += current.destCommunity(this).id + " dq = " + current.DeltaQR+", ";
			/*if(isMax(current)){
				str += "max ";
			}*/
			str += current.destCommunity(this).id +", ";
			current = current.next(this);
		};
		str += "] ";

		//System.out.println(str);
		return  Integer.toString(id) + " " +this.size + str;
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
//		long diff = max.DeltaQ - com.max.DeltaQ;
		//double diff = max.DeltaQ/Math.max(((double)max.i.a)/((double)max.j.a), ((double)max.j.a)/((double)max.i.a)) - com.max.DeltaQ/Math.max(((double)com.max.i.a)/((double)com.max.j.a), ((double)com.max.j.a)/((double)com.max.i.a));
		//double diff = max.DeltaQ/(2.0 + (double)max.i.a/(double)max.j.a + (double)max.j.a/(double)max.i.a) - com.max.DeltaQ/(2.0 + (double)com.max.i.a/(double)com.max.j.a + (double)com.max.j.a/(double)com.max.i.a);
		//double diff = max.DeltaQ/(Math.max(max.i.a,max.j.a)/(double)(max.j.a + max.i.a)) - com.max.DeltaQ/(Math.max(com.max.i.a,com.max.j.a)/(double)(com.max.j.a+com.max.i.a));

		int compare = max.compareTo(com.max);
		
		//System.out.println("id = "+ id + " com.id = " + com.id + " compare = " + compare);
		
		if(compare < 0 || 0 < compare ){
			return compare;
		}else{
			return id - com.id;
		}
		/*if(diff < 0.0){
			return -1;
		}else if(diff > 0.0){
			return 1;
		}else{
			return id - com.id;
		}*/
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
	
	public void initDeltaQR(){
		CommunityPair currentPair = first;
		while(currentPair != null){
			currentPair.setDeltaQR();
			currentPair = currentPair.next(this);
		}
	}
	
	public void initMax(){
		@SuppressWarnings("unused")
		long max_dQ = Long.MIN_VALUE;
		//CommunityPair max_pair = null;
		CommunityPair max_pair = first;
		CommunityPair current_pair = first;
		while(current_pair != null){
			if(max_pair.compareTo(current_pair) < 0){
				max_pair = current_pair;
			}
			current_pair = current_pair.next(this);
		}
		
		
/*		while(current_pair != null){
			if(max_dQ <= current_pair.DeltaQ){
				max_dQ = current_pair.DeltaQ;
				max_pair = current_pair;
			}
			current_pair = current_pair.next(this);
		}*/
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
