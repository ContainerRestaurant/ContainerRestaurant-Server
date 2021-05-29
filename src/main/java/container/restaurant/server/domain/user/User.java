package container.restaurant.server.domain.user;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@Entity(name = "TB_USERS")
public class User extends BaseCreatedTimeEntity {

    @Email(message = "잘못된 이메일 형식입니다.")
    @Column(nullable = false, unique = true)
    private String email;

    @NicknameConstraint
    @Column(unique = true)
    private String nickname;

    @URL(message = "프로필의 URL 형식이 잘못되었습니다.")
    private String profile;

    private String greeting;

    private Integer level;

    private Integer levelFeedCount;

    private Integer feedCount;

    private Integer scrapCount;

    private Integer bookmarkedCount;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean banned;

    private String pushToken;
    @Builder
    protected User(String email, String profile, String nickname) {
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.greeting = null;
        this.level = 1;
        this.levelFeedCount = 0;
        this.feedCount = 0;
        this.scrapCount = 0;
        this.bookmarkedCount = 0;
        this.role = Role.USER;
        this.banned = false;
        this.pushToken = "";

    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRoleKey() {
        return this.role.getKey();
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
