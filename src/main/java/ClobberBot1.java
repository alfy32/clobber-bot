
import java.awt.Color;

/**
 * This class implements an example ClobberBot1 that makes random moves. All ClobberBots should extend this class and
 * override the takeTurn and drawMe methods.
 */
public class ClobberBot1 extends ClobberBot
{

    public ClobberBot1(Clobber game)
    {
        super(game);
        mycolor = Color.red;
    }

    /**
     * This method is called once for each bot for each turn. The bot should look at what it knows, and make an
     * appropriate decision about what to do.
     *
     * @param currState contains info on this bots current position, the position of every other bot and bullet in the
     * system.
     */
    @Override
    public ClobberBotAction takeTurn(WhatIKnow currState)
    {
        switch (rand.nextInt(8)) {
            case 0:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.UP);
            case 1:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.DOWN);
            case 2:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.LEFT);
            case 3:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.RIGHT);
            case 4:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.UP | ClobberBotAction.LEFT);
            case 5:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.UP | ClobberBotAction.RIGHT);
            case 6:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
            default:
                return new ClobberBotAction(rand.nextInt(2) + 1, ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
        }
    }

    /**
     * Your bots identifier string. It must be unique from other players, since I use it to determine who your teammates
     * are. You can include your login name in the id to guarantee uniqueness.
     */
    @Override
    public String toString()
    {
        return "ClobberBot1 by Tim Andersen";
    }

}
