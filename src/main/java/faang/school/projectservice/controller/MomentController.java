package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    public MomentDto createMoment(long momentId) {
        return momentService.createMoment(momentId);
    }

    public List<MomentDto> getAllProjectMomentsByDate(Long projectId, LocalDateTime month) {
        return momentService.getAllProjectMomentsByDate(projectId, month);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentDto updateMoment(long momentId, List<Long> addedProjectIds, List<Long> addedUserIds) {
        return momentService.updateMoment(momentId, addedProjectIds, addedUserIds);
    }
    public MomentDto getMoment(long momentId) {
        return momentService.getMomentById(momentId);
    }
}
