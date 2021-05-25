package container.restaurant.server.web.dto.feed;

import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 단일 피드에 대한 상세 정보를 응답하기 위한 DTO
 */
@Getter
public class FeedDetailDto extends RepresentationModel<FeedDetailDto> {

    private final Long id;
    private final Long ownerId;
    private final Long restaurantId;

    private final String ownerNickname;
    private final String restaurantName;
    private final Category category;
    private final String thumbnailUrl;
    private final String content;
    private final Boolean welcome;
    private final Integer difficulty;
    private final Integer likeCount;
    private final Integer scrapCount;
    private final Integer replyCount;

    private final List<FeedMenuDto> mainMenu;
    private final List<FeedMenuDto> subMenu;

    private final Boolean isLike;
    private final Boolean isScraped;

    public static FeedDetailDto from(Feed feed, Boolean isLike, Boolean isScraped) {
        return new FeedDetailDto(feed, isLike, isScraped);
    }

    private FeedDetailDto(Feed feed, Boolean isLike, Boolean isScraped) {
        this.id = feed.getId();
        this.ownerId = feed.getOwner().getId();
        this.restaurantId = feed.getRestaurant().getId();

        this.ownerNickname = feed.getOwner().getNickname();
        this.restaurantName = feed.getRestaurant().getName();
        this.category = feed.getCategory();
        this.thumbnailUrl = feed.getThumbnailUrl();
        this.content = feed.getContent();
        this.welcome = feed.getWelcome();
        this.difficulty = feed.getDifficulty();
        this.likeCount = feed.getLikeCount();
        this.scrapCount = feed.getScrapCount();
        this.replyCount = feed.getReplyCount();

        this.mainMenu = new ArrayList<>();
        this.subMenu = new ArrayList<>();
        feed.getContainerList().forEach(container -> {
            if (container.getMenu().getIsMain())
                mainMenu.add(FeedMenuDto.from(container));
            else
                subMenu.add(FeedMenuDto.from(container));
        });

        this.isLike = isLike;
        this.isScraped = isScraped;
    }

}
