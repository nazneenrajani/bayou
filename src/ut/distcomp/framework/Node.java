package ut.distcomp.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Node extends Process{
	int node_id;
	int CSN; int OSN;
	Map<Integer,Integer> version_vector = new HashMap<Integer,Integer>();
	Map<Integer,Integer> older_version_vector = new HashMap<Integer,Integer>();
	Set<Write> tentativeWrite = new TreeSet<Write>();
	Set<Write> committedWrite = new TreeSet<Write>();
	//List<Write> log;
	PlayList db;
	PlayList old_db;	
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

	public void truncate_log(PlayList currentPL, HashMap versionVector){
		//TODO: remove logs that are stable
		 old_db = currentPL;
		 older_version_vector=version_vector;
	}

	public void anti_entropy(ProcessId R, HashMap<Integer,Integer> versionVector, int csn){
		if(OSN>csn)
			databaseTransfer(R);
		if(csn<CSN){
			Iterator<Write> it = committedWrite.iterator(); 
			while(it.hasNext()){
				Write cw = it.next();
				if(cw.CSN>csn){
					if(cw.accept_stamp<=versionVector.get(cw.serverID))
						sendMessage(R,new sendCommitNotification(me, cw.accept_stamp, cw.serverID, cw.CSN));
					else
						sendMessage(R, new sendWrite(me, cw));
				}

			}
		}
		Iterator<Write> it = tentativeWrite.iterator(); 
		while(it.hasNext()){
			Write tw = it.next();
			if(versionVector.get(tw.serverID)<tw.accept_stamp)
				sendMessage(R, new sendWrite(me, tw));
		}

	}

	private void databaseTransfer(ProcessId R) {
		//TODO: roll back to previous state
		sendMessage(R, new sendDB(me, old_db));
		sendMessage(R, new sendVector(me, older_version_vector));
		sendMessage(R, new sendCSN(me, CSN));
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
