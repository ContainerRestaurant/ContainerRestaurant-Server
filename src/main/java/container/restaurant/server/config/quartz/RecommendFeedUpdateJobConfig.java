package container.restaurant.server.config.quartz;

import container.restaurant.server.domain.feed.recommend.RecommendFeedUpdateJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@RequiredArgsConstructor
@Configuration
public class RecommendFeedUpdateJobConfig {

    private final Scheduler scheduler;

    @PostConstruct
    public void scheduleRecommendFeedUpdateJob() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(RecommendFeedUpdateJob.class)
                .withIdentity(RecommendFeedUpdateJob.JOB_KEY)
                .withDescription("추천 피드를 업데이트하는 작업")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(RecommendFeedUpdateJob.JOB_KEY)
                .withIdentity(RecommendFeedUpdateJob.TRIGGER_KEY)
                .withSchedule(cronSchedule("0 10 0 1/1 * ? *"))
                .withDescription("매일 00 시 10 분에 발생시키는 트리거")
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.triggerJob(RecommendFeedUpdateJob.JOB_KEY);
    }

}
