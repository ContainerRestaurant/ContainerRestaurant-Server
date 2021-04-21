package container.restaurant.server.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

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

    @Builder
    protected User(String email, String profile) {
        this.email = email;
        this.nickname = "";
        this.profile = profile;
        this.greeting = null;
        this.level = 1;
        this.experience = 0;
        this.feedCount = 0;
        this.scrapCount = 0;
        this.bookmarkedCount = 0;
        this.role = Role.USER;
        this.banned = false;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
