
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DangerVectorTesting {
    private static final int SIZE = 500;
    private static final boolean printOutList = false;

    public static void main(String[] args) {

        double[] magn = new double[SIZE];
        double[] dirInRads = new double[SIZE];

        Random rand = new Random();
        for (int i = 0; i < SIZE; i++) {
            magn[i] = rand.nextDouble() * 100;
            dirInRads[i] = rand.nextDouble() * Math.PI * 2;
        }

        List<DangerVector> dvList = new ArrayList<DangerVector>();

        /////////////////////////////////////////////
        Long start = System.currentTimeMillis();

        for (int i = 0; i < magn.length; i++) {
            DangerVector dv = new DangerVector(magn[i], dirInRads[i]);
            dvList.add(dv);
        }
        DangerVector resultant = DangerVectorUtils.addDangerVectors(dvList);

        Long end = System.currentTimeMillis();
        /////////////////////////////////////////////

        if (printOutList) {
            int k = 0;
            for (DangerVector dv : dvList) {
                System.out.println("dangerVector " + k + ":\n" + dv.toString());
                k++;
            }
        }
        System.out.println("Resultant:\n" + resultant.toString());
        System.out.println("Start [millisecs]: " + start);
        System.out.println("End [millisecs]: " + end);
        System.out.println("Time [millisecs]: " + (end - start));

    }
}
