package ut.distcomp.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node extends Process{
	int node_id;
	int CSN; 
	Map<Integer,Integer> version_vector = new HashMap<Integer,Integer>();
	List<Write> log;
	PlayList db;
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

	public void anti_entropy(ProcessId R, HashMap<Integer,Integer> versionVector, int csn){
		//TODO: gossip protocol

	}

	public void retire(){
		//TODO: transfer db and leave
	}
	@Override
	void body() {
		for(ProcessId nodeid: env.Nodes.nodes){
			sendMessage(nodeid, new askAntiEntropyInfo(me));
		}
		while(true){
			BayouMessage m = getNextMessage();
			if(m instanceof sendAntiEntropyInfo){
				sendAntiEntropyInfo msg = (sendAntiEntropyInfo) m;
				anti_entropy(msg.src,msg.versionVector,msg.CSN);
			}
		}

	}
}
