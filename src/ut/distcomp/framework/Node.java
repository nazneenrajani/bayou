package ut.distcomp.framework;

import java.util.ArrayList;
import java.util.List;

public class Node extends Process{
	int node_id;
	//List<updates> log;
	static ConnectionMatrix connections;
	
	public Node(int nodeId, ConnectionMatrix connections){
		this.node_id=nodeId;
		this.connections=connections;
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

	@Override
	void body() {
		// TODO Auto-generated method stub
		
	}
}