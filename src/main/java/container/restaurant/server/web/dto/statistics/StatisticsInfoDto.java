package container.restaurant.server.web.dto.statistics;

import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.List;

@Getter
public class StatisticsInfoDto extends RepresentationModel<StatisticsInfoDto> implements Serializable {

    private final List<EntityModel> statisticsUserDto;
    private final int todayFeedCount;

    public static StatisticsInfoDto from(List<EntityModel> statisticsUserDto, int todyFeed) {
        return new StatisticsInfoDto(statisticsUserDto, todyFeed);
    }

    protected StatisticsInfoDto(List<EntityModel> statisticsUserDto, int todyFeed) {
        this.statisticsUserDto = statisticsUserDto;
        this.todayFeedCount = todyFeed;
    }

}
