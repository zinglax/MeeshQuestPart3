package cmsc420.pmquadtree;


import cmsc420.pmquadtree.PMQuadtree.Black;
import cmsc420.pmquadtree.PMQuadtree.Gray;

public class PM3Validator implements Validator {
	
	public boolean valid(final Black node) {
		return (node.getNumPoints() <= 1);
	}
	
	public boolean valid(final Gray node){
		if (node.halfHeight < 1 || node.halfWidth < 1){
			return false;
		} else {
			return true;
		}
	}
}
