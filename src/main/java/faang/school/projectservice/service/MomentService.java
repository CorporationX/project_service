package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.exceptions.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

public interface MomentService {
    List<MomentDto> getAllMoments();

    MomentDto getMomentById(Long momentId);

    List<MomentDto> getAllProjectMomentsByDate(Long projectId, LocalDateTime month);

    MomentDto createMoment(MomentDto momentDto) throws DataValidationException;

    MomentDto updateMoment(long momentId, List<Long> addedProjectIds, List<Long> addedUserIds) throws DataValidationException;
}
