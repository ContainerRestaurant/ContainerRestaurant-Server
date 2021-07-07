package container.restaurant.server.domain.feed.recommend;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedService;
import container.restaurant.server.web.dto.feed.FeedPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendFeedService {

    private final FeedService feedService;

    private List<Feed> recommendFeeds = List.of();

    private final static int DEFAULT_PAGE_SIZE = 1000;
    private final static Pageable DEFAULT_PAGEABLE = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    private Pageable pageable = null;

    public CollectionModel<FeedPreviewDto> getRecommendFeeds(Long loginId) {
        return CollectionModel.of(recommendFeeds.stream()
                .map(feed -> feedService.createFeedPreviewDto(feed, loginId))
                .collect(Collectors.toList()));
    }

    @PostConstruct
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional(readOnly = true)
    public void updateRecommendFeed() {
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
        recommendFeeds = queue.getList();
    }

    public void setPageSize(int pageSize) {
        pageable = PageRequest.of(0, pageSize);
    }
    private Pageable getPageable() {
        return pageable == null ? DEFAULT_PAGEABLE : pageable;
    }

}
