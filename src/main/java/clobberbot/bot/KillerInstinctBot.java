package clobberbot.bot;

import clobberbot.BotPoint2D;
import clobberbot.BulletPoint2D;
import clobberbot.Clobber;
import clobberbot.ClobberBot;
import clobberbot.ClobberBotAction;
import clobberbot.WhatIKnow;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * This class implements an example ClobberBot1 that makes random moves. All
 * ClobberBots should extend this class and override the takeTurn and drawMe
 * methods.
 */
public class KillerInstinctBot extends ClobberBot {

    private static final double BULLET_POWER = 3.0d;
    private static final double BULLET_CONSTANT = 500.0d;
    private static final double BULLET_DANGER_DISTANCE = 35d;
    private static final double WALL_POWER = 2.0d;
    private static final double WALL_CONSTANT = 5.0d;
    private static final double WALL_DANGER_DISTANCE = 100d;
    private static final double BOT_POWER = 3.0d;
    private static final double BOT_CONSTANT = 500.0d;
    private static final double BOT_DANGER_DISTANCE = 60d;
    private static final double BOT_ATTRACTION = 0.000d;
    private static final double SHOOT_MAGNITUDE = 0.0001d;
    private static final double SHOOT_ANGLE_MIN = 0.25d;
    private int SHOT_CLOCK = 0;

    public KillerInstinctBot(Clobber game) {
        super(game);
        mycolor = Color.BLUE;
    }

