package ut.distcomp.framework;

public class ConnectionMatrix {
	Boolean[][] connections;
	int maxNodes;
	
	public ConnectionMatrix(int maxNum){
		this.maxNodes = maxNum;
		connections = new Boolean[maxNum][maxNum];
		for(int i=0;i<maxNum;i++)
			for(int j=0;j<maxNum;j++) 
				connections[i][j]=false;
	}
	
	public void reconnect(int i){ //TODO rename this function according to spec
		for(int j=0;j<maxNodes;j++)
			connections[i][j]=true;
	}
	
	public void addNode(int i){
		reconnect(i);
	}
	
	public void removeNode(int i){
		isolate(i);
	}
	
	public void isolate(int i){
		for(int j=0;j<maxNodes;j++)
			connections[i][j]=false;
	}
	
	public void breakConnection(int i, int j){
		connections[i][j]=false;
	}
	
	public void recoverConnection(int i, int j){
		connections[i][j]=false;
	}
}
