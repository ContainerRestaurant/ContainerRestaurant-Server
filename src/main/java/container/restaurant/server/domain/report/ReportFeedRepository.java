package container.restaurant.server.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportFeedRepository extends JpaRepository<ReportFeed, Long> {

    boolean existsDistinctByReporterIdAndFeedId(Long reporterId, Long feedId);

    void deleteAllByReporterId(Long reporterId);

    void deleteAllByFeedId(Long feedId);

}
