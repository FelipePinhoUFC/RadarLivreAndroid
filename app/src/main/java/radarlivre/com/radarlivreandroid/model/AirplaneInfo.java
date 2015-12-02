package radarlivre.com.radarlivreandroid.model;

/**
 * Created by felipe on 01/12/15.
 */
public class AirplaneInfo {
    private int id_reg;
    private long timestamp;
    private String hex;
    private String icao;
    private String id;
    private double latitude;
    private double longitude;
    private double altitude;
    private String climb;
    private double head;
    private double velocidadegnd;
    private String utf;

    public AirplaneInfo() {}

    public AirplaneInfo(int id_reg, long timestamp, String hex, String icao, String id, double latitude, double longitude, double altitude, String climb, double head, double velocidade, String utf) {
        this.id_reg = id_reg;
        this.timestamp = timestamp;
        this.hex = hex;
        this.icao = icao;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.climb = climb;
        this.head = head;
        this.velocidadegnd = velocidade;
        this.utf = utf;
    }

    public int getId_reg() {
        return id_reg;
    }

    public void setId_reg(int id_reg) {
        this.id_reg = id_reg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getClimb() {
        return climb;
    }

    public void setClimb(String climb) {
        this.climb = climb;
    }

    public double getHead() {
        return head;
    }

    public void setHead(double head) {
        this.head = head;
    }

    public double getVelocidadegnd() {
        return velocidadegnd;
    }

    public void setVelocidadegnd(double velocidadegnd) {
        this.velocidadegnd = velocidadegnd;
    }

    public String getUtf() {
        return utf;
    }

    public void setUtf(String utf) {
        this.utf = utf;
    }

    @Override
    public boolean equals(Object o) {
        return ((AirplaneInfo)o).getId_reg() == id_reg;
    }
}