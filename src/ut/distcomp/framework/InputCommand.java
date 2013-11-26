package ut.distcomp.framework;

public class InputCommand {
	String command, clientid, updateParams = null;
	Integer node1, node2 = null;
	Long delay = null;

	public InputCommand(String commandStr){
		String[] s=commandStr.split("[(,)]");
		command = s[0];
		if(s[0].equals("update")){
			node1 = Integer.parseInt(s[1]);
			clientid = s[2];
			//TODO parse update from s[3]
			//String[] update = s[3].split("&&");
		}
		else{ //TODO check that all cases are handled
			switch(s[0]){
			case "breakConnection":
			case "recoverConnection":
				node2 = Integer.parseInt(s[2]);
			case "join":
			case "remove":
			case "isolate":
			case "reconnect":
			case "printLog":
				node1 = Integer.parseInt(s[1]);
			case "printAllLogs":
			case "pause":
			case "continue":
				break;
			case "delay":
				delay=Long.parseLong(s[1]);
				break;
			default:
				System.err.println("Unrecognized command in file "+s[0]);
			}
		}

	}
}
