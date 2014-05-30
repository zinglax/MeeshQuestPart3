package cmsc420.pmquadtree;

/**
 * A PM Quadtree of order 1 has the following rules:
 * 
 * 1. At most, one vertex can lie in a region represented by a quadtree leaf
 * node.
 * 
 * 2. All of the roads in the black leaf must connect to that city
 * 
 * 3. If there is no city, there can only be one road in the black node
 */
public class PM1Quadtree extends PMQuadtree {
	/**
	 * Constructs and initializes this PM Quadtree of order 1.
	 * 
	 * @param spatialWidth
	 *            width of the spatial map
	 * @param spatialHeight
	 *            height of the spatial map
	 */
	public PM1Quadtree(final int spatialWidth, final int spatialHeight) {
		super(new PM3Validator(), spatialWidth, spatialHeight, 1);
	}
}
