package radarlivre.com.radarlivreandroid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {

    private static Repository instance = null;

    private Map<Class<? extends AbsObject>, Map<Long, AbsObject>> objectMap = new HashMap<>();
    private List<OnObjectChangedListener> listeners;

    private Repository() { }

    public static synchronized Repository getInstance() {
        if(instance == null)
            instance = new Repository();
        return instance;
    }

    public synchronized void addListener(OnObjectChangedListener listener) {
        this.listeners.add(listener);
    }

    private <T extends AbsObject> void callListenerForCreate(Class<T> type, T object) {
        for(OnObjectChangedListener l: listeners)
            l.onObjectCreated(type, object);
    }

    private <T extends AbsObject> void callListenerForUpdate(Class<T> type, T object) {
        for(OnObjectChangedListener l: listeners)
            l.onObjectUpdated(type, object);
    }

    private <T extends AbsObject> void callListenerForRemove(Class<T> type, T object) {
        for(OnObjectChangedListener l: listeners)
            l.onObjectRemoved(type, object);
    }

    private boolean hasEntry(Class<? extends AbsObject> type) {
        return objectMap.containsKey(type);
    }

    private <T extends AbsObject> Map getOrCreateEntry(Class<T> type) {
        if(!hasEntry(type)) {
            objectMap.put(type, new HashMap<Long, AbsObject>());
        }
        return objectMap.get(type);
    }

    public synchronized <T extends AbsObject> T get(Class<T> type, Long id) {
        if (hasEntry(type)) {
            Map<Long, T> entry = getOrCreateEntry(type);
            if(entry.containsKey(id)) {
                return entry.get(id);
            }
        }
        return null;
    }

    public synchronized <T extends AbsObject> T get(Class<T> type, T object) {
        return get(type, object.getId());
    }

    public synchronized <T extends AbsObject> Repository remove(Class<T> type, Long id) {
        if (hasEntry(type)) {
            Map<Long, T> entry = getOrCreateEntry(type);
            if(entry.containsKey(id)) {
                // removing
                T object = get(type, id);
                entry.remove(id);
                callListenerForRemove(type, object);
            }
        }
        return this;
    }

    public synchronized <T extends AbsObject> Repository remove(Class<T> type, T object) {
        return remove(type, object.getId());
    }

    public synchronized <T extends AbsObject> Repository save(Class<T> type, T object) {
        Map entry = getOrCreateEntry(object.getClass());
        if(get(type, object) != null) {
            entry.put(object.getId(), object);
            callListenerForUpdate(type, object);
        } else {
            entry.put(object.getId(), object);
            callListenerForCreate(type, object);
        }
        return this;
    }

    public synchronized <T extends AbsObject> Repository save(Class<T> type, List<T> objects, boolean removeOlder) {
        List<T> older = new ArrayList<>(getOrCreateEntry(type).values());

        if(removeOlder)
            for(T o: older)
                if(!objects.contains(o))
                    remove(type, o);
        for(T o: objects)
            save(type, o);
        return this;
    }

    public interface OnObjectChangedListener {
        <T extends AbsObject> void onObjectCreated(Class<T> type, T object);
        <T extends AbsObject> void onObjectUpdated(Class<T> type, T object);
        <T extends AbsObject> void onObjectRemoved(Class<T> type, T object);
    }

}