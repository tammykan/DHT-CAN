package tw.nccu.edu.dht;

import java.io.IOException;
import java.io.Serializable;

public class Peer implements Serializable {

	private static final long serialVersionUID = 1L;
	private String key;
	private String value;
	private int x;
	private int y;
	
	public Peer(String key, String value) throws IOException {
		setX(0);
		setY(0);
		setKey(key);
		setValue(value);
	}
	
	public Peer(){
		setX(0);
		setY(0);
		setKey(null);
		setValue(null);
	}
	
	public int HashX(String key){
		int sum=0;
		
		if(key.length() == 1)
			return 0;
		else
			for(int i=0;i<key.toCharArray().length;i++)
				if(i % 2 != 0)
					sum+= key.charAt(i);		
		int hashx = sum % 10;
		return hashx;
	}
	
	public int HashY(String key){
		int sum=0;
		
		if(key.length() == 1)
			return 1;
		else
			for(int i=0;i<key.toCharArray().length;i++)
				if(i % 2 == 0)
					sum+= key.charAt(i);	
		int hashy = sum % 10;
		return hashy;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public int setY(int y) {
		this.y = y;
		return y;
	}
	
}