    /**
     * This method is called once for each bot for each turn. The bot should
     * look at what it knows, and make an appropriate decision about what to do.
     *
     * @param currState contains info on this bots current position, the
     * position of every other bot and bullet in the system.
     */
    @Override
    public ClobberBotAction takeTurn(WhatIKnow currState) {
        SHOT_CLOCK--;

        MyVector bulletVector = getBulletVector(currState);
        MyVector botVector = getBotVector(currState);
        MyVector wallVector = getWallVector(currState);

        MyVector moveVector = new MyVector();
        moveVector.addVector(bulletVector);
        moveVector.addVector(botVector);
        moveVector.addVector(wallVector);

        if (SHOT_CLOCK < 0 && moveVector.getMagnitude() < SHOOT_MAGNITUDE) {
            moveVector.setMagnitude(0);
            SHOT_CLOCK = Clobber.SHOTFREQUENCY;
        }

        if (moveVector.getX() < 0.0 && moveVector.getY() < 0.0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.LEFT);
        } else if (moveVector.getX() < 0.0 && moveVector.getY() > 0.0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.LEFT);
        } else if (moveVector.getX() > 0.0 && moveVector.getY() < 0.0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP | ClobberBotAction.RIGHT);
        } else if (moveVector.getX() > 0.0 && moveVector.getY() > 0.0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN | ClobberBotAction.RIGHT);
        } else if (moveVector.getX() == 0 && moveVector.getY() < 0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.UP);
        } else if (moveVector.getX() == 0 && moveVector.getY() > 0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.DOWN);
        } else if (moveVector.getY() == 0 && moveVector.getX() < 0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.LEFT);
        } else if (moveVector.getY() == 0 && moveVector.getX() > 0) {
            return new ClobberBotAction(ClobberBotAction.MOVE, ClobberBotAction.RIGHT);
        } else {
            return new ClobberBotAction(ClobberBotAction.SHOOT, findClosestBotDirection(currState));
        }
    }

    private int findClosestBotDirection(WhatIKnow currState) {
        double closest = Clobber._width;
        BotPoint2D closestBot = currState.bots.firstElement();
        for (BotPoint2D bot : currState.bots) {
            double distance = bot.distance(currState.me);
            if (distance < closest) {
                closest = distance;
                closestBot = bot;
            }
        }
        double x = closestBot.x - currState.me.x;
        double y = closestBot.y - currState.me.y;
//		System.out.println("X: " + x + " Y: " + y + " x/y: " + (x / y) + " y/x: " + (y / x));
        if (Math.abs(x / y) < SHOOT_ANGLE_MIN) {
            x = 0;
        }
        if (Math.abs(y / x) < SHOOT_ANGLE_MIN) {
            y = 0;
        }

        if (x < 0 && y < 0) {
            return ClobberBotAction.UP | ClobberBotAction.LEFT;
        } else if (x < 0 && y > 0) {
            return ClobberBotAction.DOWN | ClobberBotAction.LEFT;
        } else if (x > 0 && y < 0) {
            return ClobberBotAction.UP | ClobberBotAction.RIGHT;
        } else if (x < 0 && y > 0) {
            return ClobberBotAction.DOWN | ClobberBotAction.RIGHT;
        } else if (x == 0 && y > 0) {
            return ClobberBotAction.DOWN;
        } else if (x == 0 && y < 0) {
            return ClobberBotAction.UP;
        } else if (x < 0 && y == 0) {
            return ClobberBotAction.LEFT;
        } else {
            return ClobberBotAction.RIGHT;
        }
    }

    private boolean canHitMe(BulletPoint2D bullet, BotPoint2D me) {
        double danger_distance = bulletIsOnDiagonal(bullet) ? BULLET_DANGER_DISTANCE * 1.5 : BULLET_DANGER_DISTANCE;
        if (me.distance(bullet) < danger_distance
                && ((me.y < bullet.y && bullet.getYPlus() < 0)
                || (me.y > bullet.y && bullet.getYPlus() > 0)
                || (me.x < bullet.x && bullet.getXPlus() < 0)
                || (me.x > bullet.x && bullet.getXPlus() > 0))) {
            return true;
        }
        return false;
    }

    private boolean bulletIsOnDiagonal(BulletPoint2D bullet) {
        return bullet.getXPlus() != 0 && bullet.getYPlus() != 0;
    }

    private boolean botIsOnDiagonal(BotPoint2D enemy, BotPoint2D me) {
        return enemy.x - me.x != 0 && enemy.y - me.y != 0;
    }

    private MyVector getBulletVector(WhatIKnow currState) {
        MyVector vector = new MyVector();

        for (BulletPoint2D bullet : currState.bullets) {
            if (canHitMe(bullet, currState.me)) {
                MyVector bulletVector = new MyVector(currState.me.getX() - bullet.getX(), currState.me.getY() - bullet.getY());
                bulletVector.setMagnitude(BULLET_CONSTANT / Math.pow(bulletVector.getMagnitude(), BULLET_POWER));

                if (bulletIsOnDiagonal(bullet)) {
                    if (isLeft(bullet, currState.me)) {
                        bulletVector.rotate90();
                    } else {
                        bulletVector.rotate270();
                    }
                }
                vector.addVector(bulletVector);
            }
        }

        return vector;
    }

    private boolean isLeft(BulletPoint2D bullet, BotPoint2D me) {
        BulletPoint2D a = bullet;
        BulletPoint2D b = new BulletPoint2D(bullet.getX() + bullet.getXPlus(), bullet.getY() + bullet.getYPlus(), 0, 0, 0);
        BotPoint2D c = me;
        return ((b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)) > 0;
    }

    private MyVector getBotVector(WhatIKnow currState) {
        MyVector vector = new MyVector();

        for (BotPoint2D bot : currState.bots) {
            double danger_distance = botIsOnDiagonal(bot, currState.me) ? BOT_DANGER_DISTANCE * 1.5 : BOT_DANGER_DISTANCE;
            if (bot.distance(currState.me) < danger_distance) {
                MyVector botVector = new MyVector(currState.me.getX() - bot.getX(), currState.me.getY() - bot.getY());
                botVector.setMagnitude(BOT_CONSTANT / Math.pow(botVector.getMagnitude(), BOT_POWER));
                vector.addVector(botVector);
            } else {
                MyVector botVector = new MyVector(bot.getX() - currState.me.getX(), bot.getY() - currState.me.getY());
                botVector.setMagnitude(BOT_ATTRACTION);
                vector.addVector(botVector);
            }
        }

        return vector;
    }

    private MyVector getWallVector(WhatIKnow currState) {
        MyVector wallVector = new MyVector();

        MyVector xWallVector;
        if (currState.me.x < WALL_DANGER_DISTANCE) {
            xWallVector = new MyVector(1d, 0d);
            double distance = currState.me.x;
            xWallVector.setMagnitude(WALL_CONSTANT / Math.pow(distance, WALL_POWER));
        } else if (Clobber._width - currState.me.x < WALL_DANGER_DISTANCE) {
            xWallVector = new MyVector(-1d, 0d);
            double distance = Clobber._width - currState.me.x;
            xWallVector.setMagnitude(WALL_CONSTANT / Math.pow(distance, WALL_POWER));
        } else {
            xWallVector = new MyVector();
        }

        MyVector yWallVector;
        if (currState.me.y < WALL_DANGER_DISTANCE) {
            yWallVector = new MyVector(0d, 1d);
            double distance = currState.me.y;
            yWallVector.setMagnitude(WALL_CONSTANT / Math.pow(distance, WALL_POWER));
        } else if (Clobber._height - currState.me.y < WALL_DANGER_DISTANCE) {
            yWallVector = new MyVector(0d, -1d);
            double distance = Clobber._height - currState.me.y;
            yWallVector.setMagnitude(WALL_CONSTANT / Math.pow(distance, WALL_POWER));
        } else {
            yWallVector = new MyVector();
        }

        wallVector.addVector(xWallVector);
        wallVector.addVector(yWallVector);

        return wallVector;
    }

    /**
     * Your bots identifier string. It must be unique from other players, since
     * I use it to determine who your teammates are. You can include your login
     * name in the id to guarantee uniqueness.
     */
    @Override
    public String toString() {
        return "Killer Instinct";
    }

    @Override
    public void drawMe(Graphics page, Point2D me) {
        int x, y;
        x = (int) me.getX() - Clobber.MAX_BOT_GIRTH / 2 - 1;
        y = (int) me.getY() - Clobber.MAX_BOT_GIRTH / 2 - 1;
        page.setColor(mycolor);
        page.fillOval(x, y, Clobber.MAX_BOT_GIRTH, Clobber.MAX_BOT_GIRTH);

        int space = 4;
        page.setColor(Color.WHITE);
        page.fillOval(x + space, y + space, Clobber.MAX_BOT_GIRTH - space * 2, Clobber.MAX_BOT_GIRTH - space * 2);
//		x = (int) me.getX();
//		y = (int) me.getY();
//		int xPlus = (int) (me.getX() + bulletDangerVectorDraw.getDirectionComponentInX());
//		int yPlus = (int) (me.getY() + bulletDangerVectorDraw.getDirectionComponentInY());
//		page.drawLine(x, y, xPlus, yPlus);


    }

    private static class MyVector {

        double x;
        double y;

        public MyVector() {
            this.x = 0;
            this.y = 0;
        }

        public MyVector(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getMagnitude() {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        }

        public void setMagnitude(double magnitude) {
            this.normalize();
            this.x *= magnitude;
            this.y *= magnitude;
        }

        public void normalize() {
            double magnitude = this.getMagnitude();
            this.x /= magnitude;
            this.y /= magnitude;
        }

        public void addVector(MyVector vector) {
            this.x += vector.getX();
            this.y += vector.getY();
        }

        /*
         * x = x cos(t) - y sin(t)
         * y = x sin(t) + y cos(t)
         * t = 45
         * cos(45) = 0.7...
         * sin(45) = 0.7...
         */
        public void rotate45() {
            double cosSin45 = 0.7071067811865d;
            this.x = this.x * cosSin45 - this.y * cosSin45;
            this.y = this.x * cosSin45 + this.y * cosSin45;
        }

        public void rotate90() {
            double temp = this.x;
            this.x = -this.y;
            this.y = temp;
        }

        public void rotate180() {
            this.x = -this.x;
            this.y = -this.y;
        }

        public void rotate270() {
            double temp = this.x;
            this.x = this.y;
            this.y = -temp;
        }

        public static MyVector addVectors(List<MyVector> vectors) {
            double x = 0;
            double y = 0;

            for (MyVector vector : vectors) {
                x += vector.getX();
                y += vector.getY();
            }
            return new MyVector(x, y);
        }

        public static MyVector getVectorFromPoints(Point2D origin, Point2D other) {
            double x = other.getX() - origin.getX();
            double y = other.getY() - origin.getY();
            return new MyVector(x, y);
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}
