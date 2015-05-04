
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.*;
import javax.swing.ImageIcon;

/**
 *
 * @author ibrune, cthatcher
 */
public class Conqueror extends ClobberBot {
	private static final double BULLET_POWER = 3.0d;
	private static final double BULLET_CONSTANT = 500.0d;
	private static final double WALL_POWER = 2.0d;
	private static final double WALL_CONSTANT = 10.0d;
	private static final double MIDDLE_POWER = 2.0d;
	private static final double MIDDLE_CONSTANT = 2.0d;
	private static final double BOT_POWER = 2.0d;
	private static final double BOT_CONSTANT = 30.0d;
	private static ChrisVector[] decisionVector; //Cartesian Coordinates. We are 0, then quadrant 1 is one.
	private static double[] decisionMagnitude = new double[5];
	private static final double TO_BOT_CENTER = 7.5d;
	private static final double BOT_REPULSION_THRESHHOLD = 700;
	private static final double BOT_ATTRACTION_PERCENT = 0.075;
	private static final double THREAT_THRESH = .075;
	private ClobberBotAction currAction, shotAction;
	private int myOwnInt;
	private static int numOfMe;
	private int shotclock;
	private WhatIKnow lastState;

	public Conqueror(Clobber game) {
		super(game);
		myOwnInt = numOfMe++;
		mycolor = Color.BLACK;
	}

	@Override
	public void drawMe(Graphics page, Point2D me) {
		int x, y;
		x = (int) me.getX() - Clobber.MAX_BOT_GIRTH / 2 - 1;
		y = (int) me.getY() - Clobber.MAX_BOT_GIRTH / 2 - 1;

		float r = (float) (rand.nextFloat());
		float g = (float) (rand.nextFloat());
		float b = (float) (rand.nextFloat());

		if (shotclock % 20 == 0) {
			Random rand = new Random();
			r = (float) (rand.nextFloat());
			g = (float) (rand.nextFloat());
			b = (float) (rand.nextFloat());

			if (r <= .49) {
				r += 0.5;
			}
			if (r <= .49) {
				g += 0.5;
			}
			if (r <= .49) {
				b += 0.5;
			}
		}
		page.setColor(new Color(r, g, b));
		page.fillPolygon(randomPoly(x, y));
	}

	public Polygon randomPoly(int x, int y) {

		Random rand = new Random();
		int numRandoms = rand.nextInt(8);

		int[] xArray = new int[numRandoms + 1];
		int[] yArray = new int[numRandoms + 1];

		xArray[0] = x;
		yArray[0] = y;

		for (int i = 1; i < numRandoms; i++) {
			xArray[i] = x + rand.nextInt(25);
			yArray[i] = y + rand.nextInt(25);
		}

		Polygon randPoly = new Polygon(xArray, yArray, numRandoms);
		return randPoly;
	}

	public void myOwnMethod() {
		System.out.println("Unit " + myOwnInt + " reporting, sir.");
	}

