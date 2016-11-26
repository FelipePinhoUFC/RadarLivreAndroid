package radarlivre.com.radarlivreandroid.model;

/**
 * Created by felipe on 01/12/15.
 */
public class FlightInfo extends AbsObject {

    private Airline airline;
    private Flight flight;
    private double latitude;
    private double longitude;
    private double altitude;
    private double verticalVelocity;
    private double horizontalVelocity;
    private double groundTrackHeading;
    private long timestamp;

    public FlightInfo() {
    }

    public FlightInfo(int id, Airline airline, Flight flight, double latitude, double longitude, double altitude, double verticalVelocity, double horizontalVelocity, double groundTrackHeading, long timestamp) {
        super(id, false);
        this.airline = airline;
        this.flight = flight;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.verticalVelocity = verticalVelocity;
        this.horizontalVelocity = horizontalVelocity;
        this.groundTrackHeading = groundTrackHeading;
        this.timestamp = timestamp;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
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

    public double getVerticalVelocity() {
        return verticalVelocity;
    }

    public void setVerticalVelocity(double verticalVelocity) {
        this.verticalVelocity = verticalVelocity;
    }

    public double getHorizontalVelocity() {
        return horizontalVelocity;
    }

    public void setHorizontalVelocity(double horizontalVelocity) {
        this.horizontalVelocity = horizontalVelocity;
    }

    public double getGroundTrackHeading() {
        return groundTrackHeading;
    }

    public void setGroundTrackHeading(double groundTrackHeading) {
        this.groundTrackHeading = groundTrackHeading;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}