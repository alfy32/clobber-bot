package clobberbot;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clobber is a great game where bots shoot the heck out of each other.
 */
public class Clobber
{
    @Parameter(names = "-killPoints", description = "How many points for killing")
    public static final int KILLPOINTS = 1;
    @Parameter(names = "-survivalPoints", description = "How many points for surviving")
    public static final int SURVIVALPOINTS = 4;
    @Parameter(names = "-bulletStepSize", description = "How many pixels a bullet moves per update")
    public static final int BULLETSPEED = 4;
    @Parameter(names = "-botStepSize", description = "How many pixels a bot moves per update")
    public static final int BOTSPEED = 2;
    @Parameter(names = "-shotFrequency", description = "How often bots are allowed to shoot (milliseconds)")
    public static final int SHOTFREQUENCY = 20;
    @Parameter(names = "-loaddir", description = "Directory to load player bots from")
    public List<String> playerDirNames;
    @Parameter(names = "-loadplayer", description = "Load a player bot")
    public List<String> playerFileNames;
    @Parameter(names = "-framerate", description = "How fast to display battle")
    public static final int FRAMERATE = 60;
    @Parameter(names = "-teamsize", description = "How many bots per team")
    public static final int TEAMSIZE = 1;
    @Parameter(names = "-nodisplay", description = "Don't show GUI")
    public static final int EDGE_BUFFER_LEFT = 0;
    public static final int EDGE_BUFFER_TOP = 0;
    public static final int EDGE_BUFFER_RIGHT = 0;
    public static final int EDGE_BUFFER_BOTTOM = 0;
    public static final int BOT_EDGE_BUFFER = 7;
    public static final int BULLET_EDGE_BUFFER = -2;
    public static final int MAX_BOT_GIRTH = 15;
    public static final int BULLET_GIRTH = 5;
    public static final int MIN_START_DISTANCE = MAX_BOT_GIRTH * 4;
    public static final int _width = 600;
    public static final int _height = _width;
    public static final int min_x = EDGE_BUFFER_LEFT;
    public static final int min_y = EDGE_BUFFER_TOP;
    public static final int max_x = _width - EDGE_BUFFER_RIGHT;
    public static final int max_y = _height - EDGE_BUFFER_BOTTOM;
    public static final int bot_bul_thresh = (MAX_BOT_GIRTH + BULLET_GIRTH) / 2;
    public static final int bot_bot_thresh = (MAX_BOT_GIRTH);
    private static int nextID = 0;

    /**
     * returns the number of turns that must elapse between shots
     */
    public int getShotFrequency()
    {
        return SHOTFREQUENCY;
    }

    public int getMinX()
    {
        return min_x;
    }

    public int getMaxX()
    {
        return max_x;
    }

    public int getMinY()
    {
        return min_y;
    }

    public int getMaxY()
    {
        return max_y;
    }

    public int getBotStepSize()
    {
        return BOTSPEED;
    }

    public int getBulletStepSize()
    {
        return BULLETSPEED;
    }

    public static int getNextID() {
        return nextID++;
    }

    protected Clobber()
    {
        playerFileNames = new ArrayList<String>();
        playerDirNames = new ArrayList<String>();
    }

    public static Clobber create(String[] args)
    {
        Clobber clobber = new Clobber();
        new JCommander(clobber, args);
        return clobber;
    }
}
