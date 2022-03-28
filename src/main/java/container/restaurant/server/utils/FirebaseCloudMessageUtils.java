package container.restaurant.server.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import container.restaurant.server.domain.push.PushToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Log4j2
@RequiredArgsConstructor
public class FirebaseCloudMessageUtils {

    @Value("${firebase.key.path}")
    private String FIREBASE_SERVICE_ACCOUNT_KEY_PATH;
    private static final String PUSH_TITLE = "용기낸 식당";

    private FirebaseMessaging firebaseMessaging;

    @PostConstruct
    private void setup() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream(FIREBASE_SERVICE_ACCOUNT_KEY_PATH))
                .createScoped((List.of("https://www.googleapis.com/auth/cloud-platform")));

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions);
        this.firebaseMessaging = FirebaseMessaging.getInstance(app);
    }

    public void sendMessage(PushToken target, String body) {
        if (target == null || !StringUtils.hasText(target.getToken())) {
            log.info("PushToken is null.");
            return;
        }

        Message message = makeMessage(target.getToken(), body);
        try {
            String sendMessageId = firebaseMessaging.send(message);
            log.info("Push success. {}", sendMessageId);
        } catch (FirebaseMessagingException e) {
            log.error("Push failed. {}", e.getMessagingErrorCode());
        }
    }

    private Message makeMessage(String target, String body) {
        Notification.Builder notificationBuilder = Notification.builder();
        Map<String, String> data = new HashMap<>(); // android 요청으로 Notification 과 같은 내용 추가

        // title
        notificationBuilder.setTitle(PUSH_TITLE);
        data.put("title", PUSH_TITLE);

        // body
        if (StringUtils.hasText(body)) {
            notificationBuilder.setBody(body);
            data.put("body", body);
        }

        return Message.builder()
                .setToken(target)
                .setNotification(notificationBuilder.build())
                .putAllData(data)
                .build();
    }
}
