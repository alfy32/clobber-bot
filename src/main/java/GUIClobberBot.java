import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements an example GUIClobberBot.
 */
public class GUIClobberBot extends ClobberBot implements KeyListener
{
    private ClobberBotAction doNothing = new ClobberBotAction(ClobberBotAction.MOVE, 0);
	private Map<Integer, Action> keysToActions;

    public GUIClobberBot(Clobber game)
    {
        super(game);
        mycolor = Color.ORANGE;
		keysToActions = new HashMap();
		initControls();
    }

    @Override
    public void setEnvironment(Dimension worldSize)
    {
        this.worldSize = new Dimension(worldSize);
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
		for (Action action : keysToActions.values())
		{
			if(action.doing)
				return action.getAction();
		}
		return doNothing;
    }

    /**
     * Draws the clobber bot to the screen. The drawing should be centered at the point me, and should not be bigger
     * than 9x9 pixels
     */
    @Override
    public void drawMe(Graphics page, Point2D me)
    {
        int x, y;
        x = (int) (me.getX()) - 8;
        y = (int) (me.getY()) - 8;
        page.setColor(mycolor);
        page.fillArc(x + 4, y, 8, 8, 0, 180);
        page.fillRect(2 + x, 4 + y, 12, 10);
        page.fillArc(x, y + 4, 4, 4, 90, 180);
        page.fillArc(x + 12, y + 4, 4, 4, 270, 180);
        page.fillArc(x + 2, y + 12, 4, 4, 180, 180);
        page.fillArc(x + 10, y + 12, 4, 4, 180, 180);
        page.setColor(Color.YELLOW);
        page.fillOval(6 + x, 2 + y, 2, 2);
        page.fillOval(10 + x, 2 + y, 2, 2);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
		keysToActions.get(e.getKeyCode()).doing = false;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        keysToActions.get(e.getKeyCode()).doing = true;
    }
	
	private void initControls()
	{
		addControl((int) 'N', new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.DOWN));
		keysToActions.put(KeyEvent.VK_NUMPAD2, keysToActions.get((int) 'N'));
		addControl((int) 'J', new ClobberBotAction(ClobberBotAction.SHOOT,ClobberBotAction.RIGHT));
		keysToActions.put(KeyEvent.VK_NUMPAD6, keysToActions.get((int) 'J'));
		addControl((int) 'Y', new ClobberBotAction(ClobberBotAction.SHOOT,ClobberBotAction.UP));
		keysToActions.put(KeyEvent.VK_NUMPAD8, keysToActions.get((int) 'Y'));
		addControl((int) 'G', new ClobberBotAction(ClobberBotAction.SHOOT,ClobberBotAction.LEFT));
		keysToActions.put(KeyEvent.VK_NUMPAD4, keysToActions.get((int) 'G'));
		addControl((int) 'B', new ClobberBotAction(ClobberBotAction.SHOOT,ClobberBotAction.DOWN | ClobberBotAction.LEFT));
		keysToActions.put(KeyEvent.VK_NUMPAD1, keysToActions.get((int) 'B'));
		addControl((int) 'M', new ClobberBotAction(ClobberBotAction.SHOOT,ClobberBotAction.DOWN | ClobberBotAction.RIGHT));
		keysToActions.put(KeyEvent.VK_NUMPAD3, keysToActions.get((int) 'M'));
		addControl((int) 'U', new ClobberBotAction(ClobberBotAction.SHOOT,ClobberBotAction.UP | ClobberBotAction.RIGHT));
		keysToActions.put(KeyEvent.VK_NUMPAD9, keysToActions.get((int) 'U'));
		addControl((int) 'T', new ClobberBotAction(ClobberBotAction.SHOOT,ClobberBotAction.UP | ClobberBotAction.LEFT));
		keysToActions.put(KeyEvent.VK_NUMPAD7, keysToActions.get((int) 'T'));
		addControl((int) 'W', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP));
		keysToActions.put(KeyEvent.VK_UP, keysToActions.get((int) 'W'));
		addControl((int) 'D', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.RIGHT));
		keysToActions.put(KeyEvent.VK_RIGHT, keysToActions.get((int) 'D'));
		addControl((int) 'X', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN));
		keysToActions.put(KeyEvent.VK_DOWN, keysToActions.get((int) 'X'));
		addControl((int) 'A', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.LEFT));
		keysToActions.put(KeyEvent.VK_LEFT, keysToActions.get((int) 'A'));
		addControl((int) 'Q', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.LEFT | ClobberBotAction.UP));
		addControl((int) 'E', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.RIGHT | ClobberBotAction.UP));
		addControl((int) 'Z', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.LEFT | ClobberBotAction.DOWN));
		addControl((int) 'C', new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.RIGHT | ClobberBotAction.DOWN));
		addControl((int) 'S', doNothing);
	}
	
	private void addControl(int keyCode, ClobberBotAction action)
	{
		keysToActions.put(keyCode, new Action(action));
	}

    /**
     * Return a String representation of the ClobberBot
     */
    @Override
    public String toString()
    {
        return "GUIClobberBot";
    }
	
	private static class Action
	{
		private ClobberBotAction action;
		public boolean doing = false;
		
		public Action(ClobberBotAction action)
		{
			this.action = action;
		}

		public ClobberBotAction getAction()
		{
			return action;
		}
	}
}
