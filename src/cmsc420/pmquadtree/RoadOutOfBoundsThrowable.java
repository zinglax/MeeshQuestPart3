package cmsc420.pmquadtree;

public class RoadOutOfBoundsThrowable extends Throwable{
	private static final long serialVersionUID = 1L;

	public RoadOutOfBoundsThrowable(){
		
	}
	
	public RoadOutOfBoundsThrowable(String msg){
		super(msg);
	}
}
