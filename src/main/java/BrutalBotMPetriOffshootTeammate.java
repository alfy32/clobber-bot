
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * This class implements an example ClobberBot1 that makes random moves. All ClobberBots should extend this class and
 * override the takeTurn and drawMe methods.
 */
public class BrutalBotMPetriOffshootTeammate extends ClobberBot {
	private static final int BIG_GRID_SIZE = 600;
	private static final int BOT_SPEED = 2;
	// BUFFER_AMOUNTS
	private static final int BULLET_BUFFER_AMOUNT = 25;
	private static final int fullBufferAmount = BULLET_BUFFER_AMOUNT * 2 + 15;
	private static final int halfBufferAmount = BULLET_BUFFER_AMOUNT / 2;
	private static final int BOT_SIZE = 15;
	private static final int HALF_BOT_SIZE = BOT_SIZE / 2;
	// NUMERATOR_FACTOR
	private static final double BULLET_NUMERATOR_FACTOR = 100.0;
	private static final double BOT_NUMERATOR_FACTOR = 1.0;
	private static final double WALL_NUMERATOR_FACTOR = 100.0;
	// POWER_FACTOR
	private static final double BULLET_POWER_FACTOR = 2.0;
	private static final double BOT_POWER_FACTOR = 2.0;
	private static final double WALL_POWER_FACTOR = 2.0;
	// MIN_CARE_DIST
	private static final double BULLET_MIN_CARE_DIST = 175.0;
	private static final double BOT_MIN_CARE_DIST = 600.0;
	private static final double WALL_MIN_CARE_DIST = 100.0;
	// NEAR_BOT_RANGE
	private static final double NEAR_BOT_RANGE1_DIST = 400.0;
	private static final double NEAR_BOT_RANGE2_DIST = 500.0;
	// PI_VALUES
	private static final double PI_TIMES_0_OVER_6 = 0.0;
	private static final double PI_TIMES_1_OVER_6 = Math.PI * 1.0 / 6.0;
	private static final double PI_TIMES_2_OVER_6 = Math.PI * 2.0 / 6.0;
	private static final double PI_TIMES_3_OVER_6 = Math.PI * 3.0 / 6.0;
	private static final double PI_TIMES_4_OVER_6 = Math.PI * 4.0 / 6.0;
	private static final double PI_TIMES_5_OVER_6 = Math.PI * 5.0 / 6.0;
	private static final double PI_TIMES_6_OVER_6 = Math.PI * 6.0 / 6.0;
	private static final double PI_TIMES_7_OVER_6 = Math.PI * 7.0 / 6.0;
	private static final double PI_TIMES_8_OVER_6 = Math.PI * 8.0 / 6.0;
	private static final double PI_TIMES_9_OVER_6 = Math.PI * 9.0 / 6.0;
	private static final double PI_TIMES_10_OVER_6 = Math.PI * 10.0 / 6.0;
	private static final double PI_TIMES_11_OVER_6 = Math.PI * 11.0 / 6.0;
	private static final double PI_TIMES_12_OVER_6 = Math.PI * 12.0 / 6.0;
	private static final double FOUR_OVER_PI = 4.0 / Math.PI;
	private static final double PI_TIMES_1_OVER_4 = Math.PI / 4.0;
	// NON_STATIC_VARIABLES
	private BotPoint2D myPt;
	private long start;
	private long end;
	private DangerVector bulletResultant;
	private DangerVector botResultant;
	private DangerVector wallResultant;
	private int timer;
	private int offenseTimer;
	private boolean shootEm;
	// TEAMMATE ADDITIONS
	private int turnCounter = 0;
	private int turnsWithOnlyTeammatesLeft = 0;
	private boolean onlyTeammatesLeft = false;
	// Needed to determine who's on my team.
	// The moves HashMaps determine if a bot meets the team's SW-NE first two moves.
	private HashMap<Integer, BotPoint2D> startingPos = null;
	private HashMap<Integer, BotPoint2D> secondPos = null;
	// This HashMap is used to determine if a new bullet surfaces from a "teammate"
	private HashMap<Integer, BulletPoint2D> bulletsOneAgo = null;
	// Keeps track of the ids of my teammates.
	private HashSet<Integer> teammateIds = new HashSet<Integer>();

	public BrutalBotMPetriOffshootTeammate(Clobber game) {
		super(game);
		mycolor = Color.DARK_GRAY;
		timer = 0;
		shootEm = true;
	}

