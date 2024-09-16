package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;

import java.util.List;

public interface MomentService {
    MomentDto createMoment(MomentDto momentDto);

    MomentDto updateMomentByProjects(MomentDto momentDto);

    MomentDto updateMomentByUser(long userId, MomentDto momentDto);

    List<MomentDto> getMomentsByFilters(long projectId, MomentFilterDto filterDto);

    List<MomentDto> getAllMoments();

    MomentDto getMomentById(long momentId);
}

