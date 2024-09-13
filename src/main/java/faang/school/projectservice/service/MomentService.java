package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;

import java.time.LocalDateTime;
import java.util.List;

public interface MomentService {
    List<MomentDto> getAllProjectMomentsByDate(Long projectId, LocalDateTime month);

    List<MomentDto> getAllMoments();

    MomentDto getMomentById(Long momentId);

    MomentDto createMoment(Moment moment);

    MomentDto updateMoment(long momentId, List<Long> addedProjectIds, List<Long> addedUserIds);
}
