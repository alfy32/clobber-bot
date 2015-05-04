
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueensAltPlacementTest
{

    /**
     * Test of place method, of class QueensAltPlacement.
     */
    @Test
    public void place()
    {
        int distanceBetweenBots = Clobber.MIN_START_DISTANCE / 2;
        int fieldSize = Clobber._height;
        QueensAltPlacement instance = new QueensAltPlacement();

        for (int i = 1; i < 20; i++)
        {
            List result = instance.place(i, distanceBetweenBots, fieldSize);
            assertTrue(String.format("There are bots that are too close together. %s", result),
                    withinMinDistance(distanceBetweenBots, result));
            assertFalse(String.format("There are bots that are out of bounds. %s", result),
                    isOutOfBounds(fieldSize, result));
        }
    }

    /**
     * Test of place method, of class QueensAltPlacement.
     */
    @Test
    public void methodOneSuccess()
    {
        QueensAltPlacement instance = new QueensAltPlacement();

        for (int i = 1; i < 20; i++)
        {
            if (!(i % 6 != 2) || i % 2 != 0)
                continue;
            List result = instance.methodOne(i);
            assertFalse(String.format("There are bots on the same row. %s", result),
                    hasDuplicateRows(result));
        }
    }

    /**
     * Test of place method, of class QueensAltPlacement.
     */
    @Test
    public void methodTwoSuccess()
    {
        QueensAltPlacement instance = new QueensAltPlacement();

        for (int i = 1; i < 20; i++)
        {
            if (i % 6 != 2 || i % 2 != 0)
                continue;
            List result = instance.methodTwo(i);
            assertFalse(String.format("There are bots on the same row. %s", result),
                    hasDuplicateRows(result));
        }
    }

    /**
     * Test of place method, of class QueensAltPlacement.
     */
    @Test(expected = IllegalArgumentException.class)
    public void methodOneBadArgument()
    {
        QueensAltPlacement instance = new QueensAltPlacement();
        List result = instance.methodOne(5);
    }

    /**
     * Test of place method, of class QueensAltPlacement.
     */
    @Test(expected = IllegalArgumentException.class)
    public void methodTwoBadArgument()
    {
        QueensAltPlacement instance = new QueensAltPlacement();
        List result = instance.methodTwo(5);
    }

    private boolean withinMinDistance(int distanceBetweenBots, List<ImmutablePoint2D> columns)
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

    private boolean isOutOfBounds(int fieldSize, List<ImmutablePoint2D> columns)
    {
        for (ImmutablePoint2D immutablePoint2D : columns)
        {
            if (immutablePoint2D.x < 0 || immutablePoint2D.x > fieldSize
                    || immutablePoint2D.y < 0 || immutablePoint2D.y > fieldSize)
            {
                return true;
            }
        }
        return false;
    }

    private boolean hasDuplicateRows(List<Integer> columns)
    {
        for (int i = 0; i < columns.size(); i++)
        {
            int tmp1 = columns.get(i);
            for (int j = i + 1; j < columns.size(); j++)
            {
                int tmp2 = columns.get(j);
                if (tmp1 == tmp2)
                {
                    return true;
                }
            }
        }
        return false;
    }
}