package ut.distcomp.framework;

import java.util.Arrays;

public class Client extends Process {
	int client_id;
	PlayList db;

	public Client(Env env, ProcessId me, int client_id){
		this.env=env;
		this.me=me;
		this.client_id= client_id;
		this.db = new PlayList();
		env.addProc(me, this);
	}

	@Override
	void body() {
		System.out.println("Here I am: " + me);

		while(true){
			BayouMessage msg = getNextMessage();
			if(msg instanceof UpdateMessage){
				UpdateMessage m = (UpdateMessage) msg;
				if(myConnectedNode()!=null)
					sendMessage(myConnectedNode(), new UpdateMessage(me, m.updateStr));
				else{
					System.err.println(me+" not connected to any node");
				}
			}
			else if(msg instanceof QueryMessage){
				QueryMessage m = (QueryMessage) msg;
				if(myConnectedNode()!=null)
					sendMessage(myConnectedNode(), new QueryMessage(me, m.songName));
				else{
					System.err.println(me+" not connected to any node");
				}
			}
			else if(msg instanceof ResponseMessage){
				//TODO print
			}
			//TODO handle messages to update database from node
		}
	}

	ProcessId myConnectedNode(){
		if(env.clientConnections[client_id]==-1)
			return null;
		else
			return env.Nodes.getProcessId(env.clientConnections[client_id]);
	}
}
