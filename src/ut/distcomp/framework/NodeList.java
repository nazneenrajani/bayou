package ut.distcomp.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NodeList{
	ConnectionMatrix connections;
	ProcessId[] nodes;
	static int maxNum=10; // TODO read from config? or pass from controller?
	
	public NodeList(){
		connections = new ConnectionMatrix(maxNum);
		nodes = new ProcessId[maxNum];
		for(int i=0;i<nodes.length;i++)
			nodes[i]=null;
	}
	
	public void add(int i){
		nodes[i]=new ProcessId("node:"+i);
		connections.addNode(i);
		//TODO spawn new thread for node and add to env
	}
	
	public void remove(int i){
		nodes[i]=null;
		connections.removeNode(i);
	}

	public ProcessId getProcessId(Integer i) {
		return nodes[i];
	}
}