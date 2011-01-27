package jp.ac.titech.is.socialnet.clustering.original.CNM;

enum MAX_FLAG{
	I_IS_MAX,
	J_IS_MAX,
	BOTH_MAX,
	NOT_BOTH_MAX
}

public class CommunityPair{
    //static int id_allocater = 0;
	//int id;
	MAX_FLAG maxFlag = MAX_FLAG.NOT_BOTH_MAX;
	Community i, j;
	//int id_i, id_j;
	//int a_i, a_j;
    CommunityPair nexti, nextj, prebi, prebj;
    long DeltaQ;
    
    public  void setMaxFlag(Community com){
    	assert com == i || com == j;
    	if(com == i){
    		switch(maxFlag){
    		case J_IS_MAX:
    			maxFlag = MAX_FLAG.BOTH_MAX;
    			return;
    		case NOT_BOTH_MAX:
    			maxFlag = MAX_FLAG.I_IS_MAX;
    			return;
    		default:
    			return;
    			/*System.out.println(maxFlag);
    			System.out.println(com);
    			assert false;*/
    		}
    	}else{
    		switch(maxFlag){
    		case I_IS_MAX:
    			maxFlag = MAX_FLAG.BOTH_MAX;
    			return;
    		case NOT_BOTH_MAX:
    			maxFlag = MAX_FLAG.J_IS_MAX;
    			return;
    		default:
    			return;
    			/*System.out.println(maxFlag);
    			assert false;*/
    		}
    	}
    }
    
    public  void releaseMaxFlag(Community com){
    	assert com == i || com == j;
    	if(com == i){
    		switch(maxFlag){
    		case I_IS_MAX:
    			maxFlag = MAX_FLAG.NOT_BOTH_MAX;
    			return;
    		case BOTH_MAX:
    			maxFlag = MAX_FLAG.J_IS_MAX;
    			return;
    		default:
    			System.out.println(maxFlag);
    			System.out.println(i);
    			System.out.println(j);
    			assert false;
    		}
    	}else{
    		switch(maxFlag){
    		case J_IS_MAX:
    			maxFlag = MAX_FLAG.NOT_BOTH_MAX;
    			return;
    		case BOTH_MAX:
    			maxFlag = MAX_FLAG.I_IS_MAX;
    			return;
    		default:
    			System.out.println(maxFlag);
    			assert false;
    		}
    	}
    }
    /*
    int compareID(Community com){
    	assert com == i || com == j;
    	if(com == i){
    		return j;
    	}else{
    		return i;
    	}
    }
    
    int getDestA(Community com){
    	assert com.id == id_i || com.id == id_j;
    	if(com.id == id_i){
    		return a_j;
    	}else{
    		return a_i;
    	}
    }
    
    int destCommunityId(Community com){
    	assert com == i || com == j;
    	if(com == i){
    		return j;
    	}else{
    		return i;
    	}
    }*/
    
    Community destCommunity(Community com){
    	if(com != i && com != j){
    		System.out.println(com.id);
    		System.out.println(i.id);
    		System.out.println(j.id);
    	}
    	assert com == i || com == j;
    	if(com == i){
    		return j;
    	}else{
    		return i;
    	}
    }
    
    CommunityPair(Community i, Community j, long DeltaQ) {
    	//this.id = id_allocater++;
        this.i = i;
        this.j = j;
        //id_i = i.id;
        //id_j = j.id;
        //a_i = i.a;
        //a_j = j.a;
        this.DeltaQ = DeltaQ;
    }

    public CommunityPair next(Community com){	
    	assert com == i || com == j;
    	return com == i ? nexti : nextj;
    }

    public void setCommunity(Community target, Community com){
		assert target == i || target == j;
		if(target == i){
			i = com;
			//id_i = com.id;
			//a_i = com.a;
		}else{
			j = com;
			//id_j = com.id;
			//a_j = com.a;
		}
	}


    public CommunityPair preb(Community com){
    	assert com == i || com == j;
    	return com == i ? prebi : prebj;
    }
    
    public CommunityPair getNext(Community com){
    	assert com == i || com == j;
    	if(com == i){
    		return nexti;
    	}else{
    		return nextj;
    	}
    }
   
    public CommunityPair getPreb(Community com){
    	assert com == i || com == j;
    	if(com == i){
    		return prebi;
    	}else{
    		return prebj;
    	}
    }
    
    public void setNext(Community com, CommunityPair pair){
    	assert com == i || com == j;
    	if(com == i){
    		nexti = pair;
    	}else{
    		nextj = pair;
    	}
    }
    
    public void setPreb(Community com, CommunityPair pair){
    	assert com == i || com == j;
    	if(com == i){
    		prebi = pair;
    	}else{
    		prebj = pair;
    	}
    }
    
    public String toString(){
    	return "(i,j) = (" + i + "," + j + ") dQ = " + DeltaQ;
    }
    
    
}

