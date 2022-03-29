package container.restaurant.server.domain.push;

import container.restaurant.server.domain.feed.Feed;
import container.restaurant.server.domain.push.event.CommentLikedEvent;
import container.restaurant.server.domain.push.event.FeedCommentedEvent;
import container.restaurant.server.domain.push.event.FeedLikedEvent;
import container.restaurant.server.domain.user.User;
import container.restaurant.server.utils.FirebaseCloudMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j2
public class PushFeedEventHandler {

    private final FirebaseCloudMessageUtils fcmUtil;

    /*
     * 피드 좋아요 이벤트 (좋아요가 FEED_LIKE_PUSH_THRESHOLD 의 배수일 때마다 푸시)
     * @param event
     * @throws IOException
     */
    @EventListener
    public void sendFeedLikedEvent(FeedLikedEvent event) {
        Feed targetFeed = event.getFeed();
        String restaurantName = targetFeed.getRestaurant().getName();
        User target = targetFeed.getOwner();

        // 좋아요 수가 n의 배수일때 푸시
        final int FEED_LIKE_PUSH_THRESHOLD = 3;
        int targetFeedLike = targetFeed.getLikeCount();
        if (targetFeedLike != 0 && targetFeedLike % FEED_LIKE_PUSH_THRESHOLD == 0) {
            String body = String.format("내가 용기낸 %s 피드를 %d명이 좋아해요!", restaurantName, targetFeedLike);
            fcmUtil.sendMessage(target.getPushToken(), body);
        }
    }

    /*
     * 피드에 댓글, 답글 이벤트
     * @param event
     * @throws IOException
     */
    @EventListener
    public void sendFeedCommentedEvent(FeedCommentedEvent event) {
        User actor = event.getComment().getOwner();
        User target;
        String body;

        if (event.getComment().getUpperReply() == null) {
            target = event.getComment().getFeed().getOwner();
            body = String.format("내가 용기낸 피드에 %s님이 댓글을 달았어요!", actor.getNickname());
        } else {
            target = event.getComment().getUpperReply().getOwner();
            body = String.format("내가 남긴 댓글에 %s님이 답글을 달았어요!", actor.getNickname());
        }

        if (eventByMyself(target, actor)) {
            return;
        }

        fcmUtil.sendMessage(target.getPushToken(), body);
    }

    /*
     * 피드에 댓글, 답글 좋아요 이벤트
     * @param event
     * @throws IOException
     */
    @EventListener
    public void sendCommentLikedEvent(CommentLikedEvent event) {
        User target = event.getComment().getOwner();
        User actor = event.getFrom();
        boolean hasUpperReply = event.getComment().getUpperReply() != null;

        if (eventByMyself(target, actor)) {
            return;
        }

        String body = String.format("%s님이 내 %s을 좋아해요 :)", actor.getNickname(), hasUpperReply ? "답글" : "댓글");
        fcmUtil.sendMessage(target.getPushToken(), body);
    }

    private boolean eventByMyself(User target, User actor) {
        return target.getId() == actor.getId();
    }
}
