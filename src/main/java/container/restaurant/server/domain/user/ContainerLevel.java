package container.restaurant.server.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContainerLevel {
    LEVEL_5("LV5. 냄비", 20),
    LEVEL_4("LV4. 후라이팬", 10),
    LEVEL_3("LV3. 용기 세트", 5),
    LEVEL_2("LV2. 밥그릇", 1),
    LEVEL_1("LV1. 텀블러", 0);

    private final String title;
    private final Integer needCount;
}
