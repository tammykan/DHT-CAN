package tw.nccu.edu.dht;

public class Coordinate {
	
	private int lowerx, lowery;
	private int upperx, uppery;
	private int centerx, centery;
	
	public Coordinate(int lowerx,int lowery,int upperx,int uppery){
		
		this.lowerx = lowerx;
		this.lowery = lowery;
		this.upperx = upperx;
		this.uppery = uppery;
		this.centerx = (lowerx+upperx)/2;
		this.centery = (lowery+uppery)/2;
	}
	
	public Coordinate(){
		this(0,0,0,0);
	}
	
	public void setCoordinate(Coordinate c){
		this.lowerx = c.lowerx;
		this.lowery = c.lowery;
		this.upperx = c.upperx;
		this.uppery = c.uppery;
	}
	
	public void show(){
		System.out.println("Node Coordinate: ");
		System.out.println("Lower x: " + getLowerx() + " Lower y: " + getLowery());
		System.out.println("Upper x: " + getUpperx() + " Upper y: " + getUppery());
		//System.out.println("Center x: " + (getLowerx() + getUpperx())/2);
		//System.out.println("Center y: " + (getLowery() + getUppery())/2);
	}
	
	public boolean isContain(int x, int y){
		
		if(lowerx <= x && upperx >= x && lowery <= y && uppery >= y)
			return true;
		return false;
	}
	
	public double distance(int x, int y){
		
		return Math.sqrt(Math.pow(centery-y, 2) + Math.pow(centerx-x, 2));		
	}
	
	public boolean isSplitVertical(){
		
		if(upperx-lowerx >= uppery-lowery)
			return true;
		return false;
	}
	
	public void splitVertical(){	
		upperx = (lowerx+upperx)/2;
		centerx = (lowerx+upperx)/2;
		centery = (lowery+uppery)/2;
	}
	
	public void splitHorizontal(){	
		uppery = (lowery+uppery)/2;
		centerx = (lowerx+upperx)/2;
		centery = (lowery+uppery)/2;
	}
	
	public Coordinate updateCoordinate(Coordinate c){
		if(c.lowerx >= upperx)
			c.lowerx = lowerx;
		else if(c.upperx <= lowerx)
			c.upperx = upperx;
		if(c.lowery >= uppery)
			c.lowery = lowery;
		else if(c.uppery <= lowery)
			c.uppery = lowery;
		
		return c;
	}
	
	public int getLowerx(){
		return lowerx;
	}
	
	public int getLowery(){
		return lowery;
	}
	
	public int getUpperx(){
		return upperx;
	}
	
	public int getUppery(){
		return uppery;
	}
}
