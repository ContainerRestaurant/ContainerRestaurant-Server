package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequiredArgsConstructor
@Component
public class RecommendFeedUpdateJob implements Job {

    public final static JobKey JOB_KEY = JobKey.jobKey("detail", "feed_recommend");
    public final static TriggerKey TRIGGER_KEY = TriggerKey.triggerKey("trigger", "feed_recommend");

    private final static int DEFAULT_PAGE_SIZE = 1000;
    private final static Pageable DEFAULT_PAGEABLE = PageRequest.of(0, DEFAULT_PAGE_SIZE);

    private final FeedService feedService;
    private final RecommendFeedService recommendFeedService;

    private Pageable pageable = null;

    @Override
    public void execute(JobExecutionContext context) {
        RecommendFeedQueue queue = new RecommendFeedQueue();
        LocalDateTime to = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime from = to.minusWeeks(1);
        to = to.minusNanos(1);

        Pageable p = getPageable();
        Page<Feed> page = feedService.findForUpdatingRecommend(from, to, p);
        while (page.hasContent()) {
            queue.addAll(page.getContent());
            p = p.next();
            page = feedService.findForUpdatingRecommend(from, to, p);
        }

        recommendFeedService.updateRecommendFeed(queue.getList());
    }


    private Pageable getPageable() {
        return pageable == null ? DEFAULT_PAGEABLE : pageable;
    }

    public void setPageSize(int pageSize) {
        pageable = PageRequest.of(0, pageSize);
    }

}
