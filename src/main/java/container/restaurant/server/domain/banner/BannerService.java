package container.restaurant.server.domain.banner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BannerService {
    private final BannerRepository bannerRepository;
}
