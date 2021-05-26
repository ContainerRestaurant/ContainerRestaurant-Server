package container.restaurant.server.domain.banner;

import container.restaurant.server.web.dto.banner.BannerInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BannerService {
    private final BannerRepository bannerRepository;

    @Transactional
    public CollectionModel<BannerInfoDto> getBanners(){
        List<BannerInfoDto> banners = new ArrayList<>();
        bannerRepository.findAll().forEach(banner ->
            banners.add(BannerInfoDto.from(banner))
        );
        return CollectionModel.of(banners);
    }
}
