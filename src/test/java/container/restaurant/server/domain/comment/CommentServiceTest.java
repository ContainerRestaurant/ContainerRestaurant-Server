package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.user.OAuth2Registration;
import container.restaurant.server.domain.user.OAuth2Identifier;
import container.restaurant.server.exception.ResourceNotFoundException;
import container.restaurant.server.domain.feed.Category;
import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.feed.FeedRepository;
import container.restaurant.server.domain.feed.picture.Image;
import container.restaurant.server.domain.feed.picture.ImageRepository;
import container.restaurant.server.domain.restaurant.Restaurant;
import container.restaurant.server.domain.restaurant.RestaurantRepository;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserRepository;
import container.restaurant.server.web.dto.comment.CommentCreateDto;
import container.restaurant.server.web.dto.comment.CommentInfoDto;
import container.restaurant.server.web.dto.comment.CommentUpdateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentServiceTest {
    private static final Category[] CATEGORY_ARR = {
            Category.KOREAN, Category.JAPANESE, Category.FAST_FOOD
    };

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentService commentService;

    protected List<User> users;
    protected List<Restaurant> restaurants;
    protected List<Feed> feeds;
    protected List<Comment> comments;

    @BeforeEach
    void beforeEach() {

        Image image = imageRepository.save(Image.builder()
                .url("image_path_url")
                .build());

        // 3?????? ??????
        users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            users.add(User.builder()
                    .identifier(OAuth2Identifier.of("authId" + i, OAuth2Registration.KAKAO))
                    .email("me" + i + "@test.com")
                    .profile(image)
                    .nickname("TestNickname" + i)
                    .build());
        }
        users = userRepository.saveAll(users);

        // 5?????? ??????
        restaurants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            restaurants.add(restaurantRepository.save(Restaurant.builder()
                    .name("restaurant")
                    .addr("address")
                    .lat(1f)
                    .lon(1f)
                    .thumbnail(image)
                    .build()));
        }
        restaurants = restaurantRepository.saveAll(restaurants);

        // 15 ?????? ??????
        feeds = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            feeds.add(feedRepository.save(Feed.builder()
                    .owner(users.get(i % 3))
                    .restaurant(restaurants.get(i % 5))
                    // ????????? ???????????? ????????? 1~4??? ??????
                    .difficulty(i % 4 + 1)
                    .content("Feed Content" + i)
                    .category(CATEGORY_ARR[i % 3])
                    .build()));
        }

        comments = new ArrayList<>();
        for(int i=0; i<10; i++){
            comments.add(commentRepository.save(Comment.builder()
                .owner(users.get(i%3))
                .feed(feeds.get(i%3))
                .content("test")
                .build()
            ));
        }
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        feedRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("?????? ??????")
    void createComment() {
        int orgCommentCount = feeds.get(0).getReplyCount();
        CommentCreateDto commentCreateDto = new CommentCreateDto("test");

        CommentInfoDto dto = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(users.get(0).getId());
        assertThat(feedRepository.findById(feeds.get(0).getId()).orElseThrow().getReplyCount())
                .isEqualTo(orgCommentCount + 1);
    }

    @Test
    @DisplayName("????????? ??????")
    void createReplyComment() {
        int orgCommentCount = feeds.get(0).getReplyCount();
        CommentCreateDto commentCreateDto = new CommentCreateDto("test", comments.get(0).getId());

        CommentInfoDto dto = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(users.get(0).getId());
        assertThat(feedRepository.findById(feeds.get(0).getId()).orElseThrow().getReplyCount())
                .isEqualTo(orgCommentCount + 1);
    }

    @Test
    @DisplayName("?????? ??????")
    void updateComment(){
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("??????");
        CommentInfoDto commentInfoDto = commentService.update(comments.get(0).getId(), commentUpdateDto, users.get(0).getId());

        assertThat(commentUpdateDto.getContent()).isEqualTo(commentInfoDto.getContent());
    }

    @Test
    @DisplayName("?????? ??????")
    void deleteComment(){
        // given ????????? ??????
        CommentCreateDto commentCreateDto = new CommentCreateDto("test", comments.get(0).getId());
        CommentInfoDto dto = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());
        assertThat(commentRepository.findAll().size()).isEqualTo(11);
        long count = commentRepository.count();

        // when-1 ???????????? ?????? (??????)?????? ??????
        commentService.deleteById(
                comments.get(0).getId(),
                users.get(0).getId()
        );
        // then-1 ??? ?????? ????????? ????????? ?????? ??????, ?????? ????????? isDeleted ?????? true ??? ??????
        assertThat(commentRepository.count()).isEqualTo(count);
        assertThat(commentRepository.findById(comments.get(0).getId())
                .orElseThrow(()->new ResourceNotFoundException("??????")).getIsDeleted() )
                .isEqualTo(true);

        // when-2 ????????? ??????
        commentService.deleteById(
                commentRepository.findById(dto.getId())
                        .orElseThrow(()-> new ResourceNotFoundException("????????? ??????")).getId(),
                users.get(0).getId()
        );
        // then-2-1 : ?????? ???????????? ????????? -> ??? ?????? ?????? -2
        assertThat(commentRepository.count()).isEqualTo(count - 2);
        // then-2-2 : ?????? ????????? ?????????(?????? ????????? ????????? ?????? ??????)
        Assertions.assertThatThrownBy(()->
            commentRepository.findById(comments.get(0).getId())
                    .orElseThrow(()->new ResourceNotFoundException("?????? ??????"))
        ).isInstanceOf(ResourceNotFoundException.class);
    }

}
