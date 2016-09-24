package tw.nccu.edu.dht;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
 
 
public class CanNode extends NodeBase implements Runnable
{
	private String id;
    private Coordinate c ;
    private HashSet<Neighbour> neighbour = new HashSet<Neighbour>();
    private HashMap<String, String> table = new HashMap<String, String>();
    //private DataBase db;
 
    public CanNode(int port) throws IOException
    {
        super(port);
        this.id = Integer.toString(port);	// node id is as same as port number
        c = new Coordinate();  
    }
     
    public CanNode(){
    	super();
    }
 
    public String getId() {
    	return id;
    }

    public Coordinate getCoordinate() {
        return c;
    }
 
    public void addTable(Peer peer) {
        table.put(peer.getKey(), peer.getValue());
    }
    
    public void removeTable(Peer peer) {
    	table.remove(peer.getKey());
    }
    
    public void setTable(HashMap<String, String> table) {
    	this.table = table;
    }
    
    public HashMap<String, String> getTable() {
    	return table;
    }
    
    public void setNeighbour(HashSet<Neighbour> neighbour) {
    	this.neighbour = neighbour;
    }
    
    public HashSet<Neighbour> getNeighbour() {
    	return neighbour;
    }
 
    public int getRandomPoint() {
        Random r = new Random();
        return r.nextInt(10);
    }
    
	public boolean isNeighbour(Coordinate temp) {
		if(c.isLeftNeighbour(temp))
			return true;
		if(c.isRightNeighbour(temp))
			return true;
		if(c.isAboveNeighbour(temp))
			return true;
		if(c.isBelowNeighbour(temp))
			return true;
		else
			return false;
	}

    public void showTable() {
        System.out.println("My Files: ");
        for (String key : table.keySet()) {
            System.out.println("Key: " + key.toString() + "\tValue: "+ table.get(key));
        }
    }
     
    public void showNeighbour(){
        System.out.println("My Neighbours: ");
        for (Neighbour n : neighbour) {
            System.out.println("Ip: " + n.getIp() + "\tPort: "+ n.getServerPort());
        }
    }
   
