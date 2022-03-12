package container.restaurant.server.web.dto.statistics;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.List;

@Getter
public class StatisticsInfoDto extends RepresentationModel<StatisticsInfoDto> implements Serializable {

    private final List<StatisticsUserDto> statisticsUserDto;
    private final int todayFeedCount;

    public static StatisticsInfoDto from(List<StatisticsUserDto> statisticsUserDto, int todyFeed) {
        return new StatisticsInfoDto(statisticsUserDto, todyFeed);
    }

    protected StatisticsInfoDto(List<StatisticsUserDto> statisticsUserDto, int todayFeed) {
        this.statisticsUserDto = statisticsUserDto;
        this.todayFeedCount = todayFeed;
    }

}