	@Override
	public ClobberBotAction takeTurn(WhatIKnow currState) {

		for (int x = 0; x < teammates.size(); x++) {
			((Conqueror) (teammates.get(x))).myOwnMethod();
		}
		lastState = currState;
		/*
		 * vector calculation begin
		 */
		decisionVector = new ChrisVector[5];

		double currentX = lastState.me.getX();
		double currentY = lastState.me.getY();

		for (int i = 0; i < 5; i++) {
			decisionVector[i] = new ChrisVector(0d, 0d);
			decisionMagnitude[i] = 0;
		}
		//Point 0, our origin
		calculateBulletThreat(decisionVector[0], 0, currentX, currentY);
		calculateWallThreat(decisionVector[0], 0, currentX, currentY);
		calculateMiddleThreat(decisionVector[0], 0, currentX, currentY);
		calculateBotThreat(decisionVector[0], 0, currentX, currentY);
		//Point 1, topRight
		currentX = currentX + 2;
		currentY = currentY - 2;
		calculateBulletThreat(decisionVector[1], 1, currentX, currentY);
		calculateWallThreat(decisionVector[1], 1, currentX, currentY);
		calculateMiddleThreat(decisionVector[1], 1, currentX, currentY);
		calculateBotThreat(decisionVector[1], 1, currentX, currentY);
		//Point 2, topLeft
		currentX = currentX - 4;
		currentY = currentY;
		calculateBulletThreat(decisionVector[2], 2, currentX, currentY);
		calculateWallThreat(decisionVector[2], 2, currentX, currentY);
		calculateMiddleThreat(decisionVector[2], 2, currentX, currentY);
		calculateBotThreat(decisionVector[2], 2, currentX, currentY);
		//Point 3, bottomLeft
		currentX = currentX;
		currentY = currentY + 4;
		calculateBulletThreat(decisionVector[3], 3, currentX, currentY);
		calculateWallThreat(decisionVector[3], 3, currentX, currentY);
		calculateMiddleThreat(decisionVector[3], 3, currentX, currentY);
		calculateBotThreat(decisionVector[3], 3, currentX, currentY);
		//Point 4, bottomLeft
		currentX = currentX + 4;
		currentY = currentY;
		calculateBulletThreat(decisionVector[4], 4, currentX, currentY);
		calculateWallThreat(decisionVector[4], 4, currentX, currentY);
		calculateMiddleThreat(decisionVector[4], 4, currentX, currentY);
		calculateBotThreat(decisionVector[4], 4, currentX, currentY);
		/*
		 * vector calculation end
		 */
		/*
		 * turn calculation start
		 */
		shotclock--;
//		if (decisionMagnitude[0] > THREAT_THRESH && shotclock <= 0)
//		{
//			System.out.println("Mag = " + decisionMagnitude[0]);
//		}
		//System.out.println("mag = " + decisionMagnitude[0]);
		if (shotclock <= 0 && decisionMagnitude[0] < THREAT_THRESH) {
			calculateBulletTarget();
			return shotAction;
		} else {
			decisionVector[0].normalize();

			int choice = makeVectorMove();
			//makeDegreeMove();

			//System.out.println("Choice: " + decisionMagnitude[choice]);
			//System.out.println("Old: " + decisionMagnitude[0]);
			//if (decisionMagnitude[0] < decisionMagnitude[choice]) {
			//makeMagnitudeDecision();
			//}
		}
		return currAction;
	}

