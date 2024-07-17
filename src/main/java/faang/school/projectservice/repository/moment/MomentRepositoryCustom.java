package faang.school.projectservice.repository.moment;

import faang.school.projectservice.model.Moment;

import java.time.LocalDateTime;
import java.util.List;

public interface MomentRepositoryCustom {
    List<Moment> findAllByProjectIdAndDateFiltered(Long projectId, LocalDateTime start, LocalDateTime endExclusive);
}
