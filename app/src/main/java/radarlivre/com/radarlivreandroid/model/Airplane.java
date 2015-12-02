package radarlivre.com.radarlivreandroid.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felipe on 01/12/15.
 */
public class Airplane {
    private int id_reg;
    private String hex;
    private String icao;
    private String id;
    private String climb;
    private double head;
    private double speed;
    private String utf;

    private long timestamp;

    List<AirplanePosition> route = new ArrayList<>();

    public Airplane(AirplaneInfo info) {
        this(info.getId_reg(), info.getHex(), info.getIcao(), info.getId(), info.getClimb(), info.getHead(), info.getVelocidadegnd(), info.getUtf());
        route.add(new AirplanePosition(info.getId_reg(), info.getLatitude(), info.getLongitude(), info.getAltitude()));

        if(info.getTimestamp() > timestamp) {
            this.timestamp = info.getTimestamp();
            this.head = info.getHead();
        }
    }

    public Airplane(int id_reg, String hex, String icao, String id, String climb, double head, double speed, String utf) {
        this.id_reg = id_reg;
        this.hex = hex;
        this.icao = icao;
        this.id = id;
        this.climb = climb;
        this.head = head;
        this.speed = speed;
        this.utf = utf;

    }

    public void update(AirplaneInfo info) {
        AirplanePosition pos = new AirplanePosition(info.getId_reg(), info.getLatitude(), info.getLongitude(), info.getAltitude());
        if(!route.contains(pos))
            route.add(pos);

        if(info.getTimestamp() > timestamp) {
            this.timestamp = info.getTimestamp();
            this.head = info.getHead();
        }
    }

    public int getId_reg() {
        return id_reg;
    }

    public void setId_reg(int id_reg) {
        this.id_reg = id_reg;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getUtf() {
        return utf;
    }

    public void setUtf(String utf) {
        this.utf = utf;
    }

    public List<AirplanePosition> getRoute() {
        return route;
    }

    public void setRoute(List<AirplanePosition> route) {
        this.route = route;
    }

    public void addPosition(AirplanePosition pos) {
        this.route.add(pos);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public AirplanePosition getLastPosition() {
        if(route.size() > 0)
            return route.get(route.size() - 1);
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return ((Airplane)o).getHex().equals(hex);
    }
}