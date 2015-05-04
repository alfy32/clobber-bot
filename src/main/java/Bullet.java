import java.awt.geom.Point2D;

class Bullet extends ClobberObject
{
    private BotManager owner;
    private int xplus;
    private int yplus;

    public Bullet(BotManager owner, double x, double y, int xplus, int yplus)
    {
        this.owner = owner;
        pos = new Point2D.Double(x, y);
        oldpos = new Point2D.Double(x, y);
        this.xplus = xplus;
        this.yplus = yplus;
        deleteMe = false;
        clobberObjectID = Clobber.getNextID();
        minX = Clobber.min_x + Clobber.BULLET_EDGE_BUFFER;
        maxX = Clobber.max_x - Clobber.BULLET_EDGE_BUFFER;
        minY = Clobber.min_y + Clobber.BULLET_EDGE_BUFFER;
        maxY = Clobber.max_y - Clobber.BULLET_EDGE_BUFFER;
    }

    @Override
    public void updatePosition(int x, int y)
    {
        oldpos.setLocation(pos);
        pos.setLocation(x, y);
    }

    @Override
    public boolean collision(ClobberObject o)
    {
        if (o instanceof Bullet)
            return (this.distance(o) < Clobber.bot_bul_thresh);
        return false;
    }

    public BotManager getOwner() {
        return owner;
    }
    
    public void move() {
        this.updatePosition( ((int) pos.getX()) + xplus, ((int) pos.getY()) + yplus);
        if (pos.getX() < minX || pos.getX() > maxX || pos.getY() < minY || pos.getY() > maxY) {
            this.deleteMe = true;
        }
    }
    
    public BulletPoint2D getStaticCopy() {
        return new BulletPoint2D(pos.getX(), pos.getY(), clobberObjectID, xplus, yplus);
    }
    
}
