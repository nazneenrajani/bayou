package ut.distcomp.framework;

import java.util.ArrayList;
import java.util.List;

public class Node extends Thread{
	int node_id;
	List<updates> log;
	static PlayList db;
	static List<Boolean> connections;
	
	public Node(int nodeId){
		this.node_id=nodeId;
		connections=new ArrayList<Boolean>();
	}
	
	public void add_entry(){
		//TODO: add to log, called by controller
	}
	
	public void truncate_log(){
		//TODO: remove logs that are stable
	}
	
	public void anti_entropy(Node node){
		//TODO: gossip protocol
	}
	
	public void retire(){
		//TODO: transfer db and leave
	}
}
