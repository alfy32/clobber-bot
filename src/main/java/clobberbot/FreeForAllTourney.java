package clobberbot;


import java.util.List;

class FreeForAllTourney extends Tourney
{
    public FreeForAllTourney(int runs, Clobber physics, List<ClobberBot> bots,
            PlacementStrategy placement, boolean includeRandomBots) 
    {
        super(runs, physics, bots, placement, includeRandomBots);
        if (randoms)
        {
            allBots.addAll(randomBots);
        }            
    }
    
    @Override
    protected String announceTourney() {
        return String.format("***** Free for All Tournament %s%n", randoms ? "with RandomBots" : "");
    }
    
    @Override
    public void runTourney()
    {
        for (int x = 0; x < runs; x++) {
            Match m = new Match(physics, allBots, placement);
            m.run();
        }
        LOG.info(toString());
    }
}
