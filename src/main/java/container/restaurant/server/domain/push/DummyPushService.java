package container.restaurant.server.domain.push;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class DummyPushService implements PushService {

    @Override
    public void sendMessage(PushToken target, String body) {
        log.info("Push skipped on non-production profile. target: {}, body: {}", target == null ? null : target.getToken(), body);
    }
}
