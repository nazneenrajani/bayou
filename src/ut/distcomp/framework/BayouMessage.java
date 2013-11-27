package ut.distcomp.framework;

import java.util.*;

//TODO arrange these according to the clients which handle them: Env, nodes, clients

public class BayouMessage {
	ProcessId src;
}

class UpdateMessage extends BayouMessage{
	String updateStr;
	UpdateMessage(ProcessId src, String updateStr){
		this.src = src; this.updateStr = updateStr;
	}
}
class PrintLogMessage extends BayouMessage{
	PrintLogMessage(ProcessId src){
		this.src = src;
	}
}
class askAntiEntropyInfo extends BayouMessage{
	askAntiEntropyInfo(ProcessId src) {
		this.src=src;
	}
}
class sendAntiEntropyInfo extends BayouMessage{
	HashMap<Integer, Integer> versionVector; int CSN;
	sendAntiEntropyInfo(ProcessId src,HashMap<Integer, Integer> versionVector,int csn) {
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
class FailureToSendMessage extends BayouMessage{
	ProcessId dst;
	public FailureToSendMessage(ProcessId src, ProcessId dst){
		this.src = src; this.dst = dst;
	}
}
class RetireMessage extends BayouMessage{
	RetireMessage(ProcessId src){
		this.src = src;
	}
}
class ResponseMessage extends BayouMessage{
	ResponseMessage(ProcessId src){
		this.src = src;
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