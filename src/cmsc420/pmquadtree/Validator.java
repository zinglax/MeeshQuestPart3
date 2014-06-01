package cmsc420.pmquadtree;

import cmsc420.pmquadtree.PMQuadtree.Black;
import cmsc420.pmquadtree.PMQuadtree.Gray;

public interface Validator {

	public boolean valid(Black node);

	public boolean valid(Gray gray);
}
