import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * The actual fighting happens here. External classes can read state for display
 */
public class Match implements Runnable
{
    private static final Logger LOG = Logger.getLogger(Match.class.getName());
    private static final int MAXTURNS = 2500;
    List<BotManager> bots = new ArrayList<BotManager>();
    List<Bullet> bullets = new ArrayList<Bullet>();
    private List<BotManager> deadbots = new ArrayList<BotManager>();
    private List<BotStatTracker> results;
    private Clobber game;
    private int turnsLeft = MAXTURNS;
    private boolean tallied = false;
    private volatile JPanel display = null;

    private Match(Clobber game)
    {
        this.game = game;
    }

    public Match(Clobber game, List<ClobberBot> bots, PlacementStrategy placement, JPanel display)
    {
        this.game = game;
        this.display = display;
        List<ImmutablePoint2D> spots = placement.place(bots.size()*Clobber.TEAMSIZE, Clobber.MIN_START_DISTANCE/2, Clobber._height);
        for (ClobberBot clobberBot : bots) 
        {
			for (int i = 0; i < Clobber.TEAMSIZE; i++)
			{
				ImmutablePoint2D spot = spots.get(0);
				spots.remove(0);
				try {
					BotManager manager = new BotManager(clobberBot, spot.x, spot.y);
					this.bots.add(manager);
					addEventListeners(clobberBot, display, manager);
				} catch (Exception ex) {
					Logger.getLogger(Match.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
        }
        if (Clobber.TEAMSIZE > 1) {
            informTeams();
        }
    }

    public Match(Clobber game, List<BotStatTracker> bots, PlacementStrategy placement)
    {
        this.game = game;
        List<ImmutablePoint2D> spots = placement.place(bots.size(), Clobber.MIN_START_DISTANCE/2, Clobber._height);
        for (BotStatTracker clobberBot : bots) 
        {
            ImmutablePoint2D spot = spots.get(0);
            spots.remove(0);
            try {
                this.bots.add(new BotManager(clobberBot, spot.x, spot.y));
            } catch (Exception ex) {
                Logger.getLogger(Match.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (Clobber.TEAMSIZE > 1) {
            informTeams();
        }
    }

    @Override
    public void run()
    {
        long lastUpdate = System.currentTimeMillis();
        int millisecondsPerFrame = 1000 / game.FRAMERATE;

        while (!tallied)
        {
            doOneTurn();
            if (display != null)
            {
                display.repaint();
                try
                {
                    long sleep = millisecondsPerFrame - (System.currentTimeMillis() - lastUpdate);
                    if (sleep > 0)
                    {
                        Thread.sleep(sleep);
                    }
                    lastUpdate = System.currentTimeMillis();
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }
    }

    public void setDisplay(JPanel display)
    {
        this.display = display;
    }

    /**
     * Checks for collisions between bots
     */
    void checkBotVSBotCollisions()
    {
        for (int x = bots.size() - 1; x >= 0; x--)
        {
            BotManager bot1 = bots.get(x);
            for (int y = x - 1; y >= 0; y--)
            {
                if (y == x)
                    continue;
                BotManager bot2 = bots.get(y);
                if (bot1.collision(bot2))
                {
                    bot1.deleteMe = true;
                    bot2.deleteMe = true;
                    bot1.addKills(1);
                    bot2.addKills(1);
                }
            }
            if (bot1.deleteMe)
                deadbots.add(bots.remove(x));
        }
    }

    /**
     * Fills out WhatIKnow data structure with the current world state
     */
    WhatIKnow getWorldState(BotManager cbm)
    {
        BotPoint2D me = new BotPoint2D(cbm.pos.getX(), cbm.pos.getY(), cbm.clobberObjectID);
        Vector<BulletPoint2D> bulpts = new Vector<BulletPoint2D>();
        Vector<BulletPoint2D> mybulpts = new Vector<BulletPoint2D>();
        for (int x = 0; x < bullets.size(); x++)
        {
            if (cbm != bullets.get(x).getOwner())
                bulpts.add(bullets.get(x).getStaticCopy());
            else
                mybulpts.add(bullets.get(x).getStaticCopy());
        }
        Vector<BotPoint2D> botpts = new Vector<BotPoint2D>();
        for (int x = 0; x < bots.size(); x++)
        {
            if (cbm != bots.get(x))
                botpts.add(new BotPoint2D(bots.get(x).pos.getX(), bots.get(x).pos.getY(), bots.get(x).clobberObjectID));
        }
        return new WhatIKnow(me, bulpts, mybulpts, botpts);
    }

    /**
     * Checks for collisions between bullets and bots
     */
    void checkBulletVSBotCollisions()
    {
        for (int x = bullets.size() - 1; x >= 0; x--)
        {
            Bullet bul = bullets.get(x);
            for (int y = bots.size() - 1; y >= 0; y--)
            {
                BotManager bot = bots.get(y);
                if (bul.getOwner() == bot)
                    continue;
                if (bot.collision(bul))
                {
                    bot.deleteMe = true;
                    bul.deleteMe = true;
                    bul.getOwner().addKills(1);
                }
            }
            if (bul.deleteMe)
                bullets.remove(x);
        }
    }

    /**
     * updates the positions of all the bullets
     */
    void updateBulletPositions()
    {
        for (int x = bullets.size() - 1; x >= 0; x--)
        {
            Bullet bul = bullets.get(x);
            bul.move();
            if (bul.deleteMe)
                bullets.remove(x);
        }
    }

    private void informTeams()
    {
        for (BotManager botManager1 : bots) {
            ClobberBot bot1 = botManager1.getBot();
            for (BotManager botManager2 : bots) {
                ClobberBot bot2 = botManager2.getBot();
                if (!bot1.equals(bot2) && bot1.getClass() == bot2.getClass())
                    bot1.teammate(bot2);
            }
        }
    }

    /**
     * performs the set of actions the bots have decided upon. This may result in a bot moving or shooting
     */
    void performBotActions()
    {
        for (int x = 0; x < bots.size(); x++)
        {
            BotManager cbm = bots.get(x);
			cbm.incrementLifespan();
            if (cbm.getShotclock() > 0)
                cbm.decrementShotclock();
            int i = (int) (cbm.pos.getX());
            int j = (int) (cbm.pos.getY());
            if (!cbm.hasAction())
                continue;
            int action = cbm.getAction();
            if ((action & ClobberBotAction.MOVE) > 0)
            {
                if ((action & ClobberBotAction.UP) > 0)
                    j -= game.BOTSPEED;
                if ((action & ClobberBotAction.DOWN) > 0)
                    j += game.BOTSPEED;
                if ((action & ClobberBotAction.LEFT) > 0)
                    i -= game.BOTSPEED;
                if ((action & ClobberBotAction.RIGHT) > 0)
                    i += game.BOTSPEED;
                cbm.updatePosition(i, j);
            }
            else if (((action & ClobberBotAction.SHOOT) > 0) && (cbm.getShotclock() <= 0))
            {
                cbm.setShotclock(game.SHOTFREQUENCY);
                int xplus = 0;
                int yplus = 0;
                if ((action & ClobberBotAction.UP) > 0)
                    yplus -= game.BULLETSPEED;
                if ((action & ClobberBotAction.DOWN) > 0)
                    yplus += game.BULLETSPEED;
                if ((action & ClobberBotAction.LEFT) > 0)
                    xplus -= game.BULLETSPEED;
                if ((action & ClobberBotAction.RIGHT) > 0)
                    xplus += game.BULLETSPEED;
                if (xplus != 0 || yplus != 0)
                    bullets.add(new Bullet(cbm, cbm.pos.getX(), cbm.pos.getY(), xplus, yplus));
            }
        }
    }

    void tallyResults()
    {
        results = new ArrayList<BotStatTracker>();
        if (!tallied)
        {
            for (BotManager botManager : bots)
            {
                botManager.incrementSurvival();
                results.add(botManager.getTracker());
            }
            for (BotManager botManager : deadbots)
            {
                botManager.incrementDeaths();
                results.add(botManager.getTracker());
            }
            Collections.sort(results);
            tallied = true;
        }
        else
            LOG.warning("Trying to re-tally.");
    }

    /**
     * Asks each bot what it wants to do
     */
    void getBotActions()
    {
        for (BotManager cbm : bots)
        {
            try
            {
                cbm.takeTurn(getWorldState(cbm));
            }
            catch (Exception e)
            {
//                LOG.severe(e.toString());
//                cbm.nullifyAction();
//                cbm.incrementExceptions();
//                cbm.deleteMe = true;
//                if (bots.size() == 1)
//                    LOG.info(String.format("%s is a sissy that can't handle being alone.", cbm.getBot()));
            }
        }
    }

    /**
     * Prints the current score of each bot to the screen
     */
    public void printResults()
    {
        if (!tallied)
            tallyResults();
        StringBuilder sb = new StringBuilder("\n");
        sb.append(String.format("%27s%10s%10s%10s%10s%10s%10s%n", "Bots", "Points", "Kills", "Survivals", "Deaths", "Errors", "Lifespan"));
        for (BotStatTracker botStatTracker : results)
        {
            sb.append(botStatTracker);
        }
        LOG.info(sb.toString());
    }

    public void doOneTurn()
    {
        getBotActions();
        performBotActions();
        updateBulletPositions();
        checkBulletVSBotCollisions();
        checkBotVSBotCollisions();
        if ((bots.size() <= 1 && bullets.size() < 1) || --turnsLeft <= 1) 
        {
            tallyResults();
        }
    }
    
    public boolean isFinished()
    {
        return tallied;
    }

	private void addEventListeners(ClobberBot clobberBot, JPanel display, BotManager manager)
	{
		if(clobberBot instanceof KeyListener)
		{
			display.addKeyListener((KeyListener)manager.getBot());
		}
		if(clobberBot instanceof MouseListener)
		{
			display.addMouseListener((MouseListener)manager.getBot());
		}
		if(clobberBot instanceof MouseMotionListener)
		{
			display.addMouseMotionListener((MouseMotionListener)manager.getBot());
		}
	}
}
