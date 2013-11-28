package ut.distcomp.framework;

public class Write implements Comparable<Write>{
	int serverID; //TOOD serverid is more complicated than this. see paper
	int accept_stamp;
	int CSN=-1;
	String command;

	public Write(int id, int accept_stamp, int csn, String command){
		this.serverID=id;
		this.accept_stamp=accept_stamp;
		this.CSN=csn;
		this.command=command;
	}
	
	public int compareTo(Write c) {
		if(this.CSN!=-1 && c.CSN!=-1)
			return this.CSN - c.CSN;
		else if(this.accept_stamp!=c.accept_stamp)
			return (int) (this.accept_stamp-c.accept_stamp);
		else
			return this.serverID-c.serverID;
	}
	
	public String toString(){
		return "("+serverID+","+accept_stamp+","+CSN+","+command+")";
	}
}
