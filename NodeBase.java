package tw.nccu.edu.dht;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class NodeBase {
	private String ip = null;
	private int port = 0;
	private ServerSocket server;
	private MulticastSocket ms;
	InetAddress ia = InetAddress.getByName("224.5.5.5");  // broadcast IP

	public NodeBase(int port, String type) throws IOException {
		if (type == "S") {
			this.ip = InetAddress.getLocalHost().getHostAddress();
			this.port = port;
			server = createSocketServer(port);
		} 
		else {
			this.port = port;
			this.ip = InetAddress.getLocalHost().getHostAddress();
			ms = createMulticastSocket(port);
			ms.joinGroup(ia);
		}
	}

	public String getIp() {
		return this.ip;
	}
	
	public void setIp(String ip){
		this.ip = ip;
	}

	public int getServerPort() {
		return this.port;
	}

	public static ServerSocket createSocketServer(int port) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return serverSocket;
	}

	private Socket client;
	private BufferedInputStream in;
	private BufferedOutputStream out;

	public void start() {
		while (true) {
			try {
				synchronized (server) {
					client = server.accept();
					System.out.println("Get Connection : InetAddress = "
							+ client.getInetAddress());
				}
				client.setSoTimeout(15000);
				in = new BufferedInputStream(client.getInputStream());
				out = new BufferedOutputStream(client.getOutputStream());
				process(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	abstract protected void process(BufferedInputStream input,
			BufferedOutputStream output);

	protected void sendMessage(String targetAddress, int targetPort,
			String message) throws IOException {
		Socket sink = new Socket();
		InetSocketAddress isa = new InetSocketAddress(targetAddress, targetPort);
		System.out.println("Connecting...");
		try {
			sink.connect(isa, 10000);
			BufferedOutputStream out = new BufferedOutputStream(
					sink.getOutputStream());
			out.write(message.getBytes());
			out.close();
			System.out.println("Successful");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sink.close();
		}
	}

	
	public static MulticastSocket createMulticastSocket(int port) {
		MulticastSocket multicastSocket = null;
		try {
			multicastSocket = new MulticastSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return multicastSocket;
	}

	public String receive()
    {
    	String str = new String();
    	try{
    		byte[] buffer = new byte[1024];
    		while(true){
    			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
    			ms.receive(dp);
    			str = new String(dp.getData(), 0, dp.getLength());
    			System.out.println("Node: " + ip + " receive message: " + str);
    			break;
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	return str;
    }

	public void send(String str)
    {
    	try{
    		System.out.println("Node: " + ip + " send message: " + str);
    		byte[] data = str.getBytes();
    		DatagramPacket dp = new DatagramPacket(data, data.length, ia, port);
    		ms.send(dp);
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
