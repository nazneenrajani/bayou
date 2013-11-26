package ut.distcomp.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node extends Process{
	int node_id;
	int CSN; 
	Map<Integer,Integer> version_vector = new HashMap<Integer,Integer>();
	List<Write> log;
	PlayList db;
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
	@Override
	void body() {
		//for
		//sendMessage(me, new askAntiEntropyInfo());
		
		
	}
	
}
