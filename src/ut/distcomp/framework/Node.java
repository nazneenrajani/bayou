package ut.distcomp.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Node extends Process{
	int node_id;
	Server_id server_id;
	int CSN=0;
	int current_stamp=0;
	Map<Integer,Integer> version_vector = new HashMap<Integer,Integer>(); 
	//TODO shouldn't this be storing (accept_stamp,serverid)
	Set<Write> tentativeWrites = new TreeSet<Write>();
	Set<Write> committedWrites = new TreeSet<Write>();
	Boolean exitFlag = false;
	Boolean isPrimary;
	Boolean acceptingClientRequests=false;
	
	public Node(Env env, ProcessId me, int nodeId, Boolean isPrimary){
		this.node_id=nodeId;
		this.me = me;
		this.env = env;
		this.isPrimary = isPrimary;
		add_entry("creation;"+node_id);
		//TODO creation actions. Send a Creation write to everyone. Get a server_id
		//TODO after getting a serverid, start acceptingClientRequests
		env.addProc(me, this);
	}

	public void add_entry(String command){
		if(isPrimary){
			committedWrites.add(new Write(node_id, current_stamp+1, CSN+1, command));
			CSN++; current_stamp++;
		}
		else{
			tentativeWrites.add(new Write(node_id, current_stamp+1, -1, command));
			current_stamp++;
		}
	}

	public void anti_entropy(ProcessId R, HashMap<Integer,Integer> versionVector, int csn){
		//TODO check if this guy was known about before. If not, add to versionVector
		
		if(csn<CSN){
			Iterator<Write> it = committedWrites.iterator(); 
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
		Iterator<Write> it = tentativeWrites.iterator(); 
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
		System.out.println("Commited Writelog for "+me+":"+committedWrites);
		System.out.println("Tentative Writelog for "+me+":"+tentativeWrites);
		/*System.out.println("Log for "+me+":");
		for(Write w:committedWrites)
			System.out.print(w);*/
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
				UpdateMessage msg = (UpdateMessage) m;
				add_entry(msg.updateStr);
			}
			else if(m instanceof sendCommitNotification){
				sendCommitNotification msg = (sendCommitNotification) m;
				removeTentative(msg.accept_stamp,msg.serverID,msg.CSN);
			}
			else if(m instanceof sendWrite){
				sendWrite msg = (sendWrite) m;
				version_vector.put(msg.src.id, msg.w.accept_stamp);
				//TODO handle case when a committed write is sent. handle duplicates
				tentativeWrites.add(msg.w);
			}
			else if(m instanceof YouArePrimaryMessage){
				isPrimary=true;
				
				//TODO make all my tentative rights permanent
			}
		}
		env.Nodes.remove(node_id); //TODO rename node_id to my_id
		env.connections.isolate(node_id);
	}

	private void removeTentative(int accept_stamp, int serverID, int csn) {
		Iterator<Write> it = tentativeWrites.iterator(); 
		while(it.hasNext()){
			Write tw = it.next();
			if(tw.accept_stamp==accept_stamp){
				if(tw.serverID==serverID){
					committedWrites.add(new Write(serverID,accept_stamp,csn,tw.command));
					//TODO update my CSN
					it.remove();
				}
			}
		}
	}
	
	void commitAllTentativeWrites(){
		for(Write w:tentativeWrites){
			//TODO remove it
		}
		//TODO add to commiteedWrites
	}
}
