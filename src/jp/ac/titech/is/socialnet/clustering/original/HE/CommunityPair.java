package jp.ac.titech.is.socialnet.clustering.original.HE;

enum MAX_FLAG{
	I_IS_MAX,
	J_IS_MAX,
	BOTH_MAX,
	NOT_BOTH_MAX
}

public class CommunityPair implements Comparable<CommunityPair>{
    static int id_allocater = 0;
	int id;
	MAX_FLAG maxFlag = MAX_FLAG.NOT_BOTH_MAX;
	Community i, j;
	//int id_i, id_j;
	//int a_i, a_j;
    CommunityPair nexti, nextj, prebi, prebj;
    long DeltaQ;
    double DeltaQR;
    public static double calcRatio(int size_i, int size_j){
    	return Math.min(((double)size_i)/((double)size_j), ((double)size_j)/((double)size_i));
    	//return Math.sqrt(Math.min(((double)size_i)/((double)size_j), ((double)size_j)/((double)size_i)));
    }
    public static double calcDeltaQR(long deltaQ, double ratio){
    	return deltaQ * ratio;
    }
    public static double calcDeltaQR(long deltaQ, int size_i, int size_j){
    	return calcDeltaQR(deltaQ, calcRatio(size_i, size_j));
    }
    private double calcRatio(){
    	return Math.min(((double)i.size)/((double)j.size), ((double)j.size)/((double)i.size));
    	//return Math.sqrt(Math.min(((double)i.size)/((double)j.size), ((double)j.size)/((double)i.size)));
    }
    public double getCalcDeltaQR(){
    	return DeltaQ * calcRatio();
    }
    
    public void setDeltaQR(){
    	DeltaQR = getCalcDeltaQR();
    }
    
    public int compareTo(CommunityPair pair){
    	//long diff = DeltaQ - pair.DeltaQ;
		//double diff = DeltaQ/Math.max(((double)i.a)/((double)j.a), ((double)j.a)/((double)i.a)) - pair.DeltaQ/Math.max(((double)pair.i.a)/((double)pair.j.a), ((double)pair.j.a)/((double)pair.i.a));
		//double diff = max.DeltaQ/(2.0 + (double)max.i.a/(double)max.j.a + (double)max.j.a/(double)max.i.a) - com.max.DeltaQ/(2.0 + (double)com.max.i.a/(double)com.max.j.a + (double)com.max.j.a/(double)com.max.i.a);
		//double diff = max.DeltaQ/(Math.max(max.i.a,max.j.a)/(double)(max.j.a + max.i.a)) - com.max.DeltaQ/(Math.max(com.max.i.a,com.max.j.a)/(double)(com.max.j.a+com.max.i.a));
    	//double diff = DeltaQ/Math.max(((double)i.size)/((double)j.size), ((double)j.size)/((double)i.size)) - pair.DeltaQ/Math.max(((double)pair.i.size)/((double)pair.j.size), ((double)pair.j.size)/((double)pair.i.size));
    	//double diff = DeltaQ * calcRatio() - pair.DeltaQ*pair.calcRatio();
    	double diff = DeltaQR - pair.DeltaQR;
    	//		CommunityPair current = i.first;
//    	int size_i = 0, size_j = 0;
//    	while(current != null){
//    		size_i++;
//    		current = current.next(i);
//    	}
//    	current = j.first;
//    	while(current != null){
//    		size_j++;
//    		current = current.next(j);
//    	}
//    	assert i.size == size_i && j.size == size_j;
//    	
//    	size_i = 0;
//    	size_j = 0;
//    	current = pair.i.first;
//    	while(current != null){
//    		size_i++;
//    		current = current.next(pair.i);
//    	}
//    	current = pair.j.first;
//    	while(current != null){
//    		size_j++;
//    		current = current.next(pair.j);
//    	}
//    	assert pair.i.size == size_i && pair.j.size == size_j;
//    	
//    	
    	
    	
		if(diff < 0.0){
			return -1;
		}else if(diff > 0.0){
			return 1;
		}else{
			return id - pair.id;
		}
    }
    
    
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
    			System.out.println("maxFlag = " + maxFlag);
    			System.out.println("i = " + i);
    			System.out.println("j = " + j);
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
    			System.out.println("maxFlag = " + maxFlag);
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
    		System.out.println("com.id = " + com.id);
    		System.out.println("i.id = " + i.id);
    		System.out.println("j.id = " + j.id);
    		System.out.println("com.size = " + com.size);
    		System.out.println("i.size = " + i.size);
    		System.out.println("j.size = " + j.size);
    	}
    	assert com == i || com == j;
    	if(com == i){
    		return j;
    	}else{
    		return i;
    	}
    }
    
    CommunityPair(Community i, Community j, long DeltaQ) {
    	this.id = id_allocater++;
        this.i = i;
        this.j = j;
        //id_i = i.id;
        //id_j = j.id;
        //a_i = i.a;
        //a_j = j.a;
        this.DeltaQ = DeltaQ;
    }

    CommunityPair(Community i, Community j, long DeltaQ, double deltaQR) {
    	this.id = id_allocater++;
        this.i = i;
        this.j = j;
        this.DeltaQ = DeltaQ;
        this.DeltaQR = deltaQR;
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

