package clobberbot;


import java.util.ArrayList;
import java.util.List;

class PairTourney extends Tourney
{

    public PairTourney(int runs, Clobber physics, List<ClobberBot> bots,
            PlacementStrategy placement, boolean includeRandomBots)
    {
        super(runs, physics, bots, placement, includeRandomBots);
    }

    @Override
    protected String announceTourney()
    {
        return String.format("***** One on One Tournament %s%n", randoms ? "with RandomBots" : "");
    }

    @Override
    public void runTourney()
    {
        List<BotStatTracker> secondaryBots = randoms ? randomBots : allBots;
        for (BotStatTracker botStatTracker1 : allBots)
        {
            for (BotStatTracker botStatTracker2 : secondaryBots)
            {
                if (botStatTracker1.equals(botStatTracker2))
                    continue;
                try
                {
                    List<BotStatTracker> pair = new ArrayList<BotStatTracker>();
                    pair.add(botStatTracker1);
                    pair.add(botStatTracker2);
                    for (int i = 0; i < runs; i++)
                    {
                        Match m = new Match(physics, pair, placement);
                        m.run();

                    }
                } catch (Exception ex)
                {
                    LOG.severe(ex.toString());
                }
            }
        }
        LOG.info(toString());
    }
}
