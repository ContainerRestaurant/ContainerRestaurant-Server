package container.restaurant.server.domain.statistics;

import container.restaurant.server.domain.user.User;
import container.restaurant.server.domain.user.UserService;
import container.restaurant.server.web.dto.statistics.StatisticsInfoDto;
import container.restaurant.server.web.dto.statistics.StatisticsUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class StatisticsService implements ApplicationRunner {
    private final UserService userService;
    public static LinkedList<User> userLinkedList;
    public static int todayFeedCount = 0;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userLinkedList = new LinkedList<>();
        // 오늘 작성한 피드 오래된 순으로 저장 가장 마지막이 최신
        userService.findByToDayFeedWriters()
                .stream()
                .map(user -> {
                    addRecentUser(user);
                    return null;
                }).collect(Collectors.toList());

    }

    @Transactional
    public StatisticsInfoDto getRecentFeedUsers() {
        List<StatisticsUserDto> statisticsUserDtoList = new ArrayList<>();
        int count = userLinkedList.size() > 3 ? 3 : userLinkedList.size();
        for (int i = 0; i < count; i++) {
            statisticsUserDtoList
                    .add(StatisticsUserDto.from(userLinkedList.get(i)));
        }
        return StatisticsInfoDto.from(statisticsUserDtoList, todayFeedCount);
    }

    public static void addRecentUser(User user) {
        // 중복되는 사용자가 있으면, 해당 위치를 지우고 최신으로 추가
        if (userLinkedList.contains(user))
            userLinkedList.remove(user);

        // 리스트 사용자가 10 명이 넘으면 마지막 삭제 LRU 형식
        if (userLinkedList.size() >= 10)
            userLinkedList.removeLast();

        userLinkedList.addFirst(user);
        todayFeedCount++;
    }

    /*
     * 최근 작성한(5명 안에) 사용자가 피드를 삭제 했을 경우
     * 최근 사용자 리스트에서 삭제
     * @param user
     */
    public static void removeRecentUser(User user) {
        userLinkedList.remove(user);
        todayFeedCount--;
    }
}
