package ut.distcomp.framework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Env {	
	static Map<ProcessId, Process> procs = new HashMap<ProcessId, Process>();
	NodeList Nodes;
	ProcessId me= new ProcessId("env");

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

	int numClients=5;
	ProcessId[] clients = new ProcessId[numClients];
	int[] clientConnections = new int[numClients];

	int maxNodes=10;
	ConnectionMatrix connections = new ConnectionMatrix(maxNodes);

	void run(String[] args) throws IOException{
		for(int i=0;i<numClients;i++)
		{
			clients[i]=new ProcessId("client:"+i);
			clientConnections[i] = -1;
			new Client(this,clients[i]);
		}

		Nodes = new NodeList(maxNodes);
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
		while ((line = bufferedReader.readLine()) != null) {
			process(new InputCommand(line));   
		}
		bufferedReader.close();
	}

	private void process(InputCommand command) {
		System.out.println("processing "+command.command);
		switch(command.command){
		case "join":
			Nodes.add(command.nodeid);
			connections.addNode(command.nodeid);
			new Node(this,Nodes.nodes[command.nodeid],command.nodeid); 
			break;
		case "remove":
			sendMessage(Nodes.getProcessId(command.nodeid), new RetireMessage(me));
			break;
		case "isolate":
			connections.isolate(command.nodeid);
			break;
		case "reconnect":
			connections.reconnect(command.nodeid);
			break;
		case "breakConnection":
			connections.breakConnection(command.nodeid, command.nodeid2);
			break;
		case "recoverConnection":
			connections.recoverConnection(command.nodeid, command.nodeid2);
			break;
		case "update":
			sendMessage(clients[command.clientid], new UpdateMessage(me, command.updateStr)); //TODO make update message
			break;
		case "printLog":
			if(command.nodeid!=null)
				sendMessage(Nodes.getProcessId(command.nodeid), new PrintLogMessage(me));
			else
				for(ProcessId nodeid : Nodes.nodes){
					sendMessage(nodeid, new PrintLogMessage(me));
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
		case "clientConnect":
			clientConnections[command.clientid] = command.nodeid;
			break;
		case "clientDisconnect":
			clientConnections[command.clientid] = -1;
			break;
		default:
			System.err.println("Unknown command. This should never happen");
		}
	}

	private void goToPrompt() {		
		Scanner input = new Scanner(System.in);

		while(true){
			System.out.print("Enter a command: ");
			InputCommand c = new InputCommand(input.next());
			if(c.command.equals("continue")){
				break;
			}
			else{
				process(c);
			}
		}
		input.close();
	}

	public static void main(String[] args) throws IOException{
		new Env().run(args);
	}
	
	public Env(){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				goToPrompt();
			}
		});
	}
}
