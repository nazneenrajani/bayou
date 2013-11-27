package ut.distcomp.framework;

public class ConnectionMatrix {
	Boolean[][] connections;
	int maxNodes;
	
	public ConnectionMatrix(int maxNodes){
		this.maxNodes = maxNodes;
		connections = new Boolean[maxNodes][maxNodes];
		for(int i=0;i<maxNodes;i++)
			for(int j=0;j<maxNodes;j++) 
				connections[i][j]=false;
	}
	
	public void isolate(int i){
		for(int j=0;j<maxNodes;j++){
			connections[i][j]=false;
			connections[j][i]=false;
		}
	}
	
	public void reconnect(int i){
		for(int j=0;j<maxNodes;j++){
			connections[i][j]=true;
			connections[j][i]=true;
		}
	}
	
	public void addNode(int i){
		reconnect(i);
	}
	
	public void removeNode(int i){
		isolate(i);
	}
	
	public void breakConnection(int i, int j){
		connections[i][j]=false;
		connections[j][i]=false;
	}
	
	public void recoverConnection(int i, int j){
		connections[i][j]=true;
		connections[j][i]=true;
	}

	public boolean get(ProcessId me, ProcessId dst) {
		if(me==null || dst==null)
			return false;
		int i=Integer.parseInt(me.name.split(":")[1]);
		int j=Integer.parseInt(dst.name.split(":")[1]);
		return connections[i][j];
	}
}
