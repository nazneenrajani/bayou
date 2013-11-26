package ut.distcomp.framework;

public abstract class Process extends Thread {
	ProcessId me;
	Queue<BayouMessage> inbox = new Queue<BayouMessage>();
	Env env;

	abstract void body();

	public void run(){
		body();
		env.removeProc(me);
	}

	BayouMessage getNextMessage(){
		return inbox.bdequeue();
	}

	void sendMessage(ProcessId dst, BayouMessage msg){
		env.sendMessage(dst, msg);
	}

	void deliver(BayouMessage msg){
		inbox.enqueue(msg);
	}
}
