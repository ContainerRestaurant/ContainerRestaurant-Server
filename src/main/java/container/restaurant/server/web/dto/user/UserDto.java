package container.restaurant.server.web.dto.user;

import container.restaurant.server.domain.feed.picture.ImageService;
import container.restaurant.server.domain.push.PushToken;
import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.validator.NicknameConstraint;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface UserDto {

    @RequiredArgsConstructor
    @EqualsAndHashCode
    @Getter
    class ToRequestToken {

        @NotNull
        private final OAuth2Registration provider;

        @NotEmpty
        private final String accessToken;

    }

    @RequiredArgsConstructor
    @Getter
    class Token {
        private final Long id;
        private final String token;
    }

    @Getter
    class Info extends RepresentationModel<Info> {

        private final Long id;
        private final String email;
        private final String nickname;
        private final String profile;
        private final String levelTitle;
        private final Integer feedCount;
        private final Integer scrapCount;

        private final Integer bookmarkedCount;

        public static UserDto.Info from(User user) {
            return new UserDto.Info(user);
        }
        protected Info(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.profile = ImageService.getUrlFromImage(user.getProfile());
            this.levelTitle = user.getLevelTitle();
            this.feedCount = user.getFeedCount();
            this.scrapCount = user.getScrapCount();
            this.bookmarkedCount = user.getBookmarkedCount();
        }

    }
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    class Update {


        @NicknameConstraint
        private String nickname;

        private Long profileId;

        private PushToken pushToken;

    }
    @Getter
    class NicknameExists extends RepresentationModel<NicknameExists> {

        private final String nickname;

        private final Boolean exists;

        protected NicknameExists(String nickname, Boolean exists) {
            this.nickname = nickname;
            this.exists = exists;
        }
        public static NicknameExists of(String nickname, Boolean exists) {
            return new NicknameExists(nickname, exists);
        }

    }
    @Getter
    @AllArgsConstructor
    class TokenLogin {

        private final String accessToken;

        private final OAuth2Registration provider;

    }
}
