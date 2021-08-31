package container.restaurant.server.domain.home.banner;

import container.restaurant.server.web.dto.HomeBannerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("select new container.restaurant.server.web.dto." +
              "HomeBannerDto(b.id, b.bannerURL) " +
            "from TB_BANNER b")
    List<HomeBannerDto> findAllHomeBanner();

}
