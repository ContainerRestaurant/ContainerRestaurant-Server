package container.restaurant.server.domain.feed.recommend;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

@Component
public class RecommendFeedUpdateJob implements Job {

    public final static JobKey JOB_KEY = JobKey.jobKey("detail", "feed_recommend");
    public final static TriggerKey TRIGGER_KEY = TriggerKey.triggerKey("trigger", "feed_recommend");

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        /*
        TODO
            1. 최근 7일 동안 발생한 Feed 들의 Iterable 을 가져온다. (여러번으로 나눠서라도)
            2. 7일 동안 발생한 Feed 를 전체 탐색하면서 점수가 높은 12 Feed 를 고른다.
                - Bi Comparator 를 사용 - 점수 비교 -> 시간 비교
            3. DTO 화 시켜서 Service 에 저장해 놓는다.
                - Json String 을 저장해놓고 그냥 바로 문자열을 내려주면 속도차이가 많이 날까?
         */
    }

}
