package ut.distcomp.framework;

public class NodeList{
	ProcessId[] nodes;
	Env env;
	
	public NodeList(int maxNodes){
		nodes = new ProcessId[maxNodes];
		for(int i=0;i<nodes.length;i++)
			nodes[i]=null;		
	}
	
	public void add(int i){
		nodes[i]=new ProcessId("node:"+i);
	}
	
	public void remove(int i){
		nodes[i]=null;
	}

	public ProcessId getProcessId(Integer i) {
		return nodes[i];
	}
}