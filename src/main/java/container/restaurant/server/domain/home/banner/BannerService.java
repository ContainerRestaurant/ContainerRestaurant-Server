package container.restaurant.server.domain.home.banner;

import container.restaurant.server.web.dto.banner.BannerInfoDto;
import container.restaurant.server.web.linker.ImageLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BannerService {
    private final BannerRepository bannerRepository;
    private final ImageLinker imageLinker;
    List<BannerInfoDto> banners = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "0 0 2 * * *")
    public void putBanners(){
        String baseURL = imageLinker.getImage("").toString();
        bannerRepository.findAll().forEach(banner ->
                banners.add(BannerInfoDto.from(banner, baseURL)));
    }

    @Transactional(readOnly = true)
    public CollectionModel<BannerInfoDto> getBanners(){ return CollectionModel.of(banners); }
}
