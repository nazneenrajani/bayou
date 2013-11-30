package ut.distcomp.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Node extends Process{
	int node_id;
	String server_id;
	int CSN=0; //TODO make sure this init is right
	int accept_stamp=0;
	HashMap<String,Integer> version_vector = new HashMap<String,Integer>(); 
	Set<Write> tentativeWrites = new TreeSet<Write>();
	Set<Write> committedWrites = new TreeSet<Write>();
	Boolean exitFlag = false;
	Boolean exitOnNextAntientropy = false;
	Boolean isPrimary;
	Boolean acceptingClientRequests=true;
	Boolean newInformation = true;
	PlayList db;
	
	final int inf = 99999;

	public Node(Env env, ProcessId me, int nodeId, Boolean isPrimary){
		this.node_id=nodeId;
		this.me = me;
		this.env = env;
		this.isPrimary = isPrimary;
		env.addProc(me, this);
	}

	public void add_entry(String command){
		if(isPrimary){
			CSN++; accept_stamp++;
			committedWrites.add(new Write(server_id, accept_stamp, CSN, command));
			//TODO execute commands in the order they were received everytime something is committed
			db.execute(command);
		}
		else{
			accept_stamp++;
			tentativeWrites.add(new Write(server_id, accept_stamp, -1, command));
		}
		version_vector.put(server_id,accept_stamp); //TODO I always update vv with accept_stamp
	}
	
	int CompleteV(HashMap<String,Integer> r_versionVector, String w_serverID){
		if(r_versionVector.containsKey(w_serverID)){
			return r_versionVector.get(w_serverID);
		}
		else if(w_serverID.equals("0:primary")) //first server
			return inf;
		else{
			String[] s =w_serverID.split(":",2);
			int tki = Integer.parseInt(s[0]);
			if(CompleteV(r_versionVector, s[1])>=tki)
				return inf;
			else
				return -inf;
		}
	}

	public void anti_entropy(ProcessId R, HashMap<String,Integer> r_versionVector, int r_csn, String r_server_id){
		System.out.println(me+" doing anti_entropy with "+R+"\n"+"my vv "+version_vector + " his "+r_versionVector);

		if(r_csn<CSN){
			Iterator<Write> it = committedWrites.iterator(); 
			while(it.hasNext()){
				Write cw = it.next();
				if(cw.CSN>r_csn){
					int r_accept_stamp = CompleteV(r_versionVector,cw.serverID);
					if(r_accept_stamp<inf){
						if(cw.accept_stamp<=r_accept_stamp)
							sendMessage(R,new CommitNotification(me, cw.accept_stamp, cw.serverID, cw.CSN));
						else
							sendMessage(R, new WriteMessage(me, cw));
					}
				}
			}
		}
		Iterator<Write> it = tentativeWrites.iterator(); 
		while(it.hasNext()){
			Write tw = it.next();
			int r_accept_stamp = CompleteV(r_versionVector,tw.serverID);
			if(r_accept_stamp<inf){
				if(r_versionVector.get(tw.serverID)<tw.accept_stamp)
					sendMessage(R, new WriteMessage(me, tw));
			}
		}
		if(exitOnNextAntientropy)
			exitFlag = true;
	}

	public void retire(){
		acceptingClientRequests=false;
		//TODO anything special if primary decides to retire. Make new primary?
		add_entry("retire;"+me+";"+server_id);
		exitOnNextAntientropy=true;
	}

	public void printLog(){
		System.out.println(server_id+" accept_stamp= "+accept_stamp+" CSN="+CSN+"\nCommited Writelog for "+me+":"+committedWrites+"\n"+"Tentative Writelog for "+me+":"+tentativeWrites);
		System.out.flush();
		/*System.out.println("Log for "+me+":");
		for(Write w:committedWrites)
			System.out.print(w);*/
	}

	@Override
	void body() {
		System.out.println("Here I am: " + me);

		if(isPrimary){
			//Entering an empty environment.
			server_id=accept_stamp+":"+"primary";
			add_entry("creation;"+me+";"+server_id);
		}
		else{
			boolean sentMessage=false;
			while(!sentMessage){
				for(ProcessId nodeid: env.Nodes.nodes){
					if(nodeid!=null){
						sendMessage(nodeid, new CreationMessage(me));
						sentMessage=true;
						break;
					}
				}
			}
			ArrayList<BayouMessage> pendingMessages = new ArrayList<BayouMessage>();
			while(true){
				BayouMessage m = getNextMessage();
				if(m instanceof ServerIDMessage){
					ServerIDMessage msg = (ServerIDMessage) m;
					server_id = msg.server_id;
					accept_stamp = Integer.parseInt(server_id.split(":")[0])+1;
					System.out.println(me+" was assigned server_id "+server_id);
					break;
				}
				else
					pendingMessages.add(m);
			}
			for(BayouMessage m: pendingMessages)
				deliver(m);
		}

		newInformation = true;
		while(!exitFlag){
			//printLog();
			delay(500);
			if(inbox.ll.isEmpty() && newInformation){ //TODO set newInformation to false when there is nothing
				delay(2000);
				//TODO send only if I have something to say
				for(ProcessId nodeid: env.Nodes.nodes){
					sendMessage(nodeid, new askAntiEntropyInfo(me));
				}
			}
			//TODO current code sends too many askAntiEntropy. Just send one and wait for a round of responses. Then send another. Maybe send one when you receive a response, or have new info to send
			// or have a timeout
			BayouMessage m = getNextMessage();
			if(m instanceof AntiEntropyInfoMessage){
				AntiEntropyInfoMessage msg = (AntiEntropyInfoMessage) m;
				anti_entropy(msg.src,msg.versionVector,msg.CSN, msg.server_id);
			}
			else if(m instanceof askAntiEntropyInfo){
				askAntiEntropyInfo msg = (askAntiEntropyInfo) m;
				if(msg.src!=me){
					sendMessage(msg.src, new AntiEntropyInfoMessage(me, version_vector, CSN,server_id));
				}
			}
			else if(m instanceof RetireMessage){
				retire();
			}
			else if(m instanceof PrintLogMessage){
				printLog();
			}
			else if(m instanceof CreationMessage){
				CreationMessage msg = (CreationMessage) m;
				sendMessage(msg.src, new ServerIDMessage(me, accept_stamp+":"+server_id));
				String new_server_id = accept_stamp+":"+server_id;
				add_entry("creation;"+msg.src.name+";"+new_server_id);
				version_vector.put(new_server_id,accept_stamp); //TODO check
				System.out.println(me+" assigned server_id "+new_server_id+" to "+msg.src);
			}
			else if(m instanceof UpdateMessage){
				UpdateMessage msg = (UpdateMessage) m;
				if(acceptingClientRequests){
					//TODO use cid
					add_entry(msg.updateStr);
				}
				else
					System.err.println("Not accepting client requests. Dropped: "+msg.updateStr);
			}
			else if(m instanceof QueryMessage){
				QueryMessage msg = (QueryMessage) m;
				//TODO Ensure RYW through cid
				sendMessage(msg.src, new ResponseMessage(me, "Not responding to queries right now"));
			}
			/*else if(m instanceof sendDB){
				sendDB msg = (sendDB) m;
				//TODO: merge dbs
			}*/
			else if(m instanceof CommitNotification){
				CommitNotification msg = (CommitNotification) m;
				commitTentativeWrite(msg.accept_stamp,msg.serverID,msg.CSN);
				//TODO handle the case that the write is already committed
			}
			else if(m instanceof WriteMessage){
				//TODO primary makes tentative writes permanent
				WriteMessage msg = (WriteMessage) m;
				version_vector.put(msg.w.serverID, msg.w.accept_stamp); //TODO this should handle the case that you haven't heard about this guy before
				//TODO make sure duplicates are handled. Does TreeSet handle that?
				//tentativeWrites.add(msg.w);
				if(msg.w.CSN==-1){
					//if(!committedWrites.contains(msg.w)) //TODO ensure contains uses only server_id and accept_stamps, or modify this line
					tentativeWrites.add(msg.w);
				}
				else{
					//				if(tentativeWrites.contains(msg.w)) //TODO same as above
					//					tentativeWrites.remove(msg.w);
					committedWrites.add(msg.w);
				}
				if(msg.w.command.split(";")[0].equals("creation")){
					String new_server_id = msg.w.command.split(";")[2];
					int accept_stamp = Integer.parseInt(new_server_id.split(":")[0]);
					version_vector.put(new_server_id, accept_stamp); //TODO should accept_stamp be 1 less than his first stamp?
				}
				else if(msg.w.command.split(";")[0].equals("retire")){
					//TODO handle case where write is a retirement write
					String retiring_server_id = msg.w.command.split(";")[2];
					version_vector.remove(retiring_server_id);
				}
			}
			/*			else if(m instanceof sendCSN){
				sendCSN msg = (sendCSN) m;
				CSN=msg.CSN;
			}*/
			else if(m instanceof YouArePrimaryMessage){
				//TODO nobody sends this yet. Required if primary ever leaves
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
				if(tw.serverID.equals(serverID)){
					System.out.println(me +" Committing tentative write "+tw);
					committedWrites.add(new Write(serverID,accept_stamp,csn,tw.command));
					if(csn>CSN) //TODO This should always be true for a single server. maybe not for multiple
						CSN=csn;
					//TODO execute commands in the order they were received 
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
