package radarlivre.com.radarlivreandroid.model;

/**
 * Created by felipe on 19/08/16.
 */
public class Flight {
    private int id;
    private String code;

    public Flight() {
    }

    public Flight(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
