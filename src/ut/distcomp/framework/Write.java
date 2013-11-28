package ut.distcomp.framework;

public class Write implements Comparable<Write>{
	String serverID;
	int accept_stamp;
	int CSN=-1;
	String command;

	public Write(String id, int accept_stamp, int csn, String command){
		this.serverID=id;
		this.accept_stamp=accept_stamp;
		this.CSN=csn;
		this.command=command;
	}
	
	public int compareTo(Write c) {
		if(this.CSN!=-1 && c.CSN!=-1)
			return this.CSN - c.CSN;
		else if(this.accept_stamp!=c.accept_stamp)
			return this.accept_stamp-c.accept_stamp;
		else
			return this.serverID.compareTo(c.serverID); //TODO check this
	}
	
	public String toString(){
		return "("+serverID+","+accept_stamp+","+CSN+","+command+")";
	}
}
