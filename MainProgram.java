package tw.nccu.edu.dht;

import java.io.IOException;

public class MainProgram {

	public static void main(String[] args) throws IOException {

		CanNode node1 = new CanNode(1001);
		CanNode node2 = new CanNode(1001);
		CanNode node3 = new CanNode(1001);
		
		Thread node1Thread = new Thread(node1);
		Thread node2Thread = new Thread(node2);
		Thread node3Thread = new Thread(node3);
		
		node1Thread.start();
		node2Thread.start();
		node3Thread.start();

		node1.join();
		node2.join();
		node3.join();
		
		String key = "happy";
		String value = "watching tv show";
		Peer peer = new Peer(key, value);
		node1.insertPeer(peer);
		node2.showTable();

	}

}
