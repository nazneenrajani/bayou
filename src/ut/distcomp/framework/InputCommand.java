package ut.distcomp.framework;

public class InputCommand {
	String command, updateStr = null;
	Integer nodeid, nodeid2 = null;
	Long delay = null;
	Integer clientid = null;

	public InputCommand(String commandStr){
		String[] s=commandStr.split("[(,)]");
		command = s[0];
		if(s[0].equals("update")){
			nodeid = Integer.parseInt(s[1]);
			clientid = Integer.parseInt(s[2]);
			updateStr = s[3];
		}
		else{ //TODO check that all cases are handled
			switch(s[0]){
			case "breakConnection":
			case "recoverConnection":
				nodeid2 = Integer.parseInt(s[2]);
			case "join":
			case "remove":
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

	}
}
