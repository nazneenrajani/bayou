package ut.distcomp.framework;

import java.util.HashMap;
import java.util.Iterator;
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
	Boolean exitFlag = false;
	Boolean isPrimary = false;

	public Node(Env env, ProcessId me, int nodeId){
		this.node_id=nodeId;
		this.me = me;
		this.env = env;
		env.addProc(me, this);
		//TODO creation actions. Send a Creation write to everyone
	}

	public void add_entry(){
		//TODO: add to log, called by controller
	}

	public void anti_entropy(ProcessId R, HashMap<Integer,Integer> versionVector, int csn){
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
			else if(m instanceof askAntiEntropyInfo){
				askAntiEntropyInfo msg = (askAntiEntropyInfo) m;
				if(msg.src!=me)
					sendMessage(msg.src, new sendAntiEntropyInfo(me, version_vector, CSN));
			}
			else if(m instanceof RetireMessage){
				retire();
			}
			else if(m instanceof PrintLogMessage){
				printLog();
			}
			else if(m instanceof UpdateMessage){
				//TODO make write. Put timestamp
			}
			else if(m instanceof sendCommitNotification){
				sendCommitNotification msg = (sendCommitNotification) m;
				removeTentative(msg.accept_stamp,msg.serverID,msg.CSN);
			}
			else if(m instanceof sendWrite){
				sendWrite msg = (sendWrite) m;
				version_vector.put(Integer.parseInt(msg.src.name), msg.w.accept_stamp);
				tentativeWrite.add(msg.w);
			}
			else if(m instanceof sendCSN){
				sendCSN msg = (sendCSN) m;
				CSN=msg.CSN;
			}
			else if(m instanceof YouArePrimaryMessage){
				isPrimary=true;
			}
		}
		env.Nodes.remove(node_id); //TODO rename node_id to my_id
		env.connections.isolate(node_id);
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
