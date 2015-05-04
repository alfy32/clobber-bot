import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class BotManager extends ClobberObject
{
    /** Creating local bot to ensure multi-thread safety */
    private ClobberBot bot;
    private ClobberBotAction action;
    private BotStatTracker tracker;
    private int shotclock;
    private boolean perMatchTracking;

    public BotManager(ClobberBot bot, double x, double y) throws InstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException 
    {
        this.bot = constructInstance(bot);
        this.tracker = new BotStatTracker(bot);
        perMatchTracking = true;
        prep(x, y);
    }

    public BotManager(BotStatTracker botTracker, double x, double y) throws InstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException 
    {
        this.bot = constructInstance(botTracker.getBot());
        this.tracker = botTracker;
        perMatchTracking = false;
        prep(x, y);
    }

    @Override
    public boolean collision(ClobberObject o)
    {
        if (o instanceof BotManager)
            return (this.distance(o) < Clobber.bot_bot_thresh);
        if (o instanceof Bullet)
            return (this.distance(o) < Clobber.bot_bul_thresh);
        return false;
    }
    
    @Override
    public String toString()
    {
        if (perMatchTracking)
            return tracker.toString();
        return "The stats for this individual match are unavailable.";
    }

    public void incrementDeaths() {
        tracker.incrementDeaths();
    }

    public void incrementSurvival() {
        tracker.incrementSurvival();
    }

    public void incrementExceptions() {
        tracker.incrementExceptions();
    }

    public void incrementLifespan() {
        tracker.incrementLifespan();
    }

    public void addKills(int kills) {
        tracker.addKills(kills);
    }

    public int getShotclock() {
        return shotclock;
    }

    public void setShotclock(int shotclock) {
        this.shotclock = shotclock;
    }

    public void decrementShotclock() {
        this.shotclock--;
    }

    public void takeTurn(WhatIKnow whatIKnow) {
        action = bot.takeTurn(whatIKnow);
    }

    public int getAction() {
        return action.getAction();
    }

    public void nullifyAction() {
        this.action = null;
    }

    public boolean hasAction() {
        return action != null;
    }

    public void drawMe(Graphics page) {
        bot.drawMe(page, pos);
    }

    public ClobberBot getBot() {
        return bot;
    }

    public BotStatTracker getTracker() {
        return tracker;
    }

    private ClobberBot constructInstance(ClobberBot bot) throws InstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException 
    {
        Class c = bot.getClass();
        Class[] carr = {Clobber.class};
        Constructor<ClobberBot> con = c.getConstructor(carr);
        return con.newInstance(bot.game);
    }

    private void prep(double x, double y) {
        pos = new Point2D.Double(x, y);
        oldpos = new Point2D.Double(x, y);
        deleteMe = false;
        clobberObjectID = Clobber.getNextID();
        bot.informID(clobberObjectID);
        minX = Clobber.min_x + Clobber.BOT_EDGE_BUFFER;
        maxX = Clobber.max_x - Clobber.BOT_EDGE_BUFFER;
        minY = Clobber.min_y + Clobber.BOT_EDGE_BUFFER;
        maxY = Clobber.max_y - Clobber.BOT_EDGE_BUFFER;
    }
}
