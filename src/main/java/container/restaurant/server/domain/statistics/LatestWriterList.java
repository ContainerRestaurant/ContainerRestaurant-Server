package container.restaurant.server.domain.statistics;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

class LatestWriterList<T> {

    private List<T> latestWriters;
    private final int recentUserMaxCount;
    private static final int DEFAULT_RECENT_USER_MAX_COUNT = 100;

    public LatestWriterList() {
        this.latestWriters = synchronizedList(new ArrayList<>());
        this.recentUserMaxCount = DEFAULT_RECENT_USER_MAX_COUNT;
    }

    public LatestWriterList(int recentUserMaxCount) {
        this.latestWriters = synchronizedList(new ArrayList<>());
        this.recentUserMaxCount = recentUserMaxCount;
    }

    public List<T> getList() {
        return unmodifiableList(latestWriters);
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
        latestWriters = synchronizedList(list);
    }

}
