package ut.distcomp.framework;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Env {
	static Map<ProcessId, Process> procs = new HashMap<ProcessId, Process>();
	static NodeList Nodes;

	synchronized static void sendMessage(ProcessId dst, BayouMessage msg){
		Process p = procs.get(dst);
		if (p != null) {
			p.deliver(msg);
		}
	}

	synchronized void addProc(ProcessId pid, Process proc){
		procs.put(pid, proc);
		proc.start();
	}

	synchronized void removeProc(ProcessId pid){
		procs.remove(pid);
	}

	void run(String[] args) throws IOException{
		//TODO first spawn clients

		Nodes = new NodeList();
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		String line = null;
		while ((line = reader.readLine()) != null) {
			process(new InputCommand(line));   
		}		
	}

	private static void process(InputCommand command) {
		System.out.println("processing "+command.command);
		switch(command.command){
		case "join":
			Nodes.add(command.node1);
			break;
		case "remove":
			Nodes.remove(command.node1);
			break;
		case "isolate":
			Nodes.connections.isolate(command.node1);
			break;
		case "reconnect":
			Nodes.connections.reconnect(command.node1);
			break;
		case "breakConnection":
			Nodes.connections.breakConnection(command.node1, command.node2);
			break;
		case "recoverConnection":
			Nodes.connections.recoverConnection(command.node1, command.node2);
			break;
		case "update":
			sendMessage(Nodes.getProcessId(command.node1), new UpdateMessage()); //TODO make update message
			break;
		case "printLog":
			sendMessage(Nodes.getProcessId(command.node1), new PrintLogMessage());
			break;
		case "printAllLogs":
			for(ProcessId nodeid : Nodes.nodes){
				sendMessage(nodeid, new PrintLogMessage());
			}
			break;
		case "pause":
			goToPrompt();
			break;
		case "continue":		
			break;
		case "delay":
			try {
				Thread.sleep(command.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		default:
			System.err.println("Unknown command. This should never happen");
		}
	}

	private static void goToPrompt() {		
		Scanner input = new Scanner (System.in );

		while(true){
			System.out.println("Enter a command: ");
			InputCommand c = new InputCommand(input.next());
			if(c.command.equals("continue")){
				break;
			}
			else{
				process(c);
			}
		}
	}

	public static void main(String[] args) throws IOException{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				goToPrompt();
			}
		});
		new Env().run(args);
	}
}
