
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

public class QueensAltPlacement extends QueensPlacement implements PlacementStrategy
{

    /**
     * If n is even and n != 6k + 2, then place queens at 
     *  (i, 2i) and 
     *  (n/2 + i, 2i - 1) 
     * for i = 1,2,...,n/2.
     *
     * If n is even and n != 6k, then place queens at 
     *  (i, 1 + (2i + n/2 - 3 (mod n))) and 
     *  (n + 1 - i, n - (2i + n/2 - 3 (mod n))) 
     * for i = 1,2,...,n/2.
     *
     * If n is odd, then use one of the patterns above for (n - 1) and add a
     * queen at (n, n).
     */
    @Override
    public List<ImmutablePoint2D> place(int numberOfBots, int distanceBetweenBots, int fieldSize)
    {
        int numberOfColumns = initialize(fieldSize, distanceBetweenBots, numberOfBots);
        int tmpNumberOfColumns = numberOfColumns % 2 == 0 ? numberOfColumns : numberOfColumns - 1;
        List<Integer> columns;
        
        if (tmpNumberOfColumns % 6 != 2)
            columns = methodOne(tmpNumberOfColumns);
        else
            columns = methodTwo(tmpNumberOfColumns);
        
        if (numberOfColumns % 2 != 0)
            columns.add(numberOfColumns - 1, numberOfColumns - 1);
        
        return convertToPoints(columns);
    }
    
    protected List<Integer> methodOne(int tmpNumberOfColumns)
    {
        Preconditions.checkArgument(tmpNumberOfColumns % 6 != 2 && tmpNumberOfColumns % 2 == 0);
        List<Integer> columns = new ArrayList<Integer>();
        for (int i = 0; i < tmpNumberOfColumns; i++)
        {
            columns.add(-1);
        }
        
        for (int i = 1; i <= tmpNumberOfColumns / 2; i++)
        {
            columns.set((i) - 1, (2 * i) - 1);
            columns.set((tmpNumberOfColumns / 2 + i) - 1, (2 * i - 1) - 1);
        }
        return columns;
    }
    
    protected List<Integer> methodTwo(int tmpNumberOfColumns)
    {
        Preconditions.checkArgument(tmpNumberOfColumns % 6 == 2 && tmpNumberOfColumns % 2 == 0);
        List<Integer> columns = new ArrayList<Integer>();
        for (int i = 0; i < tmpNumberOfColumns; i++)
        {
            columns.add(-1);
        }
        
        for (int i = 1; i <= tmpNumberOfColumns / 2; i++)
        {
            columns.set((i) - 1, ((2 * i + tmpNumberOfColumns / 2 - 3) % tmpNumberOfColumns));
            columns.set((tmpNumberOfColumns + 1 - i) - 1, (tmpNumberOfColumns - ((2 * i + tmpNumberOfColumns / 2 - 3) % tmpNumberOfColumns)) - 1);
        }
        return columns;
    }

    @Override
    public String toString()
    {
        return "*** Queens Placement - equation";
    }
}
