
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public abstract class Tourney 
{
    protected static final Logger LOG = Logger.getLogger(Logger.class.getName());
    protected boolean randoms;
    protected int runs;
    protected Clobber physics;
    protected PlacementStrategy placement;
    protected List<BotStatTracker> allBots = new ArrayList<BotStatTracker>();
    protected List<BotStatTracker> randomBots = new ArrayList<BotStatTracker>();

    protected abstract String announceTourney();

    public abstract void runTourney();

    Tourney(int runs, Clobber physics, List<ClobberBot> bots, 
            PlacementStrategy placement, boolean randoms) 
    {
        LOG.info("Starting tournament");
        this.randoms = randoms;
        this.runs = runs;
        this.physics = physics;
        this.placement = placement;
        setupRandomBots();
        for (ClobberBot clobberBot : bots) {
            allBots.add(new BotStatTracker(clobberBot));
        }        
    }

    private void setupRandomBots() 
    {
        randomBots.add(new BotStatTracker(new ClobberBot1(physics)));
        randomBots.add(new BotStatTracker(new ClobberBot2(physics)));
        randomBots.add(new BotStatTracker(new ClobberBot3(physics)));
        randomBots.add(new BotStatTracker(new ClobberBot4(physics)));
    }

    @Override
    public String toString() 
    {
        Collections.sort(allBots);
        StringBuilder str = new StringBuilder("\n");
        str.append(announceTourney());
        str.append(placement.toString());
        str.append(String.format("%n%27s%10s%10s%10s%10s%10s%n", "Bots", "Points", "Kills", "Survivals", "Deaths", "Errors"));
        for (BotStatTracker botStatTracker : allBots) {
            str.append(botStatTracker);
        }
        return str.toString();
    }
}
