package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    public MomentDto createMoment(MomentDto momentDto) throws DataValidationException {
        return momentService.createMoment(momentDto);
    }

    public List<MomentDto> getAllProjectMomentsByDate(Long projectId, LocalDateTime month) {
        return momentService.getAllProjectMomentsByDate(projectId, month);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentDto updateMoment(long momentId, List<Long> addedProjectIds, List<Long> addedUserIds) throws DataValidationException {
        return momentService.updateMoment(momentId, addedProjectIds, addedUserIds);
    }

    public MomentDto getMoment(long momentId) {
        return momentService.getMomentById(momentId);
    }
}
