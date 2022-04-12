package container.restaurant.server.domain.push;

public interface PushService {

    void sendMessage(PushToken target, String body);

}
