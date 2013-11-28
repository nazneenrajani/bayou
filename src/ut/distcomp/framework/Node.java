package ut.distcomp.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Node extends Process{
	int node_id;
	String server_id;
	int CSN=0;
	int accept_stamp=0;
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
		env.addProc(me, this);
	}

	public void add_entry(String command){
		//TODO should accept_stamp be logical or clock?
		if(isPrimary){
			committedWrites.add(new Write(server_id, accept_stamp+1, CSN+1, command));
			CSN++; accept_stamp++;
		}
		else{
			tentativeWrites.add(new Write(server_id, accept_stamp+1, -1, command));
			accept_stamp++;
		}
		//TODO update version_vector
	}

	public void anti_entropy(ProcessId R, HashMap<Integer,Integer> versionVector, int csn){
		//TODO check if this guy was known about before. If not, add to versionVector
		
		if(csn<CSN){
			Iterator<Write> it = committedWrites.iterator(); 
			while(it.hasNext()){
				Write cw = it.next();
				if(cw.CSN>csn){
					if(cw.accept_stamp<=versionVector.get(cw.serverID))
						sendMessage(R,new CommitNotification(me, cw.accept_stamp, cw.serverID, cw.CSN));
					else
						sendMessage(R, new WriteMessage(me, cw));
				}
			}
		}
		Iterator<Write> it = tentativeWrites.iterator(); 
		while(it.hasNext()){
			Write tw = it.next();
			if(versionVector.get(tw.serverID)<tw.accept_stamp)
				sendMessage(R, new WriteMessage(me, tw));
		}

	}

	public void retire(){
		//TODO stop accepting client requests
		//TODO: transfer db and leave
		//add_entry("retire;"+server_id);
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
		
		if(isPrimary){
			//Entering an empty environment.
			server_id="primary";
			add_entry("creation;"+server_id);
		}
		else{
			//send a write as a client would, to register myself
			for(ProcessId nodeid: env.Nodes.nodes){
				if(nodeid!=null){
					sendMessage(nodeid, new CreationMessage(me));
					break;
				}
			}
			BayouMessage m = getNextMessage();
			if(m instanceof ServerIDMessage){
				ServerIDMessage msg = (ServerIDMessage) m;
				server_id = msg.server_id;
				accept_stamp = Integer.parseInt(server_id)+1;
			}
		}

		acceptingClientRequests=true;
		while(!exitFlag){
			//delay(500);
			for(ProcessId nodeid: env.Nodes.nodes){
				sendMessage(nodeid, new askAntiEntropyInfo(me));
			} 
			//TODO don't send too many of ^. Just send one and wait for a round of responses. Then send another. Maybe send one when you receive a response, or have new info to send
			// or have a timeout
			BayouMessage m = getNextMessage();
			if(m instanceof AntiEntropyInfoMessage){
				AntiEntropyInfoMessage msg = (AntiEntropyInfoMessage) m;
				anti_entropy(msg.src,msg.versionVector,msg.CSN);
			}
			else if(m instanceof askAntiEntropyInfo){
				askAntiEntropyInfo msg = (askAntiEntropyInfo) m;
				if(msg.src!=me)
					sendMessage(msg.src, new AntiEntropyInfoMessage(me, version_vector, CSN));
			}
			else if(m instanceof RetireMessage){
				retire();
			}
			else if(m instanceof PrintLogMessage){
				printLog();
			}
			else if(m instanceof CreationMessage){
				//TODO add new guy to version vector.
				add_entry("creation;"+m.src.name);
				sendMessage(m.src, new ServerIDMessage(me, accept_stamp+":"+server_id));
			}
			else if(m instanceof UpdateMessage){
				UpdateMessage msg = (UpdateMessage) m;
				add_entry(msg.updateStr);
			}
			else if(m instanceof CommitNotification){
				CommitNotification msg = (CommitNotification) m;
				commitTentativeWrite(msg.accept_stamp,msg.serverID,msg.CSN);
			}
			else if(m instanceof WriteMessage){
				WriteMessage msg = (WriteMessage) m;
				version_vector.put(msg.src.id, msg.w.accept_stamp);
				//TODO handle case when a committed write is sent. handle duplicates
				tentativeWrites.add(msg.w);
			}
			else if(m instanceof YouArePrimaryMessage){
				//TODO nobody sends this yet
				isPrimary=true;
				commitAllTentativeWrites();
			}
		}
		env.Nodes.remove(node_id); //TODO rename node_id to my_id
		env.connections.isolate(node_id);
	}

	private void commitTentativeWrite(int accept_stamp, String serverID, int csn) {
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
			//TODO commit it. Avoid concurrentmodification exception
		}
	}
}
