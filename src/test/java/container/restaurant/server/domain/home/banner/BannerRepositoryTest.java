package container.restaurant.server.domain.home.banner;

import container.restaurant.server.web.dto.HomeBannerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BannerRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    BannerRepository bannerRepository;

    @Test
    @DisplayName("HomeBannerDto 프로젝션 조회 테스트")
    void HomeBannerDto_프로젝션_조회_테스트() {
        //given 영속화된 배너가 주어졌을 때
        Long id1 = em.persist(new Banner("title1", "banner1", "content1", "additional1")).getId();
        Long id2 = em.persist(new Banner("title2", "banner2", "content2", "additional2")).getId();
        Long id3 = em.persist(new Banner("title3", "banner3", "content3", "additional3")).getId();

        em.flush();em.clear();

        //when findAllHomeBanner() 함수를 호출하면
        List<HomeBannerDto> result = bannerRepository.findAllHomeBanner();

        //then 주어진 배너의 ID 와 URL 을 갖는 DTOs 가 반환된다.
        assertThat(result.size()).isEqualTo(3);
        assertThat(result)
                .anyMatch(dto -> dto.getBannerUrl().equals("banner1") && dto.getBannerId().equals(id1))
                .anyMatch(dto -> dto.getBannerUrl().equals("banner2") && dto.getBannerId().equals(id2))
                .anyMatch(dto -> dto.getBannerUrl().equals("banner3") && dto.getBannerId().equals(id3));
    }

}