    public void join(String ip, int port) throws IOException {    	
    	Coordinate joinCoordinate = splitCoordinate(ip, port);	// split coordinate to the new node
    	HashMap<String, String> joinTable = splitTable(ip, port);	// split and update the file table to the new node
    	Neighbour n = new Neighbour(ip, port, joinCoordinate, joinTable);
    	
    	// set the neighbor of the new node
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(outputStream);
		os.writeObject(neighbour);
		byte[] data = outputStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
		String message = "set neighbour";
		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(ip), port));
		serverSocket.send(sendPacket);
		
    	neighbour.add(n);	// add the new node as neighbor
    	addNeighbour(ip, port);	// add this node as neighbor of the new node
    	
    	for(Neighbour temp : neighbour) {
    		if(!isNeighbour(temp.getCoordinate())) {
    			neighbour.remove(temp);
    			message = "remove neighbour" + "," + getId();
        		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(temp.getIp()), temp.getServerPort()));
    		}
    	}
    }
    
    public void delete(String ip, int port) throws UnknownHostException, IOException {
    	// merge coordinate
    	Coordinate mergeCoordinate = new Coordinate();
    	String mergeIp = null;
    	int mergePort = 0;
    	for(Neighbour temp : neighbour) {
    		//double area = c.area();
    		if(temp.getCoordinate().isSameSize(c)) {
    			//area = temp.getCoordinate().area();
    			mergeIp = temp.getIp();
    			mergePort = temp.getServerPort();
    			mergeCoordinate = temp.getCoordinate();
    		}    		
    	}
    	
    	mergeCoordinate = mergeCoordinate.merge(c);    	
    	String message = "set coordinate" + "," + mergeCoordinate.getLowerx() + "," + mergeCoordinate.getLowery() +
    			"," + mergeCoordinate.getUpperx() + "," + mergeCoordinate.getUppery();
    	serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(mergeIp), mergePort));
    	
    	mergeTable(mergeIp, mergePort);
    	mergeNeighbour(mergeIp, mergePort);
    }
    
    public void mergeTable(String ip, int port) throws UnknownHostException, IOException {
    	HashMap<String, String> mergeTable = new HashMap<String, String>();
    	for(String key : table.keySet()) {
    		String value = table.get(key);
    		mergeTable.put(key, value);
    	}
 
   		String message = "set table";
		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(ip), port));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	ObjectOutputStream os = new ObjectOutputStream(outputStream);
    	os.writeObject(mergeTable);
    	byte[] data = outputStream.toByteArray();
    	DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
    	serverSocket.send(sendPacket);
    }
    
    public void mergeNeighbour(String ip, int port) throws IOException {
    	HashSet<Neighbour> mergeNeighbour = new HashSet<Neighbour>();
    	for(Neighbour temp : neighbour) {
    		mergeNeighbour.add(temp);
    	}
    	
    	String message = "set neighbour";
    	serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(ip), port));
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(outputStream);
		os.writeObject(mergeNeighbour);
		byte[] data = outputStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
		serverSocket.send(sendPacket); 
    }
    
    public void addNeighbour(String ip, int port) throws IOException {
		String message = "add neighbour" + "," + getIp() + "," + getServerPort();
		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(ip), port));
		
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(outputStream);
		os.writeObject(c);
		os.writeObject(table);
		os.flush();
		byte[] data = outputStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
		serverSocket.send(sendPacket);   
    }
    
    public Coordinate splitCoordinate(String ip, int port) throws IOException {
    	Coordinate splitCoordinate = new Coordinate();
    	if(c.isSplitVertical()) {
    		int x = c.getUpperx();
    		c.splitVertical();
    		splitCoordinate = new Coordinate(c.getUpperx(), c.getLowery(), x, c.getUppery());
    	} else {
    		int y = c.getUppery();
            c.splitHorizontal();
            splitCoordinate = new Coordinate(c.getLowerx(), c.getUppery(), c.getUpperx(), y);       
        }
    	String message = "set coordinate" + "," + splitCoordinate.getLowerx() + "," + splitCoordinate.getLowery() +
    			"," + splitCoordinate.getUpperx() + "," + splitCoordinate.getUppery();
		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(ip), port));
		return splitCoordinate;
    }
    
    public HashMap<String, String> splitTable(String ip, int port) throws IOException {
    	HashMap<String, String> splitTable = new HashMap<String, String> ();
    	for(String key : table.keySet()) {
    		String value = table.get(key);
    		Peer peer = new Peer(key, value);
            int x = peer.HashX(key);
            int y = peer.HashY(key);
            splitTable.put(key, value);
            if(!c.isContain(x, y)) {
            	String message = "add peer" + "," + peer.getKey() + "," + peer.getValue();
        		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(ip), port)); 
            	table.remove(key);
            }
    	}
    	return splitTable;
    }
    
    
    public void insert(int x, int y, Peer peer) throws UnknownHostException, IOException {
		for(Neighbour temp : neighbour) {
    		double distance = temp.getCoordinate().distance(x, y);
    		double min = Double.MAX_VALUE;
    		if(distance <= min) {
    			min = distance;
    		}
    		
    		if(temp.getCoordinate().isContain(x, y)) {
    			String message = "add peer";
    			serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(temp.getIp()), temp.getServerPort()));
    			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        		ObjectOutputStream os = new ObjectOutputStream(outputStream);
        		os.writeObject(peer);
        		byte[] data = outputStream.toByteArray();
        		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(temp.getIp()), temp.getServerPort());
        		serverSocket.send(sendPacket);
    		}
    	}
    	
    }
    
    public void remove(int x, int y, Peer peer) throws UnknownHostException, IOException {
		for(Neighbour temp : neighbour) {
    		double distance = temp.getCoordinate().distance(x, y);
    		double min = Double.MAX_VALUE;
    		if(distance <= min) {
    			min = distance;
    		}
    		
    		if(temp.getCoordinate().isContain(x, y)) {
    			String message = "delete peer";
    			serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(temp.getIp()), temp.getServerPort()));
    			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        		ObjectOutputStream os = new ObjectOutputStream(outputStream);
        		os.writeObject(peer);
        		byte[] data = outputStream.toByteArray();
        		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(temp.getIp()), temp.getServerPort());
        		serverSocket.send(sendPacket);
    		}
    	}
    }
    
    public void search(int x, int y, String key) {
    	String value = null;
		for(Neighbour temp : neighbour) {    		
    		if(temp.getCoordinate().isContain(x, y)) {
    			for(String str : temp.getTable().keySet()) {
    				if(str.equals(key)) {
    					value = temp.getTable().get(str);
    					System.out.println("search peer successfully. " + "Value is " + value);
    					break;
    				}
    			}
    		}
    	}
    }
    
    public void updateNeighbour() throws UnknownHostException, IOException {
    	for(Neighbour temp : neighbour) {
    		String message = "update table" + "," + getIp() + "," + getServerPort();
    		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(temp.getIp()), temp.getServerPort()));
    		
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		ObjectOutputStream os = new ObjectOutputStream(outputStream);
    		os.writeObject(table);
    		byte[] data = outputStream.toByteArray();
    		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(temp.getIp()), temp.getServerPort());
    		serverSocket.send(sendPacket);   
    	}
    }
    
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(true){
            try {
            	serverSocket.receive(dp);
                String message = new String(dp.getData(), 0, dp.getLength());
                String[] argument = message.split(",");
            	
            	if(message.contains("set coordinate")) {
                    int lx = Integer.parseInt(argument[1]);
                    int ly = Integer.parseInt(argument[2]);
                    int hx = Integer.parseInt(argument[3]);
                    int hy = Integer.parseInt(argument[4]);
                    c.setCoordinate(new Coordinate(lx,ly,hx,hy));
            	}
            	
            	else if(message.contains("show information")) {
            		System.out.println("----------");
            		c.show();
            		showNeighbour();
            		showTable();
            		System.out.println("----------");
            		System.out.println("Neighbour information:");
            		for(Neighbour temp : neighbour) {
            			temp.getCoordinate().show();
            			temp.showTable();
            		}
            	}
            	
            	else if(message.contains("join")) {
            		String joinIp = argument[1];
            		int joinPort = Integer.parseInt(argument[2]);
            		join(joinIp, joinPort);
            	}
            	
            	else if(message.contains("delete")) {
            		String deleteIp = argument[1];
            		int deletePort = Integer.parseInt(argument[2]);            		
            		delete(deleteIp, deletePort);
            	}
            	
            	else if(message.contains("insert peer")) {
            		String key = argument[1];
            		String value = argument[2];
            		int x = Integer.parseInt(argument[3]);
            		int y = Integer.parseInt(argument[4]);
            		Peer peer = new Peer(key, value);
            		if(c.isContain(x, y)) {
            			addTable(peer);
            			updateNeighbour();
            		} else {
            			insert(x, y, peer);
            		}
            	}
            	
            	else if(message.contains("remove peer")) {
            		String key = argument[1];
            		String value = argument[2];
            		int x = Integer.parseInt(argument[3]);
            		int y = Integer.parseInt(argument[4]);
            		Peer peer = new Peer(key, value);
            		if(c.isContain(x, y)) {
            			table.remove(key);
            			updateNeighbour();
            		} else {
            			remove(x, y, peer);
            		}
            	}
            	
            	else if(message.contains("update table")) {
            		//String ip = argument[1];
            		int port = Integer.parseInt(argument[2]);
            		byte[] incomingData = new byte[1024];
            		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            		serverSocket.receive(incomingPacket);
            		byte[] data = incomingPacket.getData();
            		ByteArrayInputStream in = new ByteArrayInputStream(data);
            		ObjectInputStream is = new ObjectInputStream(in);
            		HashMap<String, String> table = (HashMap<String, String>) is.readObject();

            		for(Neighbour temp : neighbour) {
            			if(temp.getServerPort() == port) {
            				temp.setTable(table);
            			}
            		}
            	}
            	
            	else if(message.contains("search")) {
            		String searchKey = argument[1];
            		String searchValue = null;
            		int x = Integer.parseInt(argument[2]);
            		int y = Integer.parseInt(argument[3]);
            		if(c.isContain(x, y)) {
            			for(String key : table.keySet()) {
            				if(key.equals(searchKey)) {
            					searchValue = table.get(searchKey);
            					System.out.println("search peer successfully. " + "Value is " + searchValue);          			
            				}
            			}            			
            			
            		} else {
            			search(x, y, searchKey);
            		}
            	}
            	
            	else if(message.contains("add peer")) {
            		byte[] incomingData = new byte[1024];
            		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            		serverSocket.receive(incomingPacket);
            		byte[] data = incomingPacket.getData();
            		ByteArrayInputStream in = new ByteArrayInputStream(data);
            		ObjectInputStream is = new ObjectInputStream(in);
            		Peer peer = (Peer) is.readObject();
            		addTable(peer);
            		updateNeighbour();
            	}
            	
            	else if(message.contains("delete peer")) {
            		byte[] incomingData = new byte[1024];
            		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            		serverSocket.receive(incomingPacket);
            		byte[] data = incomingPacket.getData();
            		ByteArrayInputStream in = new ByteArrayInputStream(data);
            		ObjectInputStream is = new ObjectInputStream(in);
            		Peer peer = (Peer) is.readObject();
            		table.remove(peer.getKey());
            		updateNeighbour();
            	}
            	
            	else if(message.contains("remove neighbour")) {
            		String removeId = argument[1];
            		for(Neighbour temp : neighbour) {
            			if(temp.getId() == removeId) {
            				neighbour.remove(temp);
            			}
            		}
            	}
            	
            	else if(message.contains("add neighbour")) {
            		String ip = argument[1];
            		int port = Integer.parseInt(argument[2]);           		      		
            		byte[] incomingData = new byte[1024];
            		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            		serverSocket.receive(incomingPacket);
            		byte[] data = incomingPacket.getData();
            		ByteArrayInputStream in = new ByteArrayInputStream(data);
            		ObjectInputStream is = new ObjectInputStream(in);
            		
            		Coordinate c  = (Coordinate) is.readObject();
            		HashMap<String, String> table = (HashMap<String, String>) is.readObject();
            		Neighbour n = new Neighbour(ip, port, c, table);
            		neighbour.add(n);
            	}
            	
            	else if(message.contains("set table")) {
            		byte[] incomingData = new byte[1024];
            		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            		serverSocket.receive(incomingPacket);
            		byte[] data = incomingPacket.getData();
            		ByteArrayInputStream in = new ByteArrayInputStream(data);
            		ObjectInputStream is = new ObjectInputStream(in);
					table = (HashMap<String, String>) is.readObject();
            	}
            	
            	else if(message.contains("set neighbour")) {
            		byte[] incomingData = new byte[1024];
            		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            		serverSocket.receive(incomingPacket);
            		byte[] data = incomingPacket.getData();
            		ByteArrayInputStream in = new ByteArrayInputStream(data);
            		ObjectInputStream is = new ObjectInputStream(in);
					neighbour = (HashSet<Neighbour>) is.readObject();
					
			    	for(Neighbour temp : neighbour) {    		
			    		if(!isNeighbour(temp.getCoordinate())) {
			    			neighbour.remove(temp);
			    			message = "remove neighbour" + "," + getId();
			        		serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(temp.getIp()), temp.getServerPort()));
			    		} else {
			    			addNeighbour(temp.getIp(), temp.getServerPort());
			    		}
			    	}
            	}
                                 
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
         
    }
}
