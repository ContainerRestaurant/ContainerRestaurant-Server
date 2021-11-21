package container.restaurant.server.domain.user;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import container.restaurant.server.utils.ImageUtils;
import container.restaurant.server.web.dto.feed.LevelUpDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Arrays;
import java.util.Objects;

import static container.restaurant.server.domain.user.ContainerLevel.*;

@Getter
@NoArgsConstructor
@Entity(name = "TB_USERS")
public class User extends BaseCreatedTimeEntity {

    @Embedded
    private OAuth2Identifier identifier;

    private String email;

    @NicknameConstraint
    @Column(unique = true)
    private String nickname;

    @OneToOne
    private Image profile;

    @Enumerated(EnumType.STRING)
    private ContainerLevel containerLevel;

    private Integer levelFeedCount;

    private Integer feedCount;

    private Integer scrapCount;

    private Integer bookmarkedCount;

    private Boolean banned;

    @OneToOne
    private PushToken pushToken;

    @Builder
    protected User(OAuth2Identifier identifier, String email,
                   Image profile, String nickname, PushToken pushToken) {
        this.identifier = identifier;
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.containerLevel = LEVEL_1;
        this.levelFeedCount = 0;
        this.feedCount = 0;
        this.scrapCount = 0;
        this.bookmarkedCount = 0;
        this.banned = false;
        this.pushToken = pushToken;
    }

    public String getLevelTitle() {
        return this.containerLevel.getTitle();
    }

    public OAuth2Registration getRegistration() {
        return this.identifier.getRegistration();
    }

    public String getSubject() {
        return this.identifier.getSubject();
    }

    public String getProfileUrl() {
        return profile != null ? ImageUtils.getUrlFromImage(profile) : null;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfile(Image profile) {
        this.profile = profile;
    }

    public void setPushToken(PushToken pushToken) {
        this.pushToken = pushToken;
    }

    public void scrapCountUp() {
        this.scrapCount++;
    }

    public void scrapCountDown() {
        this.scrapCount--;
    }

    public void feedCountUp() {
        this.feedCount++;
    }

    public void feedCountDown() {
        this.feedCount--;
    }

    public void bookmarkedCountUp() {
        this.bookmarkedCount++;
    }

    public void bookmarkedCountDown() {
        this.bookmarkedCount--;
    }

    public LevelUpDto levelFeedUp(int count) {
        return updateLevelFeed(count);
    }

    public void levelFeedDown(int count) {
        updateLevelFeed(-count);
    }

    private LevelUpDto updateLevelFeed(int count) {
        this.levelFeedCount += count;
        return updateLevel();
    }

    private LevelUpDto updateLevel() {
        for (ContainerLevel level : values()) {
            if (this.levelFeedCount > level.getNeedCount()) {
                this.containerLevel = level;
                break;
            } else if (Objects.equals(this.levelFeedCount, level.getNeedCount())) {
                this.containerLevel = level;
                return LevelUpDto.to(level);
            }
        }
        return null;
    }
}
