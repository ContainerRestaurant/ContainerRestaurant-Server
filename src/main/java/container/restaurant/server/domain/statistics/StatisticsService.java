package container.restaurant.server.domain.statistics;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.statistics.StatisticsInfoDto;
import container.restaurant.server.web.dto.statistics.StatisticsUserDto;
import container.restaurant.server.web.linker.FeedLinker;
import container.restaurant.server.web.linker.UserLinker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.hateoas.EntityModel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class StatisticsService implements ApplicationRunner {
    private final int MAX_COUNT = 100;
    private final UserService userService;
    private LinkedList<User> userLinkedList;
    private int todayFeedCount = 0;

    private final UserLinker userLinker;
    private final FeedLinker feedLinker;
    private List<StatisticsUserDto> statisticsUserDtoList;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initToDayFeedWriter();
    }

    public void initToDayFeedWriter() {
        userLinkedList = new LinkedList<>();
        LocalDateTime to = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime from = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        // 오늘 작성한 피드 오래된 순으로 저장 가장 마지막이 최신
        userService.findByToDayFeedWriters(to, from)
                .stream()
                .map(user -> {
                    addRecentUser(user);
                    return null;
                }).collect(Collectors.toList());
    }

    @Transactional
    public StatisticsInfoDto getRecentFeedUsers() {
        List<EntityModel> entityModelList = new ArrayList<>();
        for (int i = 0; i < userLinkedList.size(); i++) {
            User user = userLinkedList.get(i);
            entityModelList
                    .add(EntityModel.of(StatisticsUserDto.from(user))
                            .add(userLinker.getUserById(user.getId()).withSelfRel(),
                                    feedLinker.selectUserFeed(user.getId()).withRel("feeds")));
        }
        return StatisticsInfoDto.from(entityModelList, todayFeedCount);
    }

    public void addRecentUser(User user) {
        // 중복되는 사용자가 있으면, 해당 위치를 지우고 최신으로 추가
        if (userLinkedList.contains(user))
            userLinkedList.remove(user);

        // 리스트 사용자가 n 명이 넘으면 마지막 삭제 LRU 형식
        if (userLinkedList.size() >= MAX_COUNT)
            userLinkedList.removeLast();

        userLinkedList.addFirst(user);
        todayFeedCount++;
    }

    /*
     * 최근 작성한(5명 안에) 사용자가 피드를 삭제 했을 경우
     * 최근 사용자 리스트에서 삭제
     * @param user
     */
    public void removeRecentUser(User user) {
        userLinkedList.remove(user);
        todayFeedCount--;
    }

    /*
     * 최근 30 일간 제일 많은 피드를 작성한 10 명을
     * 매일 9 시 0분에 업데이트 한다.
     */
    @PostConstruct
    @Scheduled(cron = "0 0 0 * * *")
    public void updateFeedCountTopUsers() {
        statisticsUserDtoList = new ArrayList<>();
        LocalDateTime from = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime to = LocalDateTime.of(from.minusMonths(1).toLocalDate(), LocalTime.MIN);

        // 현재 최다 피드 사용자는 탑 10 명으로 순서는 따로 매기지 않는다.
        statisticsUserDtoList
                .addAll(userService.findByFeedCountTopUsers(to, from)
                        .stream()
                        .map(StatisticsUserDto::from)
                        .collect(Collectors.toList())
                );
    }


    public List<StatisticsUserDto> getFeedCountTopUsers() {
        return statisticsUserDtoList;
    }
}
