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
