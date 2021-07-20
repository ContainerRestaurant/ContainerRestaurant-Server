package container.restaurant.server.domain.feed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    KOREAN("한식"),
    NIGHT_MEAL("야식"),
    CHINESE("중식"),
    SCHOOL_FOOD("분식"),
    FAST_FOOD("패스트푸드"),
    ASIAN_AND_WESTERN("아시안/양식"),
    COFFEE_AND_DESSERT("카페/디저트"),
    JAPANESE("돈까스/회/일식"),
    CHICKEN_AND_PIZZA("치킨/피자");

    private final String korean;
}
