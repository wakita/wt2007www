package jp.ac.titech.is.socialnet.clustering.original.HE;

import java.io.*;
public class GraphvizHandler {
	String dirPath, dotName;
	int[][] linkInfo;
	GraphvizHandler(String dirPath, String dotName, int[][] linkInfo){
		this.dirPath = dirPath;
		this.dotName = dotName;
		this.linkInfo = linkInfo;
	}
	public void makeGraphFile()throws IOException{
		File dotFile = new File(dirPath + dotName);
		BufferedWriter output = new BufferedWriter(new FileWriter(dotFile));
		StringBuffer buffer = new StringBuffer();
		//String text = "digraph G {\n\tedge[arrowhead=none]\n";
		String text = "Graph{\n ### metadata ### ;\n\n ;\n";
		int numNode = linkInfo.length;
		int numLink = 0;
		
		text += " " + numNode + ";\n";
		
		for(int[] neighbor : linkInfo){
			numLink += neighbor.length -1;
		}
		assert numLink % 2 ==0;
		numLink /= 2;
		text += " " + numLink + ";\n";
		text += " ;\n ;\n ### structural data ###\n\n [\n";
		buffer.append(text);
		for(int i = 0; i < linkInfo.length; i++){
			int[] neighbor = linkInfo[i];
			if(neighbor.length == 1){
				continue;
			}
			int id = neighbor[0];
			buffer.append("\t\t{ " + id + "; " + neighbor[1] + "; }");
			for(int j = 2; j < neighbor.length; j++){
				if(id < neighbor[j]){
					buffer.append(",\n\t\t{" + id + " ; " + neighbor[j] + "; }");
				}
			}
			//output.append(buffer);
		}
		buffer.append("\n\t];\n;\n");
		output.append(buffer);
		buffer.append("\n ### attribute data ###\n ;\n [\n  {\n   $root;\n   bool;\n   || false ||;\n   [\n    { 2064; T; }\n   ];\n   ;\n   ;\n  },\n  {\n   $tree_link;\n   bool;\n   || false ||;\n   ;\n   ]\n");
		
		output.flush();
	}
}
