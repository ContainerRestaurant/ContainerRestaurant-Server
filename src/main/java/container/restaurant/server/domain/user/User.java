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

    private Integer experience;

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
        this.experience = 0;
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

    public User scrapCountUp() {
        this.scrapCount++;
        return this;
    }

    public User scrapCountDown() {
        this.scrapCount--;
        return this;
    }

    public void feedCountUp() {
        this.feedCount++;
    }

    public void feedCountDown() {
        this.feedCount--;
    }
}
