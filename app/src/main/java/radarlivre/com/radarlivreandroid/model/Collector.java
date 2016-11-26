package radarlivre.com.radarlivreandroid.model;

/**
 * Created by felipe on 26/11/16.
 */
public class Collector extends AbsObject {
    private User user;
    private double latitude;
    private double longitude;
    private long timestamp;
    private long timestampData;

    public Collector() {
    }

    public Collector(long id, User user, double latitude, double longitude, long timestamp, long timestampData) {
        super(id, false);
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.timestampData = timestampData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampData() {
        return timestampData;
    }

    public void setTimestampData(long timestampData) {
        this.timestampData = timestampData;
    }
}
