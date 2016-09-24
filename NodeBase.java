package tw.nccu.edu.dht;
 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
 
public abstract class NodeBase
{
    private final static String MULTICAST_IP = "224.5.5.5";
    private final static int MULTICAST_PORT = 1001;
    private String nodeIP = null;
    private int nodePort = 0;
     
    protected MulticastSocket ms;   
    protected DatagramSocket clientSocket;
    protected DatagramSocket serverSocket;
    protected DatagramPacket dp;
    protected byte[] buffer;
     
    public NodeBase(int port) throws IOException
    {
        this.nodeIP = InetAddress.getLocalHost().getHostAddress();
        this.nodePort = port;
        System.out.println("node IP is:" + nodeIP);
        System.out.println("node Port is:" + nodePort);
         
        ms = createMulticastSocket(MULTICAST_PORT);
        ms.joinGroup(InetAddress.getByName(MULTICAST_IP));
        
        buffer = new byte[1024];
        dp = new DatagramPacket(buffer, buffer.length);     
        try{
            serverSocket = new DatagramSocket(port, InetAddress.getByName(getIp()));
        }catch(SocketException ex){
            ex.printStackTrace();
        }        
    }
 
    public NodeBase() {
        // TODO Auto-generated constructor stub
    }
 
    public String getIp()
    {
        return this.nodeIP;
    }
 
    public void setIp(String ip)
    {
        this.nodeIP = ip;
    }
 
    public int getServerPort()
    {
        return this.nodePort;
    }
    
    public void setServerPort(int port)
    {
    	this.nodePort = port;
    }
 
    public static MulticastSocket createMulticastSocket(int port)
    {
        MulticastSocket multicastSocket = null;
        try{
            multicastSocket = new MulticastSocket(port);
        }catch (IOException e){
            e.printStackTrace();
        }
        return multicastSocket;
    }
 
    public String receiveMessage() throws IOException
    {
        byte[] buffer = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        try
        {
            ms.receive(dp);
            String result = new String(buffer, 0, dp.getLength());
            //System.out.println("Node: " + nodeIP + ":" + nodePort + " receive message: " + result);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
 
    }
 
    public void sendMessage(String str) throws IOException, InterruptedException
    {
        DatagramPacket dp = null;
        try
        {
            //System.out.println("Node: " + nodeIP + ":" + nodePort + " send message: " + str);
            byte[] data = str.getBytes();
            dp = new DatagramPacket(data, data.length, InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT);
            ms.send(dp);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
     
}
