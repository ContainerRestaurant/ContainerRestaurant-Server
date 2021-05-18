package container.restaurant.server.domain.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportFeedRepository reportFeedRepository;
    private final ReportCommentRepository reportCommentRepository;

}
