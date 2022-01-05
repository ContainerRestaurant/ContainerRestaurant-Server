package container.restaurant.server.domain.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class LatestWriterList<T> {

    private List<T> latestWriters;
    private final int recentUserMaxCount;
    private static final int DEFAULT_RECENT_USER_MAX_COUNT = 100;

    public LatestWriterList() {
        this.latestWriters = toList(new ArrayList<>());
        this.recentUserMaxCount = DEFAULT_RECENT_USER_MAX_COUNT;
    }

    public LatestWriterList(int recentUserMaxCount) {
        this.latestWriters = toList(new ArrayList<>());
        this.recentUserMaxCount = recentUserMaxCount;
    }

    private List<T> toList(List<T> list) {
        return Collections.synchronizedList(list);
    }

    public List<T> getList() {
        return Collections.unmodifiableList(latestWriters);
    }

    public boolean add(T item) {
        if (!latestWriters.remove(item) && latestWriters.size() > recentUserMaxCount) {
            latestWriters.remove(0);
        }
        return latestWriters.add(item);
    }

    public boolean update(T item) {
        int index = latestWriters.indexOf(item);
        if (index == -1) {
            return false;
        }

        latestWriters.remove(index);
        latestWriters.add(index, item);
        return true;
    }

    public void remove(T item) {
        latestWriters.remove(item);
    }

    public void replaceAll(List<T> list) {
        latestWriters = toList(list);
    }

}
