package tw.nccu.edu.dht;

import java.io.Serializable;

public class Coordinate implements Serializable {

	private static final long serialVersionUID = 1L;
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
		System.out.println("Center x: " + (getLowerx() + getUpperx())/2);
		System.out.println("Center y: " + (getLowery() + getUppery())/2);
	}
	
	public boolean isContain(int x, int y){
		
		if(lowerx <= x && upperx >= x && lowery <= y && uppery >= y)
			return true;
		return false;
	}
	
	public boolean isLeftNeighbour(Coordinate c){
		if(c.upperx == lowerx && c.uppery > lowery
				&& c.lowery < uppery && c.lowerx < upperx)
			return true;
		return false;
	}
	
	public boolean isAboveNeighbour(Coordinate c){
		if(c.lowery == uppery && c.upperx > lowerx
				&& c.uppery > lowery && c.lowerx < upperx)
			return true;
		return false;
	}
	
	public boolean isRightNeighbour(Coordinate c){
		if(c.lowerx == upperx && c.lowery < uppery
				&& c.upperx > lowerx && c.uppery > lowery)
			return true;
		return false;
	}
	
	public boolean isBelowNeighbour(Coordinate c){
		if(c.uppery == lowery && c.lowerx < upperx
				&& c.lowery < uppery && c.upperx > lowerx)
			return true;
		return false;
	}
	
	public double area() {
		double area;
		area = (upperx-lowerx) * (uppery-lowery);
		return area;
	}
	
	public boolean isSameSize(Coordinate c){
		if( ((upperx-lowerx) == (c.upperx-c.lowerx)) && ((uppery-lowery) == (c.uppery-c.lowery)) )
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
	
	public Coordinate merge(Coordinate c) {
		Coordinate mergeCoordinate = new Coordinate();
		if(upperx == c.lowerx && uppery == c.uppery && lowery == c.lowery) {
			mergeCoordinate = new Coordinate(lowerx, lowery, c.upperx, uppery);
		}
		else if(lowerx == c.upperx && uppery == c.uppery && lowery == c.lowery) {
			mergeCoordinate = new Coordinate(c.lowerx, lowery, upperx, uppery);
		}
		else if(lowerx == c.lowerx && upperx == c.upperx && uppery == c.lowery) {
			mergeCoordinate = new Coordinate(lowerx, lowery, upperx, c.uppery);
		}
		else if(lowerx == c.lowerx && upperx == c.upperx && lowery == c.uppery) {
			mergeCoordinate = new Coordinate(lowerx, c.lowery, upperx, uppery);
		}
		return mergeCoordinate;
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
