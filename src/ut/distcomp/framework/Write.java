package ut.distcomp.framework;

public class Write {
	int serverID;
	int accept_stamp;
	int CSN;
	String command;
	
	public Write(int id, int accept_stamp, String command){
		this.serverID=id;
		this.accept_stamp=accept_stamp;
		this.CSN=-1;
		this.command=command;
	}
}
