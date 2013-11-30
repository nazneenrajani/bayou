package ut.distcomp.framework;

import java.util.*;

//TODO arrange these according to the clients which handle them: Env, nodes, clients

public class BayouMessage {
	ProcessId src;
}

class YouArePrimaryMessage extends BayouMessage{
	YouArePrimaryMessage(ProcessId src){
		this.src = src;
	}
}
class UpdateMessage extends BayouMessage{
	String updateStr;
	int cid;
	UpdateMessage(ProcessId src, String updateStr, int cid){
		this.src = src; this.updateStr = updateStr; this.cid=cid;
	}
}
class QueryMessage extends BayouMessage{
	String songName; int cid;
	QueryMessage(ProcessId src, String songName, int cid){
		this.src = src; this.songName = songName;this.cid=cid;
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
class AntiEntropyInfoMessage extends BayouMessage{
	HashMap<String, Integer> versionVector; int CSN; String server_id;
	AntiEntropyInfoMessage(ProcessId src,HashMap<String, Integer> version_vector,int csn, String server_id) {
		this.src=src; this.versionVector = version_vector; this.CSN=csn; this.server_id=server_id; 
	}
}
class CommitNotification extends BayouMessage{
	int accept_stamp;String serverID; int CSN;
	CommitNotification(ProcessId src,int accept_stamp,String serverID,int csn) {
		this.src=src;this.accept_stamp=accept_stamp;this.serverID=serverID;this.CSN=csn;
	}
}
class WriteMessage extends BayouMessage{
	Write w;
	WriteMessage(ProcessId src, Write w){
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
	String response;
	ResponseMessage(ProcessId src, String response){
		this.src = src; this.response = response;
	}
}
class CreationMessage extends BayouMessage{
	public CreationMessage(ProcessId src){
		this.src=src;
	}
}
class ServerIDMessage extends BayouMessage{
	String server_id; String parent_id;
	ServerIDMessage(ProcessId src, String server_id, String parent_id){
		this.src = src;
		this.server_id = server_id;
		this.parent_id = parent_id;
	}
} 