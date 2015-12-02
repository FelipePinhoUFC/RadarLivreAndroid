package radarlivre.com.radarlivreandroid.model;

/**
 * Created by felipe on 01/12/15.
 */
public class AirplanePosition {
    private int id;
    private double latitude;
    private double longitude;
    private double altitude;

    public AirplanePosition(int id, double latitude, double longitude, double altitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public boolean equals(Object o) {
        return id == ((AirplanePosition)o).getId();
    }
}