	/**
	 * This method is called once for each bot for each turn. The bot should look at what it knows, and make an
	 * appropriate decision about what to do.
	 *
	 * @param currState contains info on this bots current position, the position of every other bot and bullet in the
	 * system.
	 */
	@Override
	public ClobberBotAction takeTurn(WhatIKnow currState) {
		start = System.currentTimeMillis();
		timer++;
		setMyPt(currState.me);
		
		// TEAMMATE ADDITIONS
		// I consider someone my teammate if their first move was SW, then their second move was NE.
		if(turnCounter == 2){
			turnCounter++;
			for(BotPoint2D now : currState.bots) {
				if(Double.compare(startingPos.get(now.getID()).x, now.x) == 0 && Double.compare(startingPos.get(now.getID()).y, now.y) == 0) {
					if(Double.compare(secondPos.get(now.getID()).x, now.x - 2.0) == 0 && Double.compare(secondPos.get(now.getID()).y, now.y + 2.0) == 0) {
						teammateIds.add(now.getID());
//						System.out.println("I FOUND A TEAMMATE! : " + now.toString());
					}
				}
			}
		}
		// This checks to see if there are only teammates left on the field.
		onlyTeammatesLeft = true;
		for(BotPoint2D bot : currState.bots) {
			if(!teammateIds.contains(Integer.valueOf(bot.getID())) && onlyTeammatesLeft)
				onlyTeammatesLeft = false;
		}
		// This checks to see if any new bullets are fired when I think there are only teammates left. 
		// If at least one is, I find the nearest bot and remove it from the teammates HashSet.
		if (onlyTeammatesLeft) {
			turnsWithOnlyTeammatesLeft++;
			if(turnsWithOnlyTeammatesLeft > 3) {
				for(BulletPoint2D b : currState.bullets) {
					if(!bulletsOneAgo.containsKey(b.getID())) {
						double minDist = 10000;
						BotPoint2D minDistBot = null;
						for(BotPoint2D bot : currState.bots) {
							// Is this distance function correct?
							if(bot.distance(b) < minDist) {
								minDist = bot.distance(b);
								minDistBot = bot;
							}
						}
						if(teammateIds.contains(minDistBot.getID())) {
							teammateIds.remove(Integer.valueOf(minDistBot.getID()));
							onlyTeammatesLeft = false;
							System.out.println(minDistBot.toString() + " wasn't my teammate after all ... SAD.");
						}
					}
				}
			}
		} else {
			turnsWithOnlyTeammatesLeft = 0;
		}
		// END TEAMMATE ADDITIONS
		
		
		BotPoint2D nearestBot = getClosestBot(currState.bots, myPt); // TEAMMATE ADDITION / CHANGE
		Vector<BulletPoint2D> nearbyBullets = getHittableBullets(getListOfAllNearbyBullets(currState.bullets, myPt));
		Vector<BotPoint2D> nearbyBots = getListOfAllNearbyBots(currState.bots, myPt);
		Vector<BulletPoint2D> nearbyBulletsHeadedToUs = getListOfBulletsHeadedToUs(nearbyBullets, myPt);
		end = System.currentTimeMillis();
		
		if(end - start > 10)
			System.out.println("WHOA! ARMADILLO!");
		
		try {
			// TEAMMATE ADDITION
			if(turnCounter == 0) {
				turnCounter++;
				
				startingPos = new HashMap<Integer, BotPoint2D>();
				for(BotPoint2D toAdd : currState.bots) {
					startingPos.put(toAdd.getID(), toAdd);
				}
				
				bulletsOneAgo = new HashMap<Integer, BulletPoint2D>();
				bulletUpdate(currState.bullets);
				
				return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
			}
			else if(turnCounter == 1) {
				turnCounter++;
				secondPos = new HashMap<Integer, BotPoint2D>();
				for(BotPoint2D toAdd : currState.bots) {
					secondPos.put(toAdd.getID(), toAdd);
				}
				bulletUpdate(currState.bullets);
				return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.RIGHT);
			}
			
			// DEFENSE
			if (!nearbyBulletsHeadedToUs.isEmpty()) {
				int action = determineBestMove(nearbyBullets, nearbyBots);
				bulletUpdate(currState.bullets);
				return move(action);
			}

			// OFFENSE SHOOT IF READY
			offenseTimer++;
			if (offenseTimer % 4 == 0) {
				offenseTimer = 0;
				shootEm = true;
			}
			if (shootEm && timer > 20) {
				shootEm = false;
				timer = 0;
				// TEAMMATE ADDITION / CHANGE
				if(nearestBot != null) {
					bulletUpdate(currState.bullets);
					return shoot(getClobberBotActionDirectionFromRadians(getRadianDirection(myPt, nearestBot)));
				}
				else if(onlyTeammatesLeft) {
					bulletUpdate(currState.bullets);
					return new ClobberBotAction(ClobberBotAction.NONE, ClobberBotAction.UP);
				}
				// END TEAMMATE CHANGE
			}

			// OFFENSE MOVE IN SHOTCLOCK NOT READY
			int action = determineBestMove(nearbyBullets, nearbyBots);
			bulletUpdate(currState.bullets);
			return move(action);

		} catch (Exception ex) {
//			System.out.println("I MESSED UP. HERE's AN EXCEPTION: " + ex.toString());
			bulletUpdate(currState.bullets);
			return randomSafeAction(nearbyBullets, myPt);
		}
	}
	
	private void bulletUpdate(Vector<BulletPoint2D> bullets){
		for(BulletPoint2D bullet : bullets) {
			bulletsOneAgo.put(bullet.getID(), bullet);
		}
	}
	
	public Vector<BulletPoint2D> getHittableBullets(Vector<BulletPoint2D> bullets) {
		Set<BulletPoint2D> reachableBulletSet = new HashSet<BulletPoint2D>();
		int turnsToReachCurrent = 0;
		boolean atLeastOneInGrid = !bullets.isEmpty();
		while (atLeastOneInGrid) {
			turnsToReachCurrent++;

			int bulletX, bulletY;
			for (BulletPoint2D currentBullet : bullets) {
				bulletX = ((int)currentBullet.x) + (turnsToReachCurrent * currentBullet.getXPlus());
				bulletY = ((int)currentBullet.y) + (turnsToReachCurrent * currentBullet.getYPlus());

				if (bulletStillInPlay(bulletX, bulletY)) {
//					System.out.print("turns: " + turnsToReachCurrent + ", bX: " + bulletX + ", bY: " + bulletY + " / ");
					if (canReachBulletFrom(myPt, bulletX, bulletY, turnsToReachCurrent)) {
//						System.out.println("adding bullet" + currentBullet.x + ", " + currentBullet.y);
						reachableBulletSet.add(currentBullet);
					}
				}
			}

			atLeastOneInGrid = false;
			for (BulletPoint2D bulletPoint2D : bullets) {
				bulletX = (int) bulletPoint2D.x + (turnsToReachCurrent * bulletPoint2D.getXPlus());
				bulletY = (int) bulletPoint2D.y + (turnsToReachCurrent * bulletPoint2D.getYPlus());
				atLeastOneInGrid = atLeastOneInGrid || bulletStillInPlay(bulletX, bulletY);
			}
		}

		Vector<BulletPoint2D> reachableBullets = new Vector<BulletPoint2D>();
		for (BulletPoint2D fromSet : reachableBulletSet) {
			reachableBullets.add(fromSet);
		}
		return reachableBullets;
	}

	private boolean bulletStillInPlay(double x, double y) {
		return (x >= 0 && x <= BIG_GRID_SIZE && y >= 0 && y <= BIG_GRID_SIZE);
	}

	private boolean canReachBulletFrom(BotPoint2D startPt, int bulletX, int bulletY, int turns) {
		int buffer = turns * BOT_SPEED;
		int leftX = (int) (startPt.x) - buffer - HALF_BOT_SIZE;
		int rightX = (int) (startPt.x) + buffer + HALF_BOT_SIZE;
		int topY = (int) (startPt.y) - buffer - HALF_BOT_SIZE;
		int bottomY = (int) (startPt.y) + buffer + HALF_BOT_SIZE;
		
//		System.out.println("BOX: bx:" + bulletX + ", by:" + bulletY + ", l:" + leftX + ", r:" + rightX + ", t:" + topY + ", b:" + bottomY);

		if (bulletX > leftX && bulletX < rightX && bulletY < bottomY && bulletY > topY)
			return true;
		else
			return false;
	}

	private int determineBestMove(Vector<BulletPoint2D> nearbyBullets, Vector<BotPoint2D> nearbyBots) throws Exception {
		int action = getNextToWallMovement();
		if (action == -1 || !safeSpot(nearbyBullets, action, myPt)) {
			DangerVector dv = getOverallResultant(nearbyBullets, nearbyBots, myPt);
			action = getClobberBotActionDirectionFromRadians(dv.getDirectionInRadians());
			if (!safeSpot(nearbyBullets, action, myPt)) {
				action = getClobberBotActionDirectionFromRadians(dv.getDirectionInRadians() - PI_TIMES_1_OVER_4);
				if (!safeSpot(nearbyBullets, action, myPt)) {
					action = getClobberBotActionDirectionFromRadians(dv.getDirectionInRadians() + PI_TIMES_1_OVER_4);
				}
			}
		}
		return action;
	}

	protected Vector<BotPoint2D> getAllPossibleNextMoves(BotPoint2D point) {
		Vector<BotPoint2D> botList = new Vector<BotPoint2D>();
		botList.add(new BotPoint2D(point.getX() + 2, point.getY() + 0, 0));
		botList.add(new BotPoint2D(point.getX() + 2, point.getY() + 2, 1));
		botList.add(new BotPoint2D(point.getX() + 0, point.getY() + 2, 2));
		botList.add(new BotPoint2D(point.getX() - 2, point.getY() + 2, 3));
		botList.add(new BotPoint2D(point.getX() - 2, point.getY() + 0, 4));
		botList.add(new BotPoint2D(point.getX() - 2, point.getY() - 2, 5));
		botList.add(new BotPoint2D(point.getX() + 0, point.getY() - 2, 6));
		botList.add(new BotPoint2D(point.getX() + 2, point.getY() - 2, 7));
		return botList;
	}

	private BotPoint2D getNextPointFromActionDirection(BotPoint2D currPt, int actionDirection) {
		if (actionDirection == 0) {
			return new BotPoint2D(currPt.getX() + 2, currPt.getY(), currPt.getID());
		} else if (actionDirection == 1) {
			return new BotPoint2D(currPt.getX() + 2, currPt.getY() + 2, currPt.getID());
		} else if (actionDirection == 2) {
			return new BotPoint2D(currPt.getX(), currPt.getY() + 2, currPt.getID());
		} else if (actionDirection == 3) {
			return new BotPoint2D(currPt.getX() - 2, currPt.getY() + 2, currPt.getID());
		} else if (actionDirection == 4) {
			return new BotPoint2D(currPt.getX() - 2, currPt.getY(), currPt.getID());
		} else if (actionDirection == 5) {
			return new BotPoint2D(currPt.getX() - 2, currPt.getY() - 2, currPt.getID());
		} else if (actionDirection == 6) {
			return new BotPoint2D(currPt.getX(), currPt.getY() - 2, currPt.getID());
		} else if (actionDirection == 7) {
			return new BotPoint2D(currPt.getX() + 2, currPt.getY() - 2, currPt.getID());
		} else {
			return new BotPoint2D(currPt.getX(), currPt.getY(), currPt.getID());
		}
	}

	private int getActionDirectionFromPoints(BotPoint2D myPt, BotPoint2D nextMove) {
		int fromX = (int) myPt.getX(), fromY = (int) myPt.getY();
		int toX = (int) nextMove.getX(), toY = (int) nextMove.getY();
		int x = toX - fromX, y = toY - fromY;
		if (x == 2 && y == 0) {
			return ClobberBotAction.RIGHT;
		} else if (x == 2 && y == 2) {
			return ClobberBotAction.DOWN | ClobberBotAction.RIGHT;
		} else if (x == 0 && y == 2) {
			return ClobberBotAction.DOWN;
		} else if (x == -2 && y == 2) {
			return (ClobberBotAction.DOWN | ClobberBotAction.LEFT);
		} else if (x == -2 && y == 0) {
			return ClobberBotAction.LEFT;
		} else if (x == -2 && y == -2) {
			return ClobberBotAction.UP | ClobberBotAction.LEFT;
		} else if (x == 0 && y == -2) {
			return ClobberBotAction.UP;
		} else if (x == 2 && y == -2) {
			return ClobberBotAction.UP | ClobberBotAction.RIGHT;
		} else {
			return 0;
		}
	}

	/**
	 * Method to get the direction for the clobberBotAction method from a direction in radians.
	 *
	 * @param dirInRads
	 * @return : 0.0 -> ClobberBotAction.RIGHT : PI/2 -> ClobberBotAction.DOWN : PI -> ClobberBotAction.LEFT : 3PI/2 ->
	 * ClobberBotAction.UP
	 */
	protected int getClobberBotActionDirectionFromRadians(double dirInRads) {
		if (dirInRads < 0.0) {
			return getClobberBotActionDirectionFromRadians(dirInRads + PI_TIMES_12_OVER_6);
		}
		if (dirInRads > PI_TIMES_12_OVER_6) {
			return getClobberBotActionDirectionFromRadians(dirInRads - PI_TIMES_12_OVER_6);
		}
		int directionToGo = ((int) (Math.round(dirInRads * FOUR_OVER_PI)) % 8);
		switch (directionToGo) {
			case 0:
				return ClobberBotAction.RIGHT;
			case 1:
				return ClobberBotAction.DOWN | ClobberBotAction.RIGHT;
			case 2:
				return ClobberBotAction.DOWN;
			case 3:
				return (ClobberBotAction.DOWN | ClobberBotAction.LEFT);
			case 4:
				return ClobberBotAction.LEFT;
			case 5:
				return ClobberBotAction.UP | ClobberBotAction.LEFT;
			case 6:
				return ClobberBotAction.UP;
			default:
				return ClobberBotAction.UP | ClobberBotAction.RIGHT;
		}
	}

	private ClobberBotAction randomSafeAction(Vector<BulletPoint2D> nearbyBullets, BotPoint2D point) {
		int action = getClobberBotActionDirectionFromRadians(rand.nextInt(8));
		for (int i = 0; i < 5; i++) {
			if (!againstTheWall(point) && safeSpot(nearbyBullets, action, point)) {
				return new ClobberBotAction(1, action);
			}
			action = getClobberBotActionDirectionFromRadians(rand.nextInt(8));
		}
		return new ClobberBotAction(1, action);
	}

	private ClobberBotAction move(int clobberBotActionDirection) {
		return new ClobberBotAction(1, clobberBotActionDirection);
	}

	private ClobberBotAction shoot(int clobberBotActionDirection) {
		return new ClobberBotAction(2, clobberBotActionDirection);
	}

	private ClobberBotAction noAction() {
		return new ClobberBotAction(0, 0);
	}

	/**
	 * Method to determine if the bullet has a trajectory towards the given point. The point is the center of a Bot and
	 * thus a buffer around the tank will be checked. Thus each pixel of the edges of a square that has sides at size
	 * 15+2*bufferAmount will be looked at to see if in the path.
	 *
	 * @param bullet
	 * @param myPt
	 * @return
	 */
	protected boolean isInBulletTrajectory(BulletPoint2D bullet, Point2D myPt) {
		// slope is vertical
		if ((int) bullet.getXPlus() == 0) {
			int buX = (int) bullet.getX();
			int myX = (int) myPt.getX();
			for (int i = 0; i < fullBufferAmount; i++) {
				if (buX == (myX - halfBufferAmount + i)) {
					return true;
				}
			}
		} // slope is horizontal
		else if ((int) bullet.getYPlus() == 0) {
			int buY = (int) bullet.getY();
			int myY = (int) myPt.getY();
			for (int i = 0; i < fullBufferAmount; i++) {
				if (buY == (myY - halfBufferAmount + i)) {
					return true;
				}
			}
		} // all other slopes
		else {
			int x2 = (int) bullet.getX();
			int y2 = (int) bullet.getY();
			int xPlus = bullet.getXPlus();
			int yPlus = bullet.getYPlus();
			int myX = (int) myPt.getX();
			int myY = (int) myPt.getY();
			int slope = (int) (bullet.getYPlus() / bullet.getXPlus());
			for (int i = 0; i < fullBufferAmount; i++) {
				// north edge
				int x1 = myX - halfBufferAmount + i;
				int y1 = myY - halfBufferAmount;
				if (arePointsOnSameLineWithGivenSlope(y2, y1, x2, x1, slope, xPlus, yPlus)) {
					return true;
				}
				// west edge
				x1 = myX - halfBufferAmount;
				y1 = myY - halfBufferAmount + i;
				if (arePointsOnSameLineWithGivenSlope(y2, y1, x2, x1, slope, xPlus, yPlus)) {
					return true;
				}
				// east edge
				x1 = myX + halfBufferAmount;
				y1 = myY - halfBufferAmount + i;
				if (arePointsOnSameLineWithGivenSlope(y2, y1, x2, x1, slope, xPlus, yPlus)) {
					return true;
				}
				// south edge
				x1 = myX - halfBufferAmount + i;
				y1 = myY + halfBufferAmount;
				if (arePointsOnSameLineWithGivenSlope(y2, y1, x2, x1, slope, xPlus, yPlus)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method to calculate the resultant DangerVector object.
	 *
	 * @param dangerVector
	 * @return
	 */
	public DangerVector addDangerVectors(List<DangerVector> dangerVector) throws Exception {
		if (dangerVector.isEmpty()) {
			return new DangerVector(0.0, 0.0);
		}
		double sumOfMagnInX = 0.0;
		double sumOfMagnInY = 0.0;
		for (DangerVector dv : dangerVector) {
			sumOfMagnInX += dv.getDangerMagnitudeInX();
			sumOfMagnInY += dv.getDangerMagnitudeInY();
		}
		double epsilon = 0.000001;
		int zeroCount = 0;
		if (Math.abs(sumOfMagnInX) < epsilon) {
			sumOfMagnInX = 0.0;
			zeroCount++;
		}
		if (Math.abs(sumOfMagnInY) < epsilon) {
			sumOfMagnInY = 0.0;
			zeroCount++;
		}
		if (zeroCount == 2) {
			return new DangerVector(0.0, 0.0);
		}
		Point2D origin = new Point2D.Double(0.0, 0.0);
		Point2D resPoint = new Point2D.Double(sumOfMagnInX, sumOfMagnInY);
		double resultantDirInRads = getRadianDirection(origin, resPoint);
		double resultantDangerMagnitude = calculateResultantDangerMagnitude(sumOfMagnInX, sumOfMagnInY);
		return new DangerVector(resultantDangerMagnitude, resultantDirInRads);
	}

	/**
	 * Calculates the magnitude given a magnitude component of x and of y
	 *
	 * @param sumOfMagnInX
	 * @param sumOfMagnInY
	 * @return double
	 */
	private static double calculateResultantDangerMagnitude(double sumOfMagnInX, double sumOfMagnInY) {
		double sqSumInX;
		double sqSumInY;
		double resultantDangerMagnitude;
		sqSumInX = sumOfMagnInX * sumOfMagnInX;
		sqSumInY = sumOfMagnInY * sumOfMagnInY;
		resultantDangerMagnitude = Math.sqrt(sqSumInX + sqSumInY);
		return resultantDangerMagnitude;
	}

	/**
	 * Method to calculate the resultant vector from only the other bots.
	 *
	 * @param bots
	 * @param point
	 * @return
	 * @throws Exception
	 */
	protected DangerVector calculateBotResultant(Vector<BotPoint2D> bots, BotPoint2D point) throws Exception {
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		for (BotPoint2D bPt : bots) {
			dvList.add(new DangerVector(getBotMagnitude(bPt, point), getRadianDirection(bPt, point)));
		}
		return addDangerVectors(dvList);
	}

	protected DangerVector getOverallResultant(Vector<BulletPoint2D> bullets, Vector<BotPoint2D> bots, BotPoint2D point) throws Exception {
		DangerVector botDV = calculateBotResultant(bots, point);
		DangerVector wallDV = calculateWallResultant(point);
		DangerVector bulletDV = calculateBulletResultant(bullets, point);
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		dvList.add(botDV);
		dvList.add(wallDV);
		dvList.add(bulletDV);
		return addDangerVectors(dvList);
	}

	/**
	 * Method to calculate resultant due only to the wall forces. The force pushing from the right wall(0) acts on the
	 * tank with a direction of PI The force pushing from the left wall(4) acts on the tank with a direction of 0 The
	 * force pushing from the top wall(6) acts on the tank with a direction of PI/2 The force pushing from the bottom
	 * wall(2) acts on the tank with a direction of 3PI/2
	 *
	 * @param point
	 * @return
	 * @throws Exception
	 */
	protected DangerVector calculateWallResultant(BotPoint2D point) throws Exception {
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		dvList.add(new DangerVector(getWallMagnitude(point, 6), PI_TIMES_3_OVER_6));
		dvList.add(new DangerVector(getWallMagnitude(point, 0), PI_TIMES_6_OVER_6));
		dvList.add(new DangerVector(getWallMagnitude(point, 2), PI_TIMES_9_OVER_6));
		dvList.add(new DangerVector(getWallMagnitude(point, 4), 0.0));
		return addDangerVectors(dvList);
	}

	/**
	 * Method to get the resultant vector based only on the bullets.
	 *
	 * @param bullets
	 * @param point
	 * @return
	 * @throws Exception
	 */
	protected DangerVector calculateBulletResultant(Vector<BulletPoint2D> bullets, BotPoint2D point) throws Exception {
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		for (BulletPoint2D bPt : bullets) {
			dvList.add(new DangerVector(getBulletMagnitude(bPt, point), getRadianDirection(bPt, point)));
		}
		return addDangerVectors(dvList);
	}

	/**
	 * Method for getting the magnitude for a given wall direction.
	 *
	 * @param myPt - BotPoint2D
	 * @param wallDirection - 0 -> right wall : 2 -> bottom wall : 4 -> left wall : 6 -> top wall
	 * @return double
	 * @throws Exception
	 */
	protected double getWallMagnitude(BotPoint2D myPt, int wallDirection) throws Exception {
		double objDistFromMe = 0;
		switch (wallDirection) {
			case 0:
				objDistFromMe = 600 - myPt.getX();
				break;
			case 2:
				objDistFromMe = 600 - myPt.getY();
				break;
			case 4:
				objDistFromMe = myPt.getX();
				break;
			case 6:
				objDistFromMe = myPt.getY();
				break;
			default:
				throw new Exception("Invalid wall direction given");
		}
		double out = calculateMagnitude(objDistFromMe, WALL_MIN_CARE_DIST, WALL_NUMERATOR_FACTOR, WALL_POWER_FACTOR);
		return out;
	}

	/**
	 * Method for getting the magnitude for a given bot.
	 *
	 * @param bPt
	 * @param myPt
	 * @return
	 */
	protected double getBotMagnitude(BotPoint2D bPt, BotPoint2D myPt) {
		double objDistFromMe = bPt.distance(myPt);
		return calculateMagnitude(objDistFromMe, BOT_MIN_CARE_DIST, BOT_NUMERATOR_FACTOR, BOT_POWER_FACTOR);
	}

	/**
	 * Method for getting the magnitude for a given bullet.
	 *
	 * @param bPt
	 * @param myPt
	 * @return
	 */
	protected double getBulletMagnitude(BulletPoint2D bPt, BotPoint2D myPt) {
		double objDistFromMe = bPt.distance(myPt);
		return calculateMagnitude(objDistFromMe, BULLET_MIN_CARE_DIST, BULLET_NUMERATOR_FACTOR, BULLET_POWER_FACTOR);
	}

	/**
	 * Method to get magnitude within a minimum care distance.
	 *
	 * @param distFrom
	 * @param minCareDist
	 * @param numFctr
	 * @param pwrFctr
	 * @return the magnitude - double
	 */
	private double calculateMagnitude(double distFrom, double minCareDist, double numFctr, double pwrFctr) {
		if (distFrom < minCareDist) {
			return Math.pow(numFctr / distFrom, pwrFctr);
		} else {
			return 0;
		}
	}

	@Override
	public void drawMe(Graphics page, Point2D me) {
		int x, y;
		x = (int) me.getX() - Clobber.MAX_BOT_GIRTH / 2 - 1;
		y = (int) me.getY() - Clobber.MAX_BOT_GIRTH / 2 - 1;
		page.setColor(mycolor);
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				int r = (17 * Math.abs(i - j)) % 256;
				int g = (18 * Math.abs(i - j)) % 256;
				int b = (19 * Math.abs(i - j)) % 256;
				Color nextColor = new Color(r, g, b);
				page.setColor(nextColor);
				page.drawLine(x + i, y + j, x + i, y + j);
			}
		}
	}

	/**
	 * This method calculates the angle from the horizontal positive x-axis counterclockwise to the line extending from
	 * the vertexPoint to the otherPoint. In the ClobberBot grid, this ends up being a clockwise movement since the
	 * y-axis is positive in the downward direction. This method has been tested.
	 *
	 * @param vertexPoint: Point2D
	 * @param otherPoint: Point2D
	 * @return - angle in radians from 0 to 2*PI
	 * @throws Exception - when all conditional if statements have failed.
	 */
	protected double getRadianDirection(Point2D vertexPoint, Point2D otherPoint) throws Exception {
		double deltaX = otherPoint.getX() - vertexPoint.getX();
		double deltaY = otherPoint.getY() - vertexPoint.getY();

		// First Quadrant
		if (deltaX > 0 && deltaY > 0) {
			return Math.atan(deltaY / deltaX);
		}
		// Second Quadrant
		if (deltaX < 0 && deltaY > 0) {
			return Math.atan(deltaY / deltaX) + PI_TIMES_6_OVER_6;
		}
		// Third Quadrant
		if (deltaX < 0 && deltaY < 0) {
			return Math.atan(deltaY / deltaX) + PI_TIMES_6_OVER_6;
		}
		// Fourth Quadrant
		if (deltaX > 0 && deltaY < 0) {
			return Math.atan(deltaY / deltaX) + PI_TIMES_12_OVER_6;
		}
		// 90 degrees
		if (deltaX == 0 && deltaY > 0) {
			return PI_TIMES_3_OVER_6;
		}
		// 270 degrees
		if (deltaX == 0 && deltaY < 0) {
			return PI_TIMES_9_OVER_6;
		}
		// 0 degrees
		if (deltaX > 0 && deltaY == 0) {
			return PI_TIMES_0_OVER_6;
		}
		// 180 degrees
		if (deltaX < 0 && deltaY == 0) {
			return PI_TIMES_6_OVER_6;
		}
		// same point
		if (deltaX == 0 && deltaY == 0) {
			return PI_TIMES_0_OVER_6;
		}
		// didn't find a condition // should be impossible to get
		String exceptionMessage = "Exception thrown for vertexPoint @ "
								  + vertexPoint.toString() + " otherPoint @ " + otherPoint.toString();
		throw new Exception(exceptionMessage);
	}

	/**
	 * This method checks to see if slope = (y2 - y1) / (x2 - x1). This method has been tested.
	 *
	 * @param bulletY
	 * @param meY
	 * @param bulletX
	 * @param meX
	 * @param slope
	 * @return
	 */
	protected boolean arePointsOnSameLineWithGivenSlope(int bulletY, int meY, int bulletX, int meX, int slope, int xPlus, int yPlus) {
		// Point 1 is us.

//		if ((meX < bulletX && xPlus >= 0) || (meX > bulletX && xPlus <= 0) || (meY < bulletY && yPlus >= 0) || (meY > bulletY && yPlus <= 0))
//			return false;
		int changeInX = bulletX - meX;
		if (changeInX == 0) {
			return false;
		}
		int changeInY = bulletY - meY;
		int cmp = changeInY / changeInX;
		if (cmp == slope) {
			return true;
		}
		return false;
	}

	/**
	 * This method gets the closest bot to the given point and returns it.
	 *
	 * @param bots - Vector<BotPoint2D>
	 * @param point - Point2D
	 * @return retBot - BotPoint2D or null if no bots
	 */
	protected BotPoint2D getClosestBot(Vector<BotPoint2D> bObjs, Point2D point) {
		// TEAMMATE ADDITION / CHANGE
		// I don't want to even care about my teammates
        ArrayList<BotPoint2D> toRemove = new ArrayList<BotPoint2D>();
        for(int teammateCheck = 0; teammateCheck < bObjs.size(); teammateCheck++) {
               if(teammateIds.contains(bObjs.get(teammateCheck).getID()))
                     toRemove.add(bObjs.get(teammateCheck));
        }
        for(BotPoint2D bot : toRemove) {
               bObjs.remove(bot);
        }
        // END TEAMMATE CHANGE
		
		BotPoint2D retObj = null;
		double dist = 100000;
		for (BotPoint2D bPt : bObjs) {
			double bPtDist = bPt.distance(point);
			if (bPtDist < dist) {
				retObj = bPt;
				dist = bPtDist;
			}
		}
		return retObj;

	}

	/**
	 * This method gets the closest bullet to the given point and returns it.
	 *
	 * @param bObj - Vector<BulletPoint2D>
	 * @param point - Point2D
	 * @return retBot - BulletPoint2D
	 */
	protected BulletPoint2D getClosestBullet(Vector<BulletPoint2D> bObjs, Point2D point) {
		BulletPoint2D retObj = null;
		double dist = 100000;
		for (BulletPoint2D bPt : bObjs) {
			double bPtDist = bPt.distance(point);
			if (bPtDist < dist) {
				retObj = bPt;
				dist = bPtDist;
			}
		}
		return retObj;
	}

	private boolean againstTheWall(BotPoint2D myPt) {
		if (myPt.getX() < WALL_MIN_CARE_DIST / 2 || myPt.getX() > 600 - WALL_MIN_CARE_DIST / 2) {
			return true;
		}
		if (myPt.getY() < WALL_MIN_CARE_DIST / 2 || myPt.getY() > 600 - WALL_MIN_CARE_DIST / 2) {
			return true;
		}
		return false;
	}

	private Vector<BulletPoint2D> getListOfAllNearbyBullets(Vector<BulletPoint2D> bullets, BotPoint2D myPt) {
		Vector<BulletPoint2D> nearbyBullets = new Vector<BulletPoint2D>();
		for (BulletPoint2D bullet : bullets) {
			if (bullet.distance(myPt) < BULLET_MIN_CARE_DIST) {
				nearbyBullets.add(bullet);
			}
		}
		return nearbyBullets;
	}

	private Vector<BotPoint2D> getListOfAllNearbyBots(Vector<BotPoint2D> bots, BotPoint2D myPt) {
		Vector<BotPoint2D> nearbyBots = new Vector<BotPoint2D>();
		for (BotPoint2D bot : bots) {
			if (bot.distance(myPt) < BOT_MIN_CARE_DIST) {
				nearbyBots.add(bot);
			}
		}
		return nearbyBots;
	}

	private Vector<BulletPoint2D> getListOfBulletsHeadedToUs(Vector<BulletPoint2D> bullets, BotPoint2D myPt) {
		Vector<BulletPoint2D> bulletsHeadedToUs = new Vector<BulletPoint2D>();
		for (BulletPoint2D bullet : bullets) {
			if (isInBulletTrajectory(bullet, myPt)) {
				bulletsHeadedToUs.add(bullet);
			}
		}
		return bulletsHeadedToUs;
	}

	private BotPoint2D getSafeNextBotPoint(Vector<BulletPoint2D> bullets, Vector<BotPoint2D> possNextMoves) {
		for (BotPoint2D possNextMove : possNextMoves) {
			Vector<BulletPoint2D> bulletsHeadedToUs = getListOfBulletsHeadedToUs(bullets, possNextMove);
			if (bulletsHeadedToUs.isEmpty()) {
				return possNextMove;
			}
		}
		return null;
	}

	private int determineNextBestClobberBotMoveDirection(Vector<BulletPoint2D> bullets, BotPoint2D enemyBot, BotPoint2D myPt) throws Exception {
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		for (BulletPoint2D bullet : bullets) {
			DangerVector dv = new DangerVector(getBulletMagnitude(bullet, myPt), getRadianDirection(bullet, myPt));
			dvList.add(dv);
		}
		DangerVector dv = new DangerVector(getBotMagnitude(enemyBot, myPt), getRadianDirection(enemyBot, myPt));
		dvList.add(dv);
		DangerVector resultant = addDangerVectors(dvList);

		return getClobberBotActionDirectionFromRadians(resultant.getDirectionInRadians() + PI_TIMES_1_OVER_4);

	}

	private boolean safeSpot(Vector<BulletPoint2D> nearbyBullets, int action, BotPoint2D myPt) {
		BotPoint2D nextPoint = getNextPointFromActionDirection(myPt, action);
		Vector<BulletPoint2D> bulletsHeadedToNewSpot = getListOfBulletsHeadedToUs(nearbyBullets, nextPoint);
		if (bulletsHeadedToNewSpot.isEmpty()) {
			return true;
		}
		return false;
	}

	private int getNextToWallMovement() {
		int action;
		int lowEnd = (int) WALL_MIN_CARE_DIST;
		int highEnd = 600 - lowEnd;
		if (myPt.getX() < lowEnd && myPt.getY() <= lowEnd) {
			action = 0;
		} else if (myPt.getX() < lowEnd && myPt.getY() > lowEnd) {
			action = 7;
		} else if (myPt.getX() < highEnd && myPt.getY() < lowEnd) {
			action = 1;
		} else if (myPt.getX() >= highEnd && myPt.getY() < lowEnd) {
			action = 2;
		} else if (myPt.getX() > highEnd && myPt.getY() < highEnd) {
			action = 3;
		} else if (myPt.getX() > highEnd && myPt.getY() >= highEnd) {
			action = 4;
		} else if (myPt.getX() > lowEnd && myPt.getY() > highEnd) {
			action = 5;
		} else if (myPt.getX() <= lowEnd && myPt.getY() > highEnd) {
			action = 6;
		} else {
			action = -1;
		}
		return action;
	}

	/**
	 * An inner class for an object that represents a danger vector. The main components of the vector are direction and
	 * magnitude.
	 */
	public class DangerVector {
		private double dangerMagnitude;
		private double directionInRadians;
		private double directionComponentInX;
		private double directionComponentInY;
		private double dangerMagnitudeInX;
		private double dangerMagnitudeInY;

		public DangerVector(double dangerMagnitude, double directionInRadians) {
			setDangerMagnitude(dangerMagnitude);
			setDirectionInRadians(directionInRadians);
			setDirectionComponentInX();
			setDirectionComponentInY();
			setDangerMagnitudeInX();
			setDangerMagnitudeInY();
		}

		///////////////////////////////////////////////////////////////////////////
		// Getters and Setters
		///////////////////////////////////////////////////////////////////////////
		public double getDangerMagnitude() {
			return dangerMagnitude;
		}

		private void setDangerMagnitude(double dangerMagnitude) {
			this.dangerMagnitude = dangerMagnitude;
		}

		public double getDirectionInRadians() {
			return directionInRadians;
		}

		/**
		 * Method that sets the direction of the vector in radians to be between 0 and 2PI.
		 *
		 * @param directionInRadians
		 */
		private void setDirectionInRadians(double directionInRadians) {
			if (directionInRadians >= 0.0 && directionInRadians <= PI_TIMES_12_OVER_6) {
				this.directionInRadians = directionInRadians;
			} else if (directionInRadians < 0.0) {
				setDirectionInRadians(directionInRadians + PI_TIMES_12_OVER_6);
			} else if (directionInRadians > PI_TIMES_12_OVER_6) {
				setDirectionInRadians(directionInRadians - PI_TIMES_12_OVER_6);
			}
		}

		public double getDirectionComponentInX() {
			return directionComponentInX;
		}

		private void setDirectionComponentInX() {
			directionComponentInX = Math.cos(directionInRadians);
		}

		public double getDirectionComponentInY() {
			return directionComponentInY;
		}

		private void setDirectionComponentInY() {
			directionComponentInY = Math.sin(directionInRadians);
		}

		public double getDangerMagnitudeInX() {
			return dangerMagnitudeInX;
		}

		private void setDangerMagnitudeInX() {
			dangerMagnitudeInX = dangerMagnitude * directionComponentInX;
		}

		public double getDangerMagnitudeInY() {
			return dangerMagnitudeInY;
		}

		private void setDangerMagnitudeInY() {
			dangerMagnitudeInY = dangerMagnitude * directionComponentInY;
		}

		///////////////////////////////////////////////////////////////////////////
		// The infamous toString()
		///////////////////////////////////////////////////////////////////////////
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(" dangerMagnitude: ").append(getDangerMagnitude()).append("\n");
			sb.append(" directionInRadians: ").append(getDirectionInRadians()).append("\n");
			return sb.toString();
		}
	}

	/**
	 * Method for testing the DangerVector class logic.
	 *
	 * @return
	 */
	public boolean testFor_DangerVector_class() {
		double epsilon = 0.0000001;
		DangerVector dv = new DangerVector(10.0, Math.PI);
		double diff = Math.abs(dv.getDirectionInRadians() - Math.PI);
		if (diff > epsilon) {
			return false;
		}
		dv = new DangerVector(10.0, 15 * Math.PI);
		diff = Math.abs(dv.getDirectionInRadians() - Math.PI);
		if (diff > epsilon) {
			return false;
		}
		dv = new DangerVector(10.0, -3 * Math.PI);
		diff = Math.abs(dv.getDirectionInRadians() - Math.PI);
		if (diff > epsilon) {
			return false;
		}
		return true;
	}

	/**
	 * Method for testing the addDangerVectors method.
	 *
	 * @return
	 * @throws Exception
	 */
	public boolean testFor_addDangerVectors_method_1() throws Exception {
		double epsilon = 0.0000001;
		DangerVector dv1 = new DangerVector(10.0, 0.0);
		DangerVector dv2 = new DangerVector(10.0, Math.PI / 2);
		DangerVector dv3 = new DangerVector(10.0, Math.PI);
		DangerVector dv4 = new DangerVector(10.0, 3 * Math.PI / 2);
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		dvList.add(dv1);
		dvList.add(dv2);
		dvList.add(dv3);
		dvList.add(dv4);
		DangerVector resultant = addDangerVectors(dvList);
		if (Math.abs(resultant.getDangerMagnitude()) > epsilon
			|| Math.abs(resultant.getDirectionInRadians()) > epsilon) {
			return false;
		}
		return true;
	}

	/**
	 * Method for testing the addDangerVectors method.
	 *
	 * @return
	 * @throws Exception
	 */
	public boolean testFor_addDangerVectors_method_2() throws Exception {
		double epsilon = 0.0000001;
		DangerVector dv1 = new DangerVector(10.0, 0.0);
		DangerVector dv2 = new DangerVector(10.0, Math.PI / 2.0);
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		dvList.add(dv1);
		dvList.add(dv2);
		DangerVector resultant = addDangerVectors(dvList);
		if (Math.abs(resultant.getDangerMagnitude() - Math.sqrt(200.0)) > epsilon
			|| Math.abs(resultant.getDirectionInRadians() - Math.PI / 4.0) > epsilon) {
			return false;
		}
		return true;
	}

	/**
	 * Method for testing the addDangerVectors method.
	 *
	 * @return
	 * @throws Exception
	 */
	public boolean testFor_addDangerVectors_method_3() throws Exception {
		double epsilon = 0.0000001;
		DangerVector dv1 = new DangerVector(100.0, 4.0 * Math.PI / 3);
		DangerVector dv2 = new DangerVector(25.0, -1.0 * Math.PI / 6);
		List<DangerVector> dvList = new ArrayList<DangerVector>();
		dvList.add(dv1);
		dvList.add(dv2);
		DangerVector resultant = addDangerVectors(dvList);
		if (Math.abs(resultant.getDangerMagnitude() - 103.0776406404) > epsilon
			|| Math.abs(resultant.getDirectionInRadians() - 4.4337688679) > epsilon) {
			return false;
		}
		return true;
	}

	protected double getBulletMinCareDist() {
		return BULLET_MIN_CARE_DIST;
	}

	protected double getBulletNumeratorFactor() {
		return BULLET_NUMERATOR_FACTOR;
	}

	protected double getBulletPowerFactor() {
		return BULLET_POWER_FACTOR;
	}

	protected double getBotMinCareDist() {
		return BOT_MIN_CARE_DIST;
	}

	protected double getBotNumeratorFactor() {
		return BOT_NUMERATOR_FACTOR;
	}

	protected double getBotPowerFactor() {
		return BOT_POWER_FACTOR;
	}

	protected double getWallMinCareDist() {
		return WALL_MIN_CARE_DIST;
	}

	protected double getWallNumeratorFactor() {
		return WALL_NUMERATOR_FACTOR;
	}

	protected double getWallPowerFactor() {
		return WALL_POWER_FACTOR;
	}

	public DangerVector getWallResultant() {
		return wallResultant;
	}

	public DangerVector getBotResultant() {
		return botResultant;
	}

	public DangerVector getBulletResultant() {
		return bulletResultant;
	}

	public BotPoint2D getMyPt() {
		return myPt;
	}

	public void setMyPt(BotPoint2D myPt) {
		this.myPt = myPt;
	}

	/**
	 * Your bots identifier string. It must be unique from other players, since I use it to determine who your teammates
	 * are. You can include your login name in the id to guarantee uniqueness.
	 */
	@Override
	public String toString() {
		return "BrutalBotMPetriOffshootTeammate";
	}
}
