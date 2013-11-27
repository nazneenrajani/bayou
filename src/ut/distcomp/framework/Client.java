package ut.distcomp.framework;

public class Client extends Process {
	int client_id;
	PlayList db;
	
	public Client(Env env, ProcessId me){
		this.env=env;
		this.me=me;
		env.addProc(me, this);
	}
	
	@Override
	void body() {
		System.out.println("Here I am: " + me);
		
		while(true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BayouMessage msg = getNextMessage();
			if(msg instanceof UpdateMessage){
				UpdateMessage m = (UpdateMessage) msg;
				sendMessage(myConnectedNode(), new UpdateMessage(me, m.updateStr));
			}
			else if(msg instanceof ResponseMessage){
				//TODO print
			}
			//TODO handle messages to update database from node
		}
	}
	
	ProcessId myConnectedNode(){
		return env.Nodes.getProcessId(env.clientConnections[client_id]);
	}
}
