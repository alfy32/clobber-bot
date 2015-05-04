package clobberbot;

public class BotPoint2D extends ImmutablePoint2D
{
    public BotPoint2D(double x, double y, int id)
    {
        super(x, y, id);
    }

    /** Creates a new object of the same class and with the same x,y and ID values */
    public Object copy()
    {
        return new BotPoint2D(x, y, getID());
    }

}
