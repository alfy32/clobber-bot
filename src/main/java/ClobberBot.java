import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
   This class implements an example ClobberBot that makes random moves.  All ClobberBots should extend this
   class and override the takeTurn and drawMe methods.
*/
public abstract class ClobberBot
{
    public Dimension worldSize;
    public Random rand = new Random();
    public Color mycolor;
    public Clobber game;
    public List<ClobberBot> teammates = new ArrayList<ClobberBot>();
    protected int id;

    public ClobberBot(Clobber game)
    {
        mycolor = Color.white;
        this.game = game;
    }

    /**
     * Used by the game to inform each bot of its game ID. You can use this to search through the list of bot positions
     * you are passed each turn to differentiate between your teammates and your foes
     */
    public void informID(int id)
    {
        this.id = id;
    }

    /**
     * public method you can use to query for a bots game ID
     */
    public int getID()
    {
        return id;
    }

    public void setEnvironment(Dimension worldSize)
    {
        this.worldSize = new Dimension(worldSize);
    }

    /**
     * Used by the game to tell each bot who its teammates are before the game begins.
     */
    public void teammate(ClobberBot bot)
    {
        System.out.println("Adding teammate " + bot + " to " + this + "'s team.");
        teammates.add(bot);
    }

    /**
     * This method is called once for each bot for each turn. The bot should look at what it knows, and make an
     * appropriate decision about what to do.
     *
     * @param currState contains info on this bots current position, the position of every other bot and bullet in the
     * system.
     */
    public abstract ClobberBotAction takeTurn(WhatIKnow currState);

    /**
     * Draws the clobber bot to the screen. The drawing should be centered at the point me, and should not be bigger
     * than 9x9 pixels
     */
    public void drawMe(Graphics page, Point2D me)
    {
        int x, y;
        x = (int) me.getX() - Clobber.MAX_BOT_GIRTH / 2 - 1;
        y = (int) me.getY() - Clobber.MAX_BOT_GIRTH / 2 - 1;
        page.setColor(mycolor);
        page.fillOval(x, y, Clobber.MAX_BOT_GIRTH, Clobber.MAX_BOT_GIRTH);
    }

    /**
     * Your bots identifier string. It must be unique from other players, since I use it to determine who your teammates
     * are. You can include your login name in the id to guarantee uniqueness.
     */
    @Override
    public String toString()
    {
        return "ClobberBot with no name";
    }
}