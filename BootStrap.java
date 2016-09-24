package tw.nccu.edu.dht;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import tw.nccu.edu.dht.ServerGUI.CoordinateSystem;

public class BootStrap extends NodeBase implements Runnable {
	
	private static HashMap<Integer, String> list = new HashMap<Integer, String>();	// node list
	private static ArrayList<Integer> index = new ArrayList<Integer>();
	private static int nodeNum = 0;
	private static Random random = new Random();
	private CoordinateSystem coordinate;

	public BootStrap(int port) throws IOException {
		super(port);	// bootstrap port
	}
	
	public static void main(String[] args) throws IOException {
		
		BootStrap bootstrap = new BootStrap(999);
		new Thread(bootstrap).start();
		System.out.println("Bootstrap start.");
		System.out.println("--------------------");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true) {
			String message;
			try {
				message = receiveMessage();
				String[] argument = message.split(",");
				message = argument[0];
				switch(message) {
				case "join":
					System.out.println("Node join.");
					String ip = argument[1];
					int port = Integer.parseInt(argument[2]);				
					if(nodeNum == 0) {
						Coordinate c = new Coordinate(0,0,10,10);
						message = "set coordinate" + "," + c.getLowerx() + "," + c.getLowery() +
								"," + c.getUpperx() + "," + c.getUppery();
						serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(ip), port)); 
					} else {
						int i = random.nextInt(index.size());
						int joinPort = index.get(i);
						String joinIp = list.get(joinPort);
						message = "join" + "," + ip + "," + port;
						serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(joinIp), joinPort)); 
					}
					list.put(port, ip);
					index.add(port);
					nodeNum += 1;
					listToString();
					break;
					
				case "delete":
					System.out.println("Node delete.");
					String deleteIp = argument[1];
					int deletePort = Integer.parseInt(argument[2]);	
					if(nodeNum == 0)
						System.out.println("Empty network.");
					else {
						//int i = random.nextInt(index.size());
						//int startPort = index.get(i);
						//String startIp = list.get(startPort);
						message = "delete" + "," + deleteIp + "," + deletePort;
						serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(deleteIp), deletePort)); 
					}
					list.remove(deletePort, deleteIp);
					index.remove((Integer)deletePort);
					nodeNum -= 1;
					listToString();
					break;
					
				case "insert":
					String key = argument[1];
					String value = argument[2];
			    	Peer peer = new Peer(key, value);
			    	int x = peer.HashX(key);
			    	int y = peer.HashY(key);
			    	message = "insert peer" + "," + key + "," + value + "," + x + "," + y;
					int i = random.nextInt(index.size());
					int startPort = index.get(i);
					String startIp = list.get(startPort);
			    	serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(startIp), startPort)); 
			    	break;
			    	
				case "remove":
					String removeKey = argument[1];
					String removeValue = argument[2];
			    	Peer removePeer = new Peer(removeKey, removeValue);
			    	int rx = removePeer.HashX(removeKey);
			    	int ry = removePeer.HashY(removeKey);
			    	message = "remove peer" + "," + removeKey + "," + removeValue + "," + rx + "," + ry;
					int k = random.nextInt(index.size());
					int removePort = index.get(k);
					String removeIp = list.get(removePort);
			    	serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(removeIp), removePort)); 
			    	break;
					
				
				case "search":
					String searchKey = argument[1];
					Peer speer = new Peer();
					int sx = speer.HashX(searchKey);
			    	int sy = speer.HashY(searchKey);
			    	message = "search" + "," + searchKey + "," + sx + "," + sy;
					int j = random.nextInt(index.size());
					int searchPort = index.get(j);
					String searchIp = list.get(searchPort);
			    	serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(searchIp), searchPort)); 
			    	break;
			    	
				
				case "list":
					System.out.println("Show node information");
					for(int targetPort : list.keySet()) {
						String targetIp = list.get(targetPort);
						message = "show information";
						serverSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,InetAddress.getByName(targetIp), targetPort));
					}
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void listToString() {
		System.out.println(list.size());
		for(int port : list.keySet()) {
			String ip = list.get(port);
			System.out.println("List ip: " + ip + " port: " + port);
		}
	}

}
