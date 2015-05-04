
import java.awt.geom.Point2D;
import java.util.List;

public class DangerVectorUtils {
	public static double convertDirectionFromRadiansToDegrees(double directionInRadians) {
		return Math.toDegrees(directionInRadians);
	}

	public static DangerVector addDangerVectors(List<DangerVector> dangerVector) {
		if (dangerVector.isEmpty()) {
			return new DangerVector(0.0, 0.0);
		}
		double sumOfMagnInX = 0.0;
		double sumOfMagnInY = 0.0;
		for (DangerVector dv : dangerVector) {
			sumOfMagnInX += dv.getDangerMagnitudeInX();
			sumOfMagnInY += dv.getDangerMagnitudeInY();
		}
		double resultantDangerMagnitude;
		double resultantDirInRads;

		// Quadrant 1
		if (sumOfMagnInX > 0 && sumOfMagnInY > 0) {
			resultantDangerMagnitude = calculateResultantDangerMagnitude(sumOfMagnInX, sumOfMagnInY);
			resultantDirInRads = Math.atan(sumOfMagnInY / sumOfMagnInX);
		} else if (sumOfMagnInX < 0 && sumOfMagnInY > 0) {
			resultantDangerMagnitude = calculateResultantDangerMagnitude(sumOfMagnInX, sumOfMagnInY);
			resultantDirInRads = Math.PI + Math.atan(sumOfMagnInY / sumOfMagnInX);
		} // Quadrant 3
		else if (sumOfMagnInX < 0 && sumOfMagnInY < 0) {
			resultantDangerMagnitude = calculateResultantDangerMagnitude(sumOfMagnInX, sumOfMagnInY);
			resultantDirInRads = Math.PI + Math.atan(sumOfMagnInY / sumOfMagnInX);
		} // Quadrant 4
		else if (sumOfMagnInX > 0 && sumOfMagnInY < 0) {
			resultantDangerMagnitude = calculateResultantDangerMagnitude(sumOfMagnInX, sumOfMagnInY);
			resultantDirInRads = Math.atan(sumOfMagnInY / sumOfMagnInX) + 6.28318531; // PI*2
		} // on positive side of y-axis
		else if (sumOfMagnInX == 0 && sumOfMagnInY > 0) {
			resultantDangerMagnitude = sumOfMagnInY;
			resultantDirInRads = 1.570796327; // PI/2
		} // on negative side of y-axis
		else if (sumOfMagnInX == 0 && sumOfMagnInY > 0) {
			resultantDangerMagnitude = -sumOfMagnInY;
			resultantDirInRads = 3.0 * Math.PI / 2.0; // 3*PI/2
		} // on positive side of x-axis
		else if (sumOfMagnInX > 0 && sumOfMagnInY == 0) {
			resultantDangerMagnitude = sumOfMagnInX;
			resultantDirInRads = 0.0; // 0*PI
		} // on negative side of x-axis
		else if (sumOfMagnInX < 0 && sumOfMagnInY == 0) {
			resultantDangerMagnitude = -sumOfMagnInX;
			resultantDirInRads = Math.PI; // PI
		} // there was a major issue somewhere but we are going to ignore it
		else {
			return new DangerVector(0.0, 0.0);
		}
		return new DangerVector(resultantDangerMagnitude, resultantDirInRads);
	}

	private static double calculateResultantDangerMagnitude(double sumOfMagnInX, double sumOfMagnInY) {
		double sqSumInX;
		double sqSumInY;
		double resultantDangerMagnitude;
		sqSumInX = sumOfMagnInX * sumOfMagnInX;
		sqSumInY = sumOfMagnInY * sumOfMagnInY;
		resultantDangerMagnitude = Math.sqrt(sqSumInX + sqSumInY);
		return resultantDangerMagnitude;
	}

	public static double getRadianDirection(Point2D origin, Point2D otherPoint) {
		if (origin.equals(otherPoint)) {
			return 0;
		} else if (origin.getX() == otherPoint.getX()) {
			if (origin.getY() > otherPoint.getY()) {
				return Math.PI / 2.0;
			} else {
				return 3.0 * Math.PI / 2.0;
			}
		} else {
			double x = otherPoint.getX() - origin.getX();
			double y = origin.getY() - otherPoint.getY();
			// First Quadrant
			if (x > 0 && y >= 0) {
				return Math.atan2(x, y);
			} else if (x < 0) { // Second or Third Quadrant
				return Math.atan2(x, y) + Math.PI;
			} else { // Fourth Quadrant
				return Math.atan2(x, y) + 2.0 * Math.PI;
			}
		}
	}
}
