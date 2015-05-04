
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class QueensPlacement implements PlacementStrategy
{

    private static final Logger LOG = Logger.getLogger(QueensPlacement.class.getName());
    private static final int MAXTRIES = 50;
    private Random rand = new Random();
    private int mPreviousRowMoved;
    protected static final int SMALLEST = 4;
    protected int distance;

    @Override
    public List<ImmutablePoint2D> place(int numberOfBots, int distanceBetweenBots, int fieldSize)
    {
        int numberOfColumns = initialize(fieldSize, distanceBetweenBots, numberOfBots);

        List<Integer> columns = stepOne(numberOfColumns);
        columns = stepTwo(columns);

        return convertToPoints(columns);
    }

    private List<Integer> stepOne(int numberOfColumns)
    {
        List<Integer> rows = rowPrep(numberOfColumns);
        List<Integer> columns = new ArrayList<Integer>(numberOfColumns);
        for (int i = 0; i < numberOfColumns; i++)
        {
            for (int j = 0; j < rows.size(); j++)
            {
                if (!hasConflicts(columns, i, rows.get(j)))
                {
                    columns.add(i, rows.get(j));
                    rows.remove(rows.get(j));
                    break;
                }
            }
            if (columns.size() < i + 1)
            {
                columns.add(rows.get(0));
                rows.remove(0);
            }
        }
        return columns;
    }

    private List<Integer> stepTwo(List<Integer> columns)
    {
        List<Integer> newColumns = columns;
        int count = 0;
        mPreviousRowMoved = -1;
        do
        {
            newColumns = performNextMove(newColumns);
            count++;
            if (count > MAXTRIES)
            {
                count = 0;
                newColumns = stepOne(newColumns.size());
            }
        } while (hasConflicts(newColumns));

        return newColumns;
    }

    private boolean hasConflicts(List<Integer> columns, int x, int y)
    {
        for (int i = 0; i < columns.size(); i++)
        {
            if (x != i && (y == columns.get(i) || Math.abs(x - i) == Math.abs(y - columns.get(i))))
                return true;
        }
        return false;
    }

    private boolean hasConflicts(List<Integer> columns)
    {
        for (int i = 0; i < columns.size(); i++)
        {
            if (hasConflicts(columns, i, columns.get(i)))
                return true;
        }
        return false;
    }

    private int numberOfConflicts(List<Integer> columns, int x, int y)
    {
        int counts = 0;
        for (int i = 0; i < columns.size(); i++)
        {
            if (x != i && (y == columns.get(i) || Math.abs(x - i) == Math.abs(y - columns.get(i))))
                counts++;
        }
        return counts;
    }

    public List<Integer> performNextMove(List<Integer> start)
    {
        List<Integer> columns = new ArrayList<Integer>(start);
        // Find the next attacked queen
        for (int i = 0; i < columns.size(); i++)
        {
            int idx = (i + mPreviousRowMoved + 1) % columns.size();
            int curQueen = columns.get(idx);

            int nAttackers = numberOfConflicts(columns, idx, curQueen);
            if (nAttackers > 0)
            {
                // Find the number of attackers for each column in this row
                List<Integer> attackers = new ArrayList<Integer>();
                for (int j = 0; j < columns.size(); j++)
                {
                    if (curQueen == j)
                    {
                        // Avoid moving the queen to the same place
                        attackers.add(j, columns.size());
                    } else
                    {
                        attackers.add(j, numberOfConflicts(columns, idx, j));
                    }
                }

                // Find the minimum number of attackers
                int minAttackers = columns.size();
                List<Integer> possibleLocations = new ArrayList<Integer>();
                for (int j = 0; j < columns.size(); j++)
                {
                    if (attackers.get(j) < minAttackers)
                    {
                        minAttackers = attackers.get(j);
                        possibleLocations = new ArrayList<Integer>();
                        possibleLocations.add(j);
                    } else if (attackers.get(j) == minAttackers)
                    {
                        possibleLocations.add(j);
                    }
                }

                // Make sure we are improving. If not - skip this move
                if (minAttackers > nAttackers)
                {
                    continue;
                }

                // Select a random possible location
                int index = rand.nextInt(possibleLocations.size());
                int selectedColumn = possibleLocations.get(index);

                LOG.fine(String.format("Moving queen in col %s from row %s to row %s", idx, curQueen, selectedColumn));
                columns.set(idx, selectedColumn);

                mPreviousRowMoved = idx;

                break;
            }
        }
        return columns;
    }

    private List<Integer> rowPrep(int numberOfColumns)
    {
        List<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < numberOfColumns; i++)
        {
            rows.add(i);
        }
        Collections.shuffle(rows);
        return rows;
    }

    protected int initialize(int fieldSize, int distanceBetweenBots, int numberOfBots)
    {
        int numberOfColumns = fieldSize / distanceBetweenBots;
        // We want the same space between a wall and the bots so we allow for an extra column
        if (numberOfBots >= numberOfColumns)
            throw new IllegalStateException(String.format("I Can't find a place to put all the bots. Max bots for this distance is %s", numberOfColumns - 1));

        numberOfColumns = numberOfBots < SMALLEST ? SMALLEST : numberOfBots;
        distance = fieldSize / (numberOfColumns + 1);

        return numberOfColumns;
    }

    protected List<ImmutablePoint2D> convertToPoints(List<Integer> columns)
    {
        List<ImmutablePoint2D> spots = new ArrayList<ImmutablePoint2D>();
        for (int i = 0; i < columns.size(); i++)
        {
            spots.add(new ImmutablePoint2D((i + 1) * distance, (columns.get(i) + 1) * distance, 0));
        }
        Collections.shuffle(spots);

        return spots;
    }

    @Override
    public String toString()
    {
        return "*** Queens Placement - random";
    }
}
