package container.restaurant.server.domain.user;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "TB_USERS")
public class User extends BaseCreatedTimeEntity {

    @Column(nullable = false)
    private String authId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    private String email;

    @NicknameConstraint
    @Column(unique = true)
    private String nickname;

    @OneToOne
    private Image profile;

    private Integer level;

    private Integer levelFeedCount;

    private Integer feedCount;

    private Integer scrapCount;

    private Integer bookmarkedCount;

    private Boolean banned;

    @OneToOne
    private PushToken pushToken;

    @Builder
    protected User(String authId, AuthProvider authProvider, String email,
                   Image profile, String nickname, PushToken pushToken) {
        this.authId = authId;
        this.authProvider = authProvider;
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.level = 0;
        this.levelFeedCount = 0;
        this.feedCount = 0;
        this.scrapCount = 0;
        this.bookmarkedCount = 0;
        this.banned = false;
        this.pushToken = pushToken;
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

    public void levelFeedUp(int count) {
        updateLevelFeed(count);
    }

    public void levelFeedDown(int count) {
        updateLevelFeed(-count);
    }

    private void updateLevelFeed(int count) {
        this.levelFeedCount += count;
        updateLevel();
    }

    private void updateLevel() {
        if (this.levelFeedCount <= 0) {
            this.level = 0;
        } else if (this.levelFeedCount < 5) {
            this.level = 1;
        } else if (this.levelFeedCount < 10) {
            this.level = 2;
        } else if (this.levelFeedCount < 20) {
            this.level = 3;
        } else {
            this.level = 4;
        }
    }
}
