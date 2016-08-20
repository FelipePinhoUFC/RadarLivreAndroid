package radarlivre.com.radarlivreandroid.model;

/**
 * Created by felipe on 01/12/15.
 */
public class Airline {

    private int id;
    private String name;
    private String icao;
    private String country;

    public Airline() {
    }

    public Airline(int id, String name, String icao, String country) {
        this.id = id;
        this.name = name;
        this.icao = icao;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
