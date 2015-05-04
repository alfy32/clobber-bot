package clobberbot;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
class BotStatTracker implements Comparable<BotStatTracker>
{
    private final int killPoints;
    private final int survivalPoints;
    private int kills;
    private int survivals;
    private int deaths;
    private int exceptions;
    private ClobberBot bot;

    @Override
    public String toString()
    {
        return String.format("%27s%10d%10d%10d%10d%10d%n", 
                bot, 
                kills * killPoints + survivals * survivalPoints, 
                kills, 
                survivals, 
                deaths, 
                exceptions);
    }

    public BotStatTracker(ClobberBot bot)
    {
        kills = 0;
        deaths = 0;
        exceptions = 0;
        survivals = 0;
        this.bot = bot;
        this.killPoints = bot.game.KILLPOINTS;
        this.survivalPoints = bot.game.SURVIVALPOINTS;
    }

    public synchronized int getKillCount() {
        return kills;
    }
    
    public synchronized void addKills(int kills) {
        this.kills += kills;
    }
    
    public synchronized void incrementKills() {
        this.kills++;
    }

    public synchronized int getSurvivalCount() {
        return survivals;
    }
    
    public synchronized void incrementSurvival() {
        this.survivals++;
    }

    public synchronized int getDeathCount() {
        return deaths;
    }
    
    public synchronized void incrementDeaths() {
        this.deaths++;
    }

    public synchronized int getExceptionCount() {
        return exceptions;
    }
    
    public synchronized void incrementExceptions() {
        this.exceptions++;
    }

    public synchronized int getPoints() {
        return (kills * killPoints + survivals * survivalPoints);
    }

    public ClobberBot getBot() {
        return bot;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings( 
            value = "EQ_COMPARETO_USE_OBJECT_EQUALS", 
            justification = "Note: this class has a natural ordering that is "
            + "inconsistent with equals.")
    @Override
    public synchronized int compareTo(BotStatTracker t) {
        int myPoints = this.getPoints();
        int hisPoints = t.getPoints();
        if (myPoints != hisPoints)
            return hisPoints - myPoints;
        if (survivals != t.getSurvivalCount())
            return t.getSurvivalCount() - survivals;
        if (exceptions != t.getExceptionCount())
            return exceptions - t.getExceptionCount();
        return 0;
    }
}