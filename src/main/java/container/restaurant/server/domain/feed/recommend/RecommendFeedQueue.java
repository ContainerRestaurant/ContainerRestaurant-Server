package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

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
        } else if (COMPARATOR.compare(this.peek(), feed) < 0) {
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

    public <T extends Collection<RecommendFeed>> T recommendFeedsTo(
            Supplier<T> supplier, BiConsumer<T, RecommendFeed> accumulator) {

        T ret = supplier.get();

        List<Feed> sortedList = new ArrayList<>(this);
        sortedList.sort(COMPARATOR.reversed());

        for (Feed feed : sortedList)
            accumulator.accept(ret, new RecommendFeed(feed));

        return ret;
    }
}
