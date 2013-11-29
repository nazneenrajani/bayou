package ut.distcomp.framework;

public class InputCommand {
	String command, updateStr = null;
	Integer nodeid, nodeid2 = null;
	Long delay = null;
	Integer clientid = null;
	String commandStr;

	public InputCommand(String commandStr){
		this.commandStr = commandStr;
		String[] s=commandStr.split("[(,)]");
		command = s[0];
		//TODO check that all cases are handled
		switch(command){
		case "update":
		case "query":
			clientid = Integer.parseInt(s[1]);
			updateStr = s[2];
			break;
		case "breakConnection":
		case "recoverConnection":
			nodeid2 = Integer.parseInt(s[2]);
		case "join":
		case "leave":
		case "isolate":
		case "reconnect":
			nodeid = Integer.parseInt(s[1]);
			break;
		case "printLog":
			if(s.length>1)
				nodeid = Integer.parseInt(s[1]);
			break;
		case "pause":
		case "continue":
			break;
		case "delay":
			delay=Long.parseLong(s[1]);
			break;
		case "clientConnect":
			clientid = Integer.parseInt(s[1]);
			nodeid = Integer.parseInt(s[2]);
			break;
		case "clientDisconnect":
			clientid = Integer.parseInt(s[1]);
			break;
		default:
			System.err.println("Unrecognized command in file "+s[0]);
		}
	}
	
	public String toString(){
		return commandStr;
	}
}
