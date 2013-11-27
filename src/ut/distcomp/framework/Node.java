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
	static ConnectionMatrix connections;

	public Node(int nodeId, ConnectionMatrix connections){
		this.node_id=nodeId;
		this.connections=connections;
	}

	public void add_entry(){
		//TODO: add to log, called by controller
	}

	public void truncate_log(PlayList currentPL, HashMap<Integer,Integer> versionVector){
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
			else if(m instanceof askAntiEntropyInfo){
				askAntiEntropyInfo msg = (askAntiEntropyInfo) m;
				sendMessage(msg.src, new sendAntiEntropyInfo(me, version_vector, CSN));
			}
			else if(m instanceof sendCommitNotification){
				sendCommitNotification msg = (sendCommitNotification) m;
				removeTentative(msg.accept_stamp,msg.serverID,msg.CSN);
			}
			else if(m instanceof sendWrite){
				sendWrite msg = (sendWrite) m;
				tentativeWrite.add(msg.w);
			}
			else if(m instanceof sendDB){
				sendDB msg = (sendDB) m;
				//TODO: merge dbs
			}
			else if(m instanceof sendVector){
				sendVector msg = (sendVector) m;
				older_version_vector=msg.vv;
			}
			else if(m instanceof sendCSN){
				sendCSN msg = (sendCSN) m;
				CSN=msg.CSN;
			}
		}

	}

	private void removeTentative(int accept_stamp, int serverID, int csn) {
		Iterator<Write> it = tentativeWrite.iterator(); 
		while(it.hasNext()){
			Write tw = it.next();
			if(tw.accept_stamp==accept_stamp){
				if(tw.serverID==serverID){
					committedWrite.add(new Write(serverID,accept_stamp,csn,tw.command));
					it.remove();
				}

			}
		}

	}
}
