
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueensPlacementTest
{
    /**
     * Test of place method, of class QueensAltPlacement.
     */
    @Test
    public void place()
    {
        System.out.println("place");
        int distanceBetweenBots = Clobber.MIN_START_DISTANCE / 2;
        int fieldSize = Clobber._height;
        QueensPlacement instance = new QueensPlacement();

        for (int i = 1; i < 20; i++)
        {
            List result = instance.place(i, distanceBetweenBots, fieldSize);
            assertTrue(String.format("There are bots that are too close together. %s", result), 
                    checkMinDistance(distanceBetweenBots, result));
            assertFalse(String.format("There are bots that are out of bounds. %s", result), 
                    checkOutOfBounds(fieldSize, result));
        }
    }

    private boolean checkMinDistance(int distanceBetweenBots, List<ImmutablePoint2D> columns)
    {
        for (ImmutablePoint2D point1 : columns)
        {
            for (ImmutablePoint2D point2 : columns)
            {
                if (!point1.equals(point2) && point1.distance(point2) < distanceBetweenBots)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkOutOfBounds(int fieldSize, List<ImmutablePoint2D> columns)
    {
        for (ImmutablePoint2D immutablePoint2D : columns)
        {
            if (immutablePoint2D.x < 0 || immutablePoint2D.x > fieldSize || 
                    immutablePoint2D.y < 0 || immutablePoint2D.y > fieldSize)
            {
                return true;
            }
        }
        return false;
    }
}