package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class RecommendFeedQueue extends PriorityQueue<Feed> {

    private static final int FIXED_SIZE = 12;
    private static final Comparator<Feed> COMPARATOR = Feed.RECOMMEND_COMPARATOR;

    public RecommendFeedQueue() {
        super(FIXED_SIZE, COMPARATOR);
    }

    @Override
    public boolean add(Feed feed) {
        if (this.size() < FIXED_SIZE) {
            return super.add(feed);
        } else if (COMPARATOR.compare(this.peek(), feed) < 0){
            this.poll();
            return this.add(feed);
        }
        return false;
    }

    public List<Feed> getList() {
        List<Feed> sortedList = new ArrayList<>(this);
        sortedList.sort(COMPARATOR.reversed());
        return sortedList;
    }
}
