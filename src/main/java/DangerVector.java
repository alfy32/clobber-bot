
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

    private void setDirectionInRadians(double directionInRadians) {
        this.directionInRadians = directionInRadians;
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
        //sb.append(" directionComponentInX: ").append(getDirectionComponentInX()).append("\n");
        //sb.append(" directionComponentInY: ").append(getDirectionComponentInY()).append("\n");
        //sb.append(" dangerMagnitudeInX: ").append(getDangerMagnitudeInX()).append("\n");
        //sb.append(" dangerMagnitudeInY: ").append(getDangerMagnitudeInY()).append("\n");
        return sb.toString();
    }
}
