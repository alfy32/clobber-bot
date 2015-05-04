import java.awt.geom.Point2D;

abstract class ClobberObject
{
    protected int clobberObjectID;
    protected boolean deleteMe;
    protected Point2D oldpos;
    protected Point2D pos;
    protected int maxY;
    protected int minY;
    protected int maxX;
    protected int minX;

    public void updatePosition(int x, int y)
    {
        int newX = x < minX ? minX: x;
        newX = newX >= maxX ? maxX : newX;
        
        int newY = y < minY ? minY: y;
        newY = newY >= maxY ? maxY : newY;

        oldpos.setLocation(pos);
        pos.setLocation(newX, newY);
    }

    public double distance(ClobberObject o)
    {
        return pos.distance(o.pos);
    }

    public abstract boolean collision(ClobberObject o);
}