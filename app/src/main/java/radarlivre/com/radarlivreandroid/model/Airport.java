package radarlivre.com.radarlivreandroid.model;

import android.content.Context;

import radarlivre.com.radarlivreandroid.R;

/**
 * Created by felipe on 26/11/16.
 */
public class Airport extends AbsObject {
    public enum Type {
        SMALL_AIRPORT("small_airport"),
        MEDIUMAIRPORT("medium_airport"),
        LARGE_AIRPORT("large_airport"),
        SEAPLANE_BASE("seaplane_base"),
        HELIPORT("heliport"),
        BALLOONPORT("balloonport"),
        CLOSED("closed");

        private String name;
        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String toString(Context context) {
            switch(name) {
                case "small_airport":
                    return context.getString(R.string.airport_type_sa);
                case "medium_airport":
                    return context.getString(R.string.airport_type_ma);
                case "large_airport":
                    return context.getString(R.string.airport_type_la);
                case "seaplane_base":
                    return context.getString(R.string.airport_type_sb);
                case "heliport":
                    return context.getString(R.string.airport_type_h);
                case "balloonport":
                    return context.getString(R.string.airport_type_b);
                case "closed":
                    return context.getString(R.string.airport_type_c);
                default:
                    return name;
            }
        }

    };

    private String code;
    private String name;
    private String country;
    private String state;
    private String city;
    private double latitude;
    private double longitude;
    private Type type;

    public Airport() {
    }

    public Airport(long id, String code, String name, String country, String state, String city, double latitude, double longitude, Type type) {
        super(id, false);
        this.code = code;
        this.name = name;
        this.country = country;
        this.state = state;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
