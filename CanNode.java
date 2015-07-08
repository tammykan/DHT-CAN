package tw.nccu.edu.dht;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class CanNode extends NodeBase implements Runnable {

	Coordinate c = new Coordinate();
	// Hashtable<String,Coordinate>(); // neighborhood table
	HashSet<CanNode> neighbour = new HashSet<CanNode>();
	HashMap<String, String> table = new HashMap<String, String>(); // (key,value) table
	private static int nodeNum = 0;
	private static CanNode bootstrap;

	public CanNode(int port) throws IOException {
		super(port);
		c = null;
	}

	public CanNode(int port, Coordinate c, String type) throws IOException {
		super(port);
		this.c = c;
	}

	public Coordinate getCoordinate() {
		return c;
	}

	public void addNeighbours(CanNode node) {
		neighbour.add(node);
	}

	public void addTable(Peer peer) {
		table.put(peer.getKey(), peer.getValue());
	}

	/*
	 * public void showNeighbours(){ System.out.println("My Neighbours: ");
	 * Enumeration<String> e = neighbour.keys(); while(e.hasMoreElements()){
	 * String s = e.nextElement().toString(); // neighbour's IP Coordinate c =
	 * (Coordinate) neighbour.get(s); // neighbour's Coordinate
	 * System.out.println(s); c.showCoordinate(); } }
	 */

	public void showTable() {
		System.out.println("My Files: ");
		for (Object key : table.keySet()) {
			   System.out.println("Key: " + key.toString() + "\tValue: "+ table.get(key));
			   }
	}
	
	public int getRandomPoint() {
		Random r = new Random();
		return r.nextInt(10);
	}

	public void join() throws IOException {
		if (nodeNum == 0) {
			System.out.println("Join the first Node.");
			bootstrap = this;
			c = new Coordinate(0, 0, 10, 10);
			nodeNum += 1;
		} else {
			nodeNum += 1;
			System.out.println("Join the node N" + nodeNum);
			int x = bootstrap.getRandomPoint();
			int y = bootstrap.getRandomPoint();
			CanNode node = routeNode(x, y);
			node.split(this);
			node.addNeighbours(this);
		}
	}

	public void delete() {

		if (nodeNum == 0)
			System.out.println("Please join the first Node.");
		else {
			nodeNum -= 1;
			System.out.println("Delete the node.");
		}
	}

	/* update the neighborhood */
	public void updateNeighbours() {

	}

	public void updateTable(String key, String value) {

	}

	public void split(CanNode node) throws IOException {
		if (c.isSplitVertical()) {
			int x = c.getUpperx();
			c.splitVertical();
			node.c = new Coordinate(c.getUpperx(), c.getLowery(), x,
					c.getUppery()); // set new coordinate
		} else {
			int y = c.getUppery();
			c.splitHorizontal();
			node.c = new Coordinate(c.getLowerx(), c.getUppery(),
					c.getUpperx(), y); // set new coordinate
		}
	}

	public void insertPeer(Peer peer) throws IOException {
		int x = peer.HashX(peer.getKey());
		int y = peer.HashY(peer.getKey());
		if (c.isContain(x, y)) {
			addTable(peer); // add peer's information to the node table
		} else {
			CanNode node = routeNode(x, y);
			node.addTable(peer);
		}
	}

	public String searchPeer(String key) {
		Peer peer = new Peer();
		int x = peer.HashX(key);
		int y = peer.HashY(key);

		if (c.isContain(x, y)) {
			if (table.containsKey(key))
				return table.get(key).toString(); // return value
			else
				return ("Failure");
		} else {

		}
		return null;
	}

	public CanNode routeNode(int x, int y) throws IOException {
		CanNode node = new CanNode(getServerPort());
		if (bootstrap.c.isContain(x, y))
			return bootstrap;
		else {
			double distance = Double.MAX_VALUE;
			Iterator<CanNode> iterator = neighbour.iterator();
			CanNode temp = new CanNode(getServerPort());
			while (iterator.hasNext()) {
				temp = iterator.next();
				if (temp.c.distance(x, y) <= distance) {
					distance = temp.c.distance(x, y);
					node = temp;
				}
			}
		}
		return node;
	}

	@Override
	protected void process(BufferedInputStream input,
			BufferedOutputStream output) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

}
