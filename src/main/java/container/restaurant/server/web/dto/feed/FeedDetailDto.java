package container.restaurant.server.web.dto.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.utils.ImageUtils;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

/**
 * 단일 피드에 대한 상세 정보를 응답하기 위한 DTO
 */
@Getter
public class FeedDetailDto extends RepresentationModel<FeedDetailDto> {

    private final Long id;
    private final Long ownerId;
    private final Long restaurantId;

    private final String ownerNickname;
    private final String ownerContainerLevel;
    private final String ownerProfile;

    private final String restaurantName;
    private final double latitude;
    private final double longitude;

    private final Category category;
    private final String thumbnailUrl;
    private final String content;
    private final Boolean welcome;
    private final Integer difficulty;
    private final Integer likeCount;
    private final Integer scrapCount;
    private final Integer replyCount;
    private final Boolean isContainerFriendly;

    private final List<FeedMenuDto> mainMenu;
    private final List<FeedMenuDto> subMenu;

    private final Boolean isLike;
    private final Boolean isScraped;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private final LocalDateTime createdDate;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private final LocalDateTime modifiedDate;

    @Builder
    private FeedDetailDto(Feed feed, Boolean isLike, Boolean isScraped) {
        this.id = feed.getId();
        this.ownerId = feed.getOwner().getId();
        this.restaurantId = feed.getRestaurant().getId();

        this.ownerNickname = feed.getOwner().getNickname();
        this.ownerContainerLevel = feed.getOwner().getLevelTitle();
        this.ownerProfile = ImageUtils.getUrlFromImage(feed.getOwner().getProfile());

        this.restaurantName = feed.getRestaurant().getName();
        this.latitude = feed.getRestaurant().getLatitude();
        this.longitude = feed.getRestaurant().getLongitude();

        this.isContainerFriendly = feed.getRestaurant().isContainerFriendly();
        this.category = feed.getCategory();
        this.thumbnailUrl = ImageUtils.getUrlFromImage(feed.getThumbnail());
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

        this.createdDate = feed.getCreatedDate();
        this.modifiedDate = feed.getModifiedDate();
    }

}
