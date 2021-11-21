package container.restaurant.server.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ContainerLevel {
    LEVEL_5("LV5. 냄비", 5, 20),
    LEVEL_4("LV4. 후라이팬", 4, 10),
    LEVEL_3("LV3. 용기 세트", 3, 5),
    LEVEL_2("LV2. 밥그릇", 2, 1),
    LEVEL_1("LV1. 텀블러", 1, 0);

    private final String title;
    private final int level;
    private final Integer needCount;

    public static ContainerLevel getLevel(int level) {
        return Arrays.stream(values())
                .filter(it -> it.level == level)
                .findFirst().orElse(null);
    }
}
