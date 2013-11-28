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
class AntiEntropyInfoMessage extends BayouMessage{
	HashMap<Integer, Integer> versionVector; int CSN;
	AntiEntropyInfoMessage(ProcessId src,Map<Integer, Integer> version_vector,int csn) {
		this.src=src;
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
	ResponseMessage(ProcessId src){
		this.src = src;
	}
}
class CreationMessage extends BayouMessage{
	public CreationMessage(ProcessId src){
		this.src=src;
	}
}
class ServerIDMessage extends BayouMessage{
	String server_id;
	ServerIDMessage(ProcessId src, String server_id){
		this.src = src;
		this.server_id = server_id;
	}
} 