package clobberbot;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlacement implements PlacementStrategy {

    private Random rand = new Random();
    private static final int TRIES = 1000;

    @Override
    public List<ImmutablePoint2D> place(int numberOfBots, int distanceBetweenBots, int fieldSize) 
    {
        List<ImmutablePoint2D> set = new ArrayList<ImmutablePoint2D>();
        for (int i = 0; i < numberOfBots; i++) {
            addSpot(set, distanceBetweenBots * 2, fieldSize);            
        }
        return set;
    }

    private double getMinDistanceToBot(Point2D point, List<ImmutablePoint2D> set) 
    {
        double mindistance = Double.MAX_VALUE;

        for (ImmutablePoint2D immutablePoint2D : set) 
        {
            double distance = immutablePoint2D.distance(point);
            if (distance < mindistance) {
                mindistance = distance;
            }
        }
        return mindistance;
    }

    private void addSpot(List<ImmutablePoint2D> set, int distanceBetweenBots, int fieldSize) 
    {
        int numtries = 0;
        double mindistance;
        ImmutablePoint2D point2D;
        do {
            numtries++;
            if (numtries > TRIES) 
            {
                throw new IllegalStateException("I Can't find a place to put all the bots");
            }
            point2D = new ImmutablePoint2D(rand.nextInt(fieldSize),
                    rand.nextInt(fieldSize), 0);
            mindistance = getMinDistanceToBot(point2D, set);
        } while (mindistance < distanceBetweenBots);        
        set.add(point2D);
    }

    @Override
    public String toString()
    {
        return "*** Classic Random Placement";
    }
}
