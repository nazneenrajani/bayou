package ut.distcomp.framework;

import java.util.*;

public class PaxosMessage {
	ProcessId src;
}

class UpdateMessage extends PaxosMessage{

}
class PrintLogMessage extends PaxosMessage{
}
class askAntiEntropyInfo extends PaxosMessage{
	askAntiEntropyInfo(ProcessId src) {
		this.src=src;
	}
}
class sendAntiEntropyInfo extends PaxosMessage{
	HashMap<Integer, Integer> versionVector; int CSN;
	sendAntiEntropyInfo(ProcessId src,HashMap<Integer, Integer> versionVector,int csn) {
		this.src=src;
	}
}
class sendCommitNotification extends PaxosMessage{
	int accept_stamp;int serverID; int CSN;
	sendCommitNotification(ProcessId src,int accept_stamp,int serverID,int csn) {
		this.src=src;this.accept_stamp=accept_stamp;this.serverID=serverID;this.CSN=csn;
	}
}
class sendWrite extends PaxosMessage{
	Write w;
	sendWrite(ProcessId src, Write w){
		this.src=src;this.w=w;
	}
}