	private void calculateBulletTarget() {
		shotclock = game.getShotFrequency() + 1;
		int closestBot = 0;
		double closestDistance = Double.MAX_VALUE;
		if (lastState.bots.size() != 0) {
			for (int i = 0; i < lastState.bots.size(); i++) {
				double tempDistance = lastState.bots.get(i).distance(lastState.me);
				if (tempDistance < closestDistance) {
					closestDistance = tempDistance;
					closestBot = i;
				}
			}
			BotPoint2D bot = lastState.bots.get(closestBot);
			double xDelta = lastState.me.getX() - bot.getX();
			double yDelta = lastState.me.getY() - bot.getY();
			int degree = (int) Math.toDegrees(Math.atan2(xDelta, yDelta)) + 180;

			if (degree >= 330 || (degree > 0 && degree < 30)) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.DOWN);
			} else if (degree >= 30 && degree < 60) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
			} else if (degree >= 60 && degree < 120) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.RIGHT);
			} else if (degree >= 120 && degree < 150) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.RIGHT | ClobberBotAction.UP);
			} else if (degree >= 150 && degree < 210) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.UP);
			} else if (degree >= 210 && degree < 240) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.UP | ClobberBotAction.LEFT);
			} else if (degree >= 240 && degree < 300) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.LEFT);
			} else if (degree >= 300 && degree < 330) {
				shotAction = new ClobberBotAction(ClobberBotAction.SHOOT, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
			}
		}
	}

	private int makeVectorMove() {

		int choice = 0;
		if (decisionVector[0].normalizedX < 0.0 && decisionVector[0].normalizedY < 0.0) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.LEFT);
			choice = 2;
		} else if (decisionVector[0].normalizedX < 0.0 && decisionVector[0].normalizedY > 0.0) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
			choice = 3;
		} else if (decisionVector[0].normalizedX > 0.0 && decisionVector[0].normalizedY < 0.0) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.RIGHT);
			choice = 1;
		} else if (decisionVector[0].normalizedX > 0.0 && decisionVector[0].normalizedY > 0.0) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
			choice = 4;
		} else {
			currAction = null;
		}
		return choice;
	}

	private void makeDegreeMove() {
		double xDelta = decisionVector[0].normalizedX;
		double yDelta = decisionVector[0].normalizedY;
		int degree = (int) Math.toDegrees(Math.atan2(xDelta, yDelta)) + 180;

		if (degree >= 330 || (degree > 0 && degree < 30)) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.UP);
		} else if (degree >= 30 && degree < 60) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.UP | ClobberBotAction.LEFT);
		} else if (degree >= 60 && degree < 120) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.LEFT);
		} else if (degree >= 120 && degree < 150) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.LEFT | ClobberBotAction.DOWN);
		} else if (degree >= 150 && degree < 210) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.DOWN);
		} else if (degree >= 210 && degree < 240) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
		} else if (degree >= 240 && degree < 300) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.RIGHT);
		} else if (degree >= 300 && degree < 330) {
			currAction = new ClobberBotAction((ClobberBotAction.MOVE), ClobberBotAction.UP | ClobberBotAction.RIGHT);
		}
	}

	private void makeMagnitudeDecision() {
		int lowSpot = Integer.MAX_VALUE;
		for (int i = 0; i < decisionMagnitude.length; i++) {
			if (decisionMagnitude[lowSpot] > decisionMagnitude[i]) {
				lowSpot = i;
			}
		}
		if (lowSpot == 2) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.LEFT);
		} else if (lowSpot == 3) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
		} else if (lowSpot == 1) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.RIGHT);
		} else if (lowSpot == 4) {
			currAction = new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
		} else {
			currAction = null;
		}
	}

	/**
	 * modifies the dangerousBullets set to contain all of the bullets coming 'towards' us
	 */
	private void calculateBulletThreat(ChrisVector theThreat, int magnitude, double ourX, double ourY) {

		Set<BulletPoint2D> dangerousBullets = findDangerousBullets();

		for (BulletPoint2D bullet : dangerousBullets) {
			boolean isDiagonal = (bullet.getXPlus() != 0 && bullet.getYPlus() != 0);

			double yDelta = ourY - bullet.getY();
			double xDelta = ourX - bullet.getX();
			ChrisVector bulletVector = new ChrisVector(xDelta, yDelta);
			bulletVector.normalize();

			double distance = bullet.distance(lastState.me.x + TO_BOT_CENTER, lastState.me.y + TO_BOT_CENTER);

			if (isDiagonal) {
				distance /= Math.pow(2, 0.5);
			}

			double yThreat = bulletVector.getNormalizedY() * BULLET_CONSTANT / Math.pow(distance, BULLET_POWER);
			double xThreat = bulletVector.getNormalizedX() * BULLET_CONSTANT / Math.pow(distance, BULLET_POWER);

			ChrisVector threatVector = new ChrisVector(xThreat, yThreat);
			if (isDiagonal) {
				if (isLeft(bullet)) {
					threatVector = rotateVectorPlus90(threatVector);
				} else {
					threatVector = rotateVectorMinus90(threatVector);
				}
			} else {
				if (isLeft(bullet)) {
					threatVector = rotateVector45(threatVector);
				} else {
					threatVector = rotateVector315(threatVector);
				}
			}

			theThreat.addVector(threatVector);
			decisionMagnitude[magnitude] += threatVector.getMagnitude();
		}
	}

	public boolean isLeft(BulletPoint2D bullet1) {
		ChrisVector bullet2 = new ChrisVector(bullet1.x + bullet1.getXPlus(), bullet1.y + bullet1.getYPlus());
		return ((bullet2.getRawX() - bullet1.x) * (lastState.me.y - bullet1.y) - (bullet2.getRawY() - bullet1.y) * (lastState.me.x - bullet1.x)) > 0;
	}

	private ChrisVector rotateVectorPlus90(ChrisVector oldVector) {
		return new ChrisVector(-1.0d * oldVector.getRawY(), oldVector.getRawX());
	}

	private ChrisVector rotateVectorMinus90(ChrisVector oldVector) {
		return new ChrisVector(oldVector.getRawY(), -1.0d * oldVector.getRawX());
	}

	private ChrisVector rotateVector45(ChrisVector oldVector) {
		double oneOverRoot2 = 0.7071067811865d;
		double x = (oldVector.getRawX() * oneOverRoot2) - (oldVector.getRawY() * oneOverRoot2);
		double y = (oldVector.getRawX() * oneOverRoot2) + (oldVector.getRawY() * oneOverRoot2);
		return new ChrisVector(x, y);
	}

	private ChrisVector rotateVector315(ChrisVector oldVector) {
		double oneOverRoot2 = 0.7071067811865d;
		double x = (oldVector.getRawX() * oneOverRoot2) + (oldVector.getRawY() * oneOverRoot2);
		double y = (oldVector.getRawX() * oneOverRoot2 * -1) + (oldVector.getRawY() * oneOverRoot2);
		return new ChrisVector(x, y);
	}

	private Set<BulletPoint2D> findDangerousBullets() {

		Set<BulletPoint2D> dangerousBullets = new HashSet<BulletPoint2D>();
		int xPlus = 0;
		int yPlus = 0;
		Vector<BulletPoint2D> bulletList = lastState.bullets;

		for (int i = 0; i < bulletList.size(); i++) {
			xPlus = bulletList.get(i).getXPlus();
			yPlus = bulletList.get(i).getYPlus();
			BulletPoint2D bullet = bulletList.get(i);

			if (lastState.me.y < bulletList.get(i).y && yPlus < 0) {
				dangerousBullets.add(bulletList.get(i));
			}
			if (lastState.me.y + TO_BOT_CENTER * 2.0 > bulletList.get(i).y && yPlus > 0) {
				if (!dangerousBullets.contains(bulletList.get(i))) {
					dangerousBullets.add(bulletList.get(i));
				}
			}
			if (lastState.me.x < bulletList.get(i).x && xPlus < 0) {
				if (!dangerousBullets.contains(bulletList.get(i))) {
					dangerousBullets.add(bulletList.get(i));
				}
			}
			if (lastState.me.x + TO_BOT_CENTER * 2.0 > bulletList.get(i).x && yPlus > 0) {
				if (!dangerousBullets.contains(bulletList.get(i))) {
					dangerousBullets.add(bulletList.get(i));
				}
			}
		}
		return dangerousBullets;
	}

	/**
	 * calculate threat from running into a wall THIS IS NOT CURRENTLY CORRECT
	 */
	private void calculateWallThreat(ChrisVector dVector, int magnitude, double ourX, double ourY) {

		double xDistance = 0;
		ChrisVector xPushVector = null;

		if (ourX < 100) {
			xPushVector = new ChrisVector(1d, 0d);
			xPushVector.normalize();
			xDistance = ourX + TO_BOT_CENTER;
			addWallVector(dVector, magnitude, xPushVector, xDistance);
		} else if (ourX > 500) {
			xPushVector = new ChrisVector(-1d, 0d);
			xPushVector.normalize();
			xDistance = 600.0d - ourX + TO_BOT_CENTER;
			addWallVector(dVector, magnitude, xPushVector, xDistance);
		} else {
		}

		double yDistance = 0;
		ChrisVector yPushVector = null;

		if (ourY < 100) {
			yPushVector = new ChrisVector(0d, 1d);
			yPushVector.normalize();
			yDistance = ourY + TO_BOT_CENTER;
			addWallVector(dVector, magnitude, yPushVector, yDistance);
		} else if (ourY > 500) {
			yPushVector = new ChrisVector(0d, -1d);
			yPushVector.normalize();
			yDistance = 600.0d - ourY + TO_BOT_CENTER;
			addWallVector(dVector, magnitude, yPushVector, yDistance);
		} else {
			return;
		}
	}

	private void addWallVector(ChrisVector dVector, int magnitude, ChrisVector seedVector, double distance) {

		double yThreat = seedVector.getNormalizedY() * WALL_CONSTANT / Math.pow(distance, WALL_POWER);
		double xThreat = seedVector.getNormalizedX() * WALL_CONSTANT / Math.pow(distance, WALL_POWER);

		ChrisVector threatVector = new ChrisVector(xThreat, yThreat);

		dVector.addVector(threatVector);
		decisionMagnitude[magnitude] += threatVector.getMagnitude();
	}

	private void calculateMiddleThreat(ChrisVector dVector, int magnitude, double ourX, double ourY) {
		double yDelta = ourY - 300.0d;
		double xDelta = ourX - 300.0d;
		ChrisVector middleVector = new ChrisVector(xDelta, yDelta);
		middleVector.normalize();

		double distance = new ImmutablePoint2D(ourX, ourY, 0).distance(300d + TO_BOT_CENTER, 300d + TO_BOT_CENTER);

		double yThreat = middleVector.getNormalizedY() * MIDDLE_CONSTANT / Math.pow(distance, MIDDLE_POWER);
		double xThreat = middleVector.getNormalizedX() * MIDDLE_CONSTANT / Math.pow(distance, MIDDLE_POWER);

		ChrisVector threatVector = new ChrisVector(xThreat, yThreat);

		dVector.addVector(threatVector);
		decisionMagnitude[magnitude] += threatVector.getMagnitude();
	}

	/**
	 * calculate threat from running into a bot
	 */
	private void calculateBotThreat(ChrisVector dVector, int magnitude, double ourX, double ourY) {
		for (BotPoint2D bot : lastState.bots) {

			double yDelta = ourY - bot.getY();
			double xDelta = ourX - bot.getX();
			ChrisVector botVector = new ChrisVector(xDelta, yDelta);
			botVector.normalize();

			double distance = bot.distance(ourX + TO_BOT_CENTER, ourY + TO_BOT_CENTER);


			double yThreat = botVector.getNormalizedY() * BOT_CONSTANT / Math.pow(distance, BOT_POWER);
			double xThreat = botVector.getNormalizedX() * BOT_CONSTANT / Math.pow(distance, BOT_POWER);

			if (distance > BOT_REPULSION_THRESHHOLD) {
				yThreat = yThreat * -1 * BOT_ATTRACTION_PERCENT;
				xThreat = xThreat * -1 * BOT_ATTRACTION_PERCENT;
			}

			ChrisVector threatVector = new ChrisVector(xThreat, yThreat);

			dVector.addVector(threatVector);
			decisionMagnitude[magnitude] += threatVector.getMagnitude();
		}
	}

	@Override
	public String toString() {
		return "Conqueror";
	}

	//////////////////////////////////////////////////////////////
	//////////////////////// INNER CLASSES ///////////////////////
	//////////////////////////////////////////////////////////////
	private class ChrisVector {
		private double rawX;
		private double rawY;
		private double normalizedX;
		private double normalizedY;
		private double magnitude;

		private void normalize() {
			makeMagnitude();
			setNormalizedX(rawX / magnitude);
			setNormalizedY(rawY / magnitude);
		}

		private double getMagnitude() {
			makeMagnitude();
			return magnitude;
		}

		private void makeMagnitude() {
			magnitude = Math.sqrt((Math.pow(rawX, 2) + Math.pow(rawY, 2)));
		}

		private ChrisVector(double x, double y) {
			this.rawX = x;
			this.rawY = y;
		}

		private void addVector(ChrisVector other) {
			rawX = rawX + other.rawX;
			rawY = rawY + other.rawY;
		}

		private double getRawX() {
			return rawX;
		}

		private void setRawX(double rawX) {
			this.rawX = rawX;
		}

		private double getRawY() {
			return rawY;
		}

		private void setRawY(double rawY) {
			this.rawY = rawY;
		}

		private double getNormalizedX() {
			return normalizedX;
		}

		private void setNormalizedX(double normalizedX) {
			this.normalizedX = normalizedX;
		}

		private double getNormalizedY() {
			return normalizedY;
		}

		private void setNormalizedY(double normalizedY) {
			this.normalizedY = normalizedY;
		}
	}
}
