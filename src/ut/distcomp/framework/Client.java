package ut.distcomp.framework;

import java.util.Arrays;

public class Client extends Process {
	int client_id;
	int cid=0;

	public Client(Env env, ProcessId me, int client_id){
		this.env=env;
		this.me=me;
		this.client_id= client_id;
		env.addProc(me, this);
	}

	@Override
	void body() {
		System.out.println("Here I am: " + me);

		while(true){
			BayouMessage msg = getNextMessage();
			if(msg instanceof UpdateMessage){				
				UpdateMessage m = (UpdateMessage) msg;
				cid++;
				if(myConnectedNode()!=null)
					sendMessage(myConnectedNode(), new UpdateMessage(me, m.updateStr,cid));
				else{
					System.err.println(me+" not connected to any node");
				}
			}
			else if(msg instanceof QueryMessage){
				QueryMessage m = (QueryMessage) msg;
				if(myConnectedNode()!=null)
					sendMessage(myConnectedNode(), new QueryMessage(me, m.songName,cid));
				else{
					System.err.println(me+" not connected to any node");
				}
			}
			else if(msg instanceof ResponseMessage){
				ResponseMessage m = (ResponseMessage) msg;
				System.err.println("Received response "+m.response+" from "+m.src);
			}
		}
	}

	ProcessId myConnectedNode(){
		if(env.clientConnections[client_id]==-1)
			return null;
		else
			return env.Nodes.getProcessId(env.clientConnections[client_id]);
	}
}
