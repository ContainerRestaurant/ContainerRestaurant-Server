package container.restaurant.server.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    boolean existsDistinctByReporterIdAndCommentId(Long reporterId, Long commentId);

}
