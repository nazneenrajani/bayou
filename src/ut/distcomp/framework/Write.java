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
	public int compareTo(Write c) {
		if(this.CSN!=-1 && c.CSN!=-1)
			return this.CSN - c.CSN;
		else if(this.accept_stamp!=c.accept_stamp)
			return this.accept_stamp-c.accept_stamp;
		else
			return this.serverID-c.serverID;
	}
}
