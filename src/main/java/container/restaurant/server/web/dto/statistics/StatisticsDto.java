package container.restaurant.server.web.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

public interface StatisticsDto {

    @Getter
    @Builder
    @AllArgsConstructor
    class TotalContainer {
        private final Collection<UserProfileDto> latestWriters;
        private final Collection<UserProfileDto> topWriters;
        private final long writerCount;
        private final long feedCount;
    }

}
