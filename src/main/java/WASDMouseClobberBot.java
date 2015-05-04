import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class implements an example GUIClobberBot.
 */
public class WASDMouseClobberBot extends ClobberBot implements KeyListener, MouseMotionListener, MouseListener
{
	private static final int ALL_DIRECTIONS = ClobberBotAction.UP | ClobberBotAction.DOWN | ClobberBotAction.LEFT | ClobberBotAction.RIGHT;
	private int moveDirection;
	private int mouseDirection;
	private boolean mouseDown;
	private Point mouseLocation = new Point(0, 0);
	private Point2D myLocation = new Point(0,0);
	private int shootTimer = 0;
	private double atan2 = 0;

    public WASDMouseClobberBot(Clobber game)
    {
        super(game);
        mycolor = Color.ORANGE;
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
		
		shootTimer--;
		myLocation = currState.me;
		int direction;
		int action;
		if(mouseDown && shootTimer <= 0)
		{
			action = ClobberBotAction.SHOOT;
			direction = mouseDirection;
			shootTimer = game.getShotFrequency();
		} else {
			action = ClobberBotAction.MOVE;
			direction = moveDirection;
		}
		return new ClobberBotAction(action, direction);
    }

    /**
     * Draws the clobber bot to the screen. The drawing should be centered at the point me, and should not be bigger
     * than 9x9 pixels
     */
    @Override
    public void drawMe(Graphics page, Point2D me)
    {
        int x, y, centerX,centerY,turretX = 0, turretY = 0;
		centerX = (int)(me.getX());
		centerY = (int)(me.getY());
		x = centerX - Clobber.MAX_BOT_GIRTH / 2;
		y = centerY - Clobber.MAX_BOT_GIRTH / 2;
		if((mouseDirection & ClobberBotAction.LEFT) != 0) {
			turretX = -Clobber.MAX_BOT_GIRTH/2;
		} else if((mouseDirection & ClobberBotAction.RIGHT) != 0) {
			turretX = Clobber.MAX_BOT_GIRTH/2;
		}
		if((mouseDirection & ClobberBotAction.UP) != 0) {
			turretY = -Clobber.MAX_BOT_GIRTH/2;
		} else if((mouseDirection & ClobberBotAction.DOWN) != 0) {
			turretY = Clobber.MAX_BOT_GIRTH/2;
		}
        page.setColor(mycolor);
        page.fillOval(x, y, Clobber.MAX_BOT_GIRTH, Clobber.MAX_BOT_GIRTH);
		page.setColor(Color.GREEN);
		page.drawLine(centerX, centerY, turretX+(int)me.getX(), turretY+(int)me.getY());
		page.drawString(""+atan2,10, 10);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
		switch(e.getKeyCode())
		{
			case 'W':
				moveDirection &= ALL_DIRECTIONS ^ ClobberBotAction.UP;
				break;
			case 'A':
				moveDirection &= ALL_DIRECTIONS ^ ClobberBotAction.LEFT;
				break;
			case 'S':
				moveDirection &= ALL_DIRECTIONS ^ ClobberBotAction.DOWN;
				break;
			case 'D':
				moveDirection &= ALL_DIRECTIONS ^ ClobberBotAction.RIGHT;
				break;
		}
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
		switch(e.getKeyCode())
		{
			case 'W':
				moveDirection |= ClobberBotAction.UP;
				break;
			case 'A':
				moveDirection |= ClobberBotAction.LEFT;
				break;
			case 'S':
				moveDirection |= ClobberBotAction.DOWN;
				break;
			case 'D':
				moveDirection |= ClobberBotAction.RIGHT;
				break;
		}
    }
	
    /**
     * Return a String representation of the ClobberBot
     */
    @Override
    public String toString()
    {
        return "WASD AND MOUSE!";
    }

	@Override
	public void mouseDragged(MouseEvent e)
	{
		relocateMouse(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		relocateMouse(e);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		mouseDown = true;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		mouseDown = false;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	private void relocateMouse(MouseEvent e)
	{
		mouseDirection = 0;
		mouseLocation = e.getPoint();
		atan2 = Math.atan2(myLocation.getY() - mouseLocation.y, myLocation.getX() - mouseLocation.x);
		if(atan2 >= -7*Math.PI/8 && atan2 <= -Math.PI/8)
		{
			mouseDirection |= ClobberBotAction.DOWN;
		} else if(atan2 <= 7*Math.PI/8 && atan2 >= Math.PI/8)
		{
			mouseDirection |= ClobberBotAction.UP;
		}
		if(Math.abs(atan2) <= 3*Math.PI/8)
		{
			mouseDirection |= ClobberBotAction.LEFT;
		}else if(Math.abs(atan2) >= 5*Math.PI/8)
		{
			mouseDirection |= ClobberBotAction.RIGHT;
		}
	}
	
}
