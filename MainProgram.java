package tw.nccu.edu.dht;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainProgram {

	public static void main(String[] args) throws IOException {

		CanNode node1 = new CanNode(1001);
		CanNode node2 = new CanNode(1001);
		CanNode node3 = new CanNode(1001);
		CanNode node4 = new CanNode(1001);
		CanNode node5 = new CanNode(1001);
		
		Thread node1Thread = new Thread(node1);
		Thread node2Thread = new Thread(node2);
		Thread node3Thread = new Thread(node3);
		Thread node4Thread = new Thread(node4);
		Thread node5Thread = new Thread(node5);
		
		node1Thread.start();
		node2Thread.start();
		node3Thread.start();
		node4Thread.start();
		node5Thread.start();

		node1.join();
		node2.join();
		node3.join();
		node4.join();
		node5.join();
		
		while(true){
			BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
			String key = buf.readLine();
			String value = buf.readLine();		
			Peer peer = new Peer(key,value);
			node1.insertPeer(peer);
			System.out.println(node1.searchPeer(key));
			node2.showTable();
			node1.showTable();
		}
		
		
		
	}

}
