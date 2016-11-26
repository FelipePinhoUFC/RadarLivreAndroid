package radarlivre.com.radarlivreandroid.model;

/**
 * Created by felipe on 25/11/16.
 */
public class AbsObject {

    private long id;
    private boolean persistent;

    public AbsObject() {
    }

    public AbsObject(long id, boolean persistent) {
        this.id = id;
        this.persistent = persistent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public boolean equals(Object o) {
        return ((AbsObject)o).getId() == id;
    }
}
