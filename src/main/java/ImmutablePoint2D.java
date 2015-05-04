

import java.awt.geom.*;
import java.util.logging.Logger;

/**
 * This class provides an immutable form of the Point2D.Double class
 */
public class ImmutablePoint2D extends Point2D.Double
{
    private final int id;
    private static final Logger LOG = Logger.getLogger(ImmutablePoint2D.class.getName());

    public ImmutablePoint2D(double x, double y, int id)
    {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    /**
     * This method returns the ID of this point.
     */
    public int getID()
    {
        return id;
    }

    /**
     * This operation is not supported by ImmutablePoint2D
     */
    @Override
    public void setLocation(Point2D pos)
    {
        LOG.warning("Someone is trying to alter an ImmutablePoint2D");
    }

    /**
     * This operation is not supported by ImmutablePoint2D
     */
    @Override
    public void setLocation(double x, double y)
    {
        LOG.warning("Someone is trying to alter an ImmutablePoint2D");
    }

    /**
     * Creates a new object of the same class and with the same x,y and ID values
     */
    public Object copy()
    {
        return new ImmutablePoint2D(x, y, id);
    }

    @Override
    public String toString()
    {
        return "(" + getID() + ", " + getX() + ", " + getY() + ")";

    }
}
