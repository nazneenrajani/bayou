package ut.distcomp.framework;

import java.util.*;

public class BayouMessage {
	ProcessId src;
}

class UpdateMessage extends BayouMessage{

}
class PrintLogMessage extends BayouMessage{
}
class askAntiEntropyInfo extends BayouMessage{
	askAntiEntropyInfo(ProcessId src) {
		this.src=src;
	}
}
class sendAntiEntropyInfo extends BayouMessage{
	HashMap<Integer, Integer> versionVector; int CSN;
	sendAntiEntropyInfo(ProcessId src,Map<Integer, Integer> version_vector,int csn) {
		this.src=src;
	}
}
class sendCommitNotification extends BayouMessage{
	int accept_stamp;int serverID; int CSN;
	sendCommitNotification(ProcessId src,int accept_stamp,int serverID,int csn) {
		this.src=src;this.accept_stamp=accept_stamp;this.serverID=serverID;this.CSN=csn;
	}
}
class sendWrite extends BayouMessage{
	Write w;
	sendWrite(ProcessId src, Write w){
		this.src=src;this.w=w;
	}
}
class sendDB extends BayouMessage{
	PlayList db;
	sendDB(ProcessId src, PlayList db){
		this.src=src;this.db=db;
	}
}
class sendVector extends BayouMessage{
	Map<Integer,Integer> vv;
	sendVector(ProcessId src, Map<Integer, Integer> older_version_vector){
		this.src=src;this.vv=older_version_vector;
	}
}
class sendCSN extends BayouMessage{
	int CSN;
	sendCSN(ProcessId src, int csn){
		this.src=src;this.CSN=csn;
	}
}
