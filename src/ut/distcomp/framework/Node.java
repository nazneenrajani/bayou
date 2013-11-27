package ut.distcomp.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node extends Process{
	int node_id;
	int CSN; 
	Map<Integer,Integer> version_vector = new HashMap<Integer,Integer>();
	List<Write> log;
	Boolean exitFlag = false;

	public Node(Env env, ProcessId me, int nodeId){
		this.node_id=nodeId;
		this.me = me;
		this.env = env;
		env.addProc(me, this);
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
		//TODO stop accepting client requests
		//TODO: transfer db and leave
		exitFlag=true;
	}
	
	public void printLog(){
		//TODO print log		
	}
	
	@Override
	void body() {
		System.out.println("Here I am: " + me);
		
		for(ProcessId nodeid: env.Nodes.nodes){
			sendMessage(nodeid, new askAntiEntropyInfo(me));
		}
		while(!exitFlag){
			BayouMessage m = getNextMessage();
			if(m instanceof sendAntiEntropyInfo){
				sendAntiEntropyInfo msg = (sendAntiEntropyInfo) m;
				anti_entropy(msg.src,msg.versionVector,msg.CSN);
			}
			else if(m instanceof RetireMessage){
				retire();
			}
			else if(m instanceof PrintLogMessage){
				printLog();
			}
		}
		env.Nodes.remove(node_id); //TODO rename node_id to my_id
		env.connections.isolate(node_id);
	}
}