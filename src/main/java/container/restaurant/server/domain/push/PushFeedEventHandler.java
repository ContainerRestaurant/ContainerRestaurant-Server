package container.restaurant.server.domain.push;

import container.restaurant.server.domain.push.event.CommentLikedEvent;
import container.restaurant.server.domain.push.event.FeedCommentedEvent;
import container.restaurant.server.domain.push.event.FeedHitEvent;
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
     * 피드 좋아요 이벤트
     * @param event
     * @throws IOException
     */
    @EventListener
    public void sendFeedLikedEvent(FeedLikedEvent event) {
        // 피드 작성자가 푸시 대상
        PushToken pushToken = event.getFeed().getOwner().getPushToken();

        // 피드 좋아요 사용자 + 고정문구
        String title = event.getFrom().getNickname() + " 님이 내 피드를 좋아해요\uD83D\uDC97";

        // 좋아요가 눌린 피드의 식당 이름
        String msg = event.getFeed().getRestaurant().getName();
//        fcmUtil.sendMessage(pushToken, title, msg);
    }

    /*
     * 피드에 댓글, 답글 이벤트
     * @param event
     * @throws IOException
     */
    @EventListener
    public void sendFeedCommentedEvent(FeedCommentedEvent event) {
        // 댓글 or 답글 쓴 사람
        User actor = event.getComment().getOwner();

        // 댓글의 경우 피드 사용자가 푸시 대상
        User target = event.getComment().getFeed().getOwner();

        // 댓글인 경우
        String title = String.format("내가 용기낸 피드에 %s님이 댓글을 달았어요", actor.getNickname());

        // 대댓글은 테스트 후 추가
//        if (event.getComment().getUpperReply() != null) {
//            // 답글인 경우 UpperReply의 owner 가 푸시 대상이 됨
//            target = event.getComment().getUpperReply().getOwner();
//
//            // 답글일 경우 title 를 변경
//            title = "내가 남긴 댓글에 "
//        }

        fcmUtil.sendMessage(target.getPushToken(), title);
    }

    /*
     * 피드에 댓글, 답글 좋아요 이벤트
     * @param event
     * @throws IOException
     */
    @EventListener
    public void sendCommentLikedEvent(CommentLikedEvent event) {
        // 좋아요 눌린 댓글의 작성자가 푸시 대상
        PushToken pushToken = event.getComment().getOwner().getPushToken();

        // 좋아요 눌린 댓글 내용
        String msg = event.getComment().getContent();

        // 댓글 좋아요의 경우
        // 댓글 좋아요인 경우  댓글 좋아요 사용자 + 고정문구
        String title = event.getFrom().getNickname() + " 님이 내 댓글을 좋아해요\uD83D\uDC97";

        if (event.getComment().getUpperReply() != null) {
            // 현재 댓글이 대댓글 일 때 문구 변경
            title = title.replace("댓글", "답글");
        }
//        fcmUtil.sendMessage(pushToken, title, msg);
    }

    /*
     * 피드 조회수 이벤트
     * @param event
     * @throws IOException
     */
    @EventListener
    public void sendFeedHitEvent(FeedHitEvent event) {
        // 피드 주인이 푸시 대상
        PushToken pushToken = event.getFeed().getOwner().getPushToken();

        // 푸시 개수 가져오기
        int hitCount = event.getFeed().getHitCount();

        // 푸시 개수 + 고정 문구
        String title = hitCount + "명이 내 용기낸 피드를 읽었어요\uD83D\uDC40";
        // 30명, 100명일 경우 이벤트 발송
//        if (hitCount == 30 || hitCount == 100)
//            fcmUtil.sendMessage(pushToken, title, event.getMsg());
    }
}
