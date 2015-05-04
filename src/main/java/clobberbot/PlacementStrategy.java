package clobberbot;


import java.util.List;

public interface PlacementStrategy {
    List<ImmutablePoint2D> place(int numberOfBots, int distanceBetweenBots, 
            int fieldSize);
}
