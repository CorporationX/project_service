package faang.school.projectservice.repository.moment;

import faang.school.projectservice.model.Moment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MomentCriteriaRepository {
    Page<Moment> findByProjectsAndDate(List<Long> projectIds, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);
}
