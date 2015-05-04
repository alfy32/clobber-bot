import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * This is a lot like ClobberBot3, but has an even stronger tendency to keep moving in the same direction. Also, I've
 * given you an example of how to read the WhatIKnow state to see where all the bullets and other bots are.
 */
public class ClobberBot4 extends ClobberBot
{
    ClobberBotAction currAction, shotAction;
    int shotclock;
	Image image;

    public ClobberBot4(Clobber game)
    {
        super(game);
        mycolor = Color.PINK;
		try
		{
			image = ImageIO.read(new File("./src/main/resources/images/randomBot.png"));
		} catch (IOException ex)
		{
			Logger.getLogger(ClobberBot4.class.getName()).log(Level.SEVERE, null, ex);
		}
    }

    /**
     * Here's an example of how to read the WhatIKnow data structure
     */
    private void showWhatIKnow(WhatIKnow currState)
    {
        System.out.println("My id is " + ((ImmutablePoint2D) (currState.me)).getID() + ", I'm at position ("
                           + currState.me.getX() + ", " + currState.me.getY() + ")");
        System.out.print("Bullets: ");
        Iterator<BulletPoint2D> it = currState.bullets.iterator();
        while (it.hasNext()) {
            ImmutablePoint2D p = (ImmutablePoint2D) (it.next());
            System.out.print(p + ", ");
        }
        System.out.println();

        System.out.print("Bots: ");
        Iterator<BotPoint2D> bit = currState.bots.iterator();
        while (bit.hasNext()) {
            System.out.print(bit.next() + ", ");
        }
        System.out.println();
    }

    @Override
    public ClobberBotAction takeTurn(WhatIKnow currState)
    {
        //showWhatIKnow(currState); // @@@ Uncomment this line to see it print out all bullet and bot positions every turn
        shotclock--;
        if (shotclock <= 0) {
            shotclock = game.getShotFrequency() + 1;
            switch (rand.nextInt(8)) {
                case 0:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.UP);
                    break;
                case 1:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.DOWN);
                    break;
                case 2:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.LEFT);
                    break;
                case 3:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.RIGHT);
                    break;
                case 4:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.UP | ClobberBotAction.LEFT);
                    break;
                case 5:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT,
                                                      ClobberBotAction.UP | ClobberBotAction.RIGHT | ClobberBotAction.DOWN | ClobberBotAction.LEFT);
                    break;
                case 6:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
                    break;
                default:
                    shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
                    break;
            }
            return shotAction;
        }
        else if (currAction == null || rand.nextInt(20) > 18) {
            switch (rand.nextInt(4)) {
                case 0:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP);
                    break;
                case 1:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN);
                    break;
                case 2:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.LEFT);
                    break;
                case 3:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.RIGHT);
                    break;
                case 4:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.LEFT);
                    break;
                case 5:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.RIGHT);
                    break;
                case 6:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
                    break;
                default:
                    currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
                    break;
            }
        }
        return currAction;
    }
	
    @Override
    public String toString()
    {
        return "ClobberBot4 by Tim Andersen";
    }
	
	@Override
	public void drawMe(Graphics page, Point2D me)
	{
		int x,y;
        x=(int)me.getX() - Clobber.MAX_BOT_GIRTH/2 -1;
        y=(int)me.getY() - Clobber.MAX_BOT_GIRTH/2 -1;

        page.drawImage(image, x, y, null); 		
	}
}
