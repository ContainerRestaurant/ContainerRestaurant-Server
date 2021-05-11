package container.restaurant.server.domain.restaurant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@SpringBootTest
public class restaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private int dataSize = 0;

    @ParameterizedTest
    @CsvSource({
            "용기낸 식당, 경기도 성남시 어딘가, 37.43924967392599, 127.12760127510042", // 태평역 5번 출구
            "피자나라 치킨공주, 서울시 구로구 어딘가, 37.43302666744512, 127.12918108322958", // 모란역 1번 출구
            "두배마니, 인천시 용현동 어딘가, 37.43679000288809, 127.13189113423786", // 성남수정초
            "롯데리아, 경기도 시흥시 어딘가, 37.44139426827272, 127.13277707448933", // 수정구 우체국
    })
    @DisplayName("DB 데이터 삽입")
    /*
     * 현재 피드 연계를 제외한 데이터 삽입 테스트
     */
    public void testSave(String name, String addr, float lat, float lon) throws ParseException {
        Point location = (Point) new WKTReader().read(String.format("POINT(%s %s)", lon, lat));
        Restaurant testRest = new Restaurant().builder()
                .name(name)
                .addr(addr)
                .loc(location)
                .lat(lat)
                .lon(lon)
                .build();
        restaurantRepository.save(testRest);
        dataSize++;
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 3, 4})
    @DisplayName("아이디 값으로 조회 확인")
    public void testFindById(long id) {
        try {
            Restaurant restaurant = restaurantRepository.findById(id).orElseThrow();
            assertThat(restaurant.getId()).isEqualTo(id);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return;
        }
    }

    @Test
    @DisplayName("데이터 삽입 개수 확인")
    public void testCount() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        assertThat(restaurants.size()).isEqualTo(dataSize);
    }

}
