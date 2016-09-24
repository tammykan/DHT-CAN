package tw.nccu.edu.dht;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
	
    private static Scanner scanner;
    
    public static void main(String[] args) throws IOException, InterruptedException
    {
        int port = (Math.abs(new Random().nextInt()) % 2000) + 1002;
        System.out.println("port " + port);
        CanNode node = new CanNode(port);
        new Thread(node).start();
        System.out.println("--------------------");
        node.sendMessage("join" + "," + node.getIp() + "," + port);
         
        while(true){
        	System.out.println("Please enter the command: ");
            scanner = new Scanner(System.in);         
            String command = scanner.next();
            String[] arguments = command.split(",");
            String message = null;
            
            switch(arguments[0]) {
            
            case "delete":
            	message = "delete" + "," + node.getIp() + "," + node.getServerPort();
            	node.sendMessage(message);
            	break;
            
            case "insert":
				RdfFunction rdf = new RdfFunction();
				String rdfString = rdf.createRDF("10.txt");
				String argument[] = rdfString.split(";");
				for(int i=1;i<11;i++) {
					String arg[] = argument[i].split(",");
					String key = arg[0];
					String value = arg[1];
					message = "insert" + "," + key + "," + value;
				}
				break;
				
            case "search":
            	String key = arguments[1];
            	message = "search" + "," + key;
            	break;
            	
            case "remove":
            	String removeKey = arguments[1];
            	String removeValue = arguments[2];
            	message = "remove" + "," + removeKey + "," + removeValue;
				
            case "list":
            	message = "list";
            	break;
            	
            }
            node.sendMessage(message);
            
        }
    }
}
