package ut.distcomp.framework;

public class ProcessId implements Comparable {
	String name;
	int id;

	public ProcessId(String name){ this.name = name; this.id = Integer.parseInt(name.split(":")[1]);}

	public boolean equals(Object other){
		return name.equals(((ProcessId) other).name);
	}

	public int compareTo(Object other){
		return name.compareTo(((ProcessId) other).name);
	}

	public String toString(){ return name; }
}
