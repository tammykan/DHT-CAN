package tw.nccu.edu.dht;

import java.io.Serializable;
import java.util.HashMap;

public class Neighbour implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String ip;
	private int port;
	private Coordinate c;
	private HashMap<String, String> table = new HashMap<String, String>();
	
	public Neighbour(String ip, int port, Coordinate c, HashMap<String, String> table) {
		this.ip = ip;
		this.port = port;
		this.c = c;
		this.table = table;
		this.id = Integer.toString(port);	
	}
	
	public String getId() {
		return id;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getServerPort() {
		return port;
	}
	
	public Coordinate getCoordinate() {
		return c;
	}

	public HashMap<String, String> getTable() {
		return table;
	}
	
    public void setTable(HashMap<String, String> table) {
    	this.table = table;
    }
	
    public void showTable() {
        System.out.println("Port " + getServerPort() + " files:");
        for (String key : table.keySet()) {
            System.out.println("Key: " + key.toString() + "\tValue: "+ table.get(key));
        }
    }

}
