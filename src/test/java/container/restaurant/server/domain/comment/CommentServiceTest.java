package container.restaurant.server.domain.comment;

import container.restaurant.server.domain.user.AuthProvider;
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

        // 3명의 유저
        users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            users.add(User.builder()
                    .authId("authId" + i)
                    .authProvider(AuthProvider.KAKAO)
                    .email("me" + i + "@test.com")
                    .profile(image)
                    .nickname("TestNickname" + i)
                    .build());
        }
        users = userRepository.saveAll(users);

        // 5개의 식당
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

        // 15 개의 피드
        feeds = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            feeds.add(feedRepository.save(Feed.builder()
                    .owner(users.get(i % 3))
                    .restaurant(restaurants.get(i % 5))
                    // 식당과 서로소가 되도록 1~4를 사용
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
    @DisplayName("댓글 작성")
    void createComment() {
        int orgCommentCount = feeds.get(0).getReplyCount();
        CommentCreateDto commentCreateDto = new CommentCreateDto("test");

        Long newId = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());
        CommentInfoDto dto = commentService.get(newId);

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(users.get(0).getId());
        assertThat(feedRepository.findById(feeds.get(0).getId()).orElseThrow().getReplyCount())
                .isEqualTo(orgCommentCount + 1);
    }

    @Test
    @DisplayName("대댓글 작성")
    void createReplyComment() {
        int orgCommentCount = feeds.get(0).getReplyCount();
        CommentCreateDto commentCreateDto = new CommentCreateDto("test", comments.get(0).getId());

        Long newId = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());
        CommentInfoDto dto = commentService.get(newId);

        assertThat(dto.getContent()).isEqualTo("test");
        assertThat(dto.getOwnerId()).isEqualTo(users.get(0).getId());
        assertThat(feedRepository.findById(feeds.get(0).getId()).orElseThrow().getReplyCount())
                .isEqualTo(orgCommentCount + 1);
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment(){
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("수정");
        commentService.update(comments.get(0).getId(), commentUpdateDto, users.get(0).getId());
        CommentInfoDto commentInfoDto = commentService.get(comments.get(0).getId());

        assertThat(commentUpdateDto.getContent()).isEqualTo(commentInfoDto.getContent());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment(){
        // given 대댓글 작성
        CommentCreateDto commentCreateDto = new CommentCreateDto("test", comments.get(0).getId());

        Long newId = commentService.createComment(commentCreateDto, feeds.get(0).getId(), users.get(0).getId());
        CommentInfoDto dto = commentService.get(newId);

        assertThat(commentRepository.findAll().size()).isEqualTo(11);
        long count = commentRepository.count();

        // when-1 대댓글이 있는 (상위)댓글 삭제
        commentService.deleteById(
                comments.get(0).getId(),
                users.get(0).getId()
        );
        // then-1 총 댓글 개수에 영향을 주지 않고, 상위 댓글의 isDeleted 값이 true 로 변경
        assertThat(commentRepository.count()).isEqualTo(count);
        assertThat(commentRepository.findById(comments.get(0).getId())
                .orElseThrow(()->new ResourceNotFoundException("없음")).isDeleted() )
                .isEqualTo(true);

        // when-2 대댓글 삭제
        commentService.deleteById(
                commentRepository.findById(dto.getId())
                        .orElseThrow(()-> new ResourceNotFoundException("대댓글 없음")).getId(),
                users.get(0).getId()
        );
        // then-2-1 : 상위 댓글까지 없어짐 -> 총 댓글 개수 -2
        assertThat(commentRepository.count()).isEqualTo(count - 2);
        // then-2-2 : 상위 댓글이 없어짐(상위 댓글을 찾으면 예외 반환)
        Assertions.assertThatThrownBy(()->
            commentRepository.findById(comments.get(0).getId())
                    .orElseThrow(()->new ResourceNotFoundException("댓글 없음"))
        ).isInstanceOf(ResourceNotFoundException.class);
    }

}
