package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    public MomentDto createMoment(@Valid MomentDto momentDto, ProjectDto projectDto) {
        return momentService.createMoment(momentDto, projectDto);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        return momentService.updateMoment(momentDto);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }
    public List<MomentDto> getAllMoments(MomentFilterDto filter) {
        return momentService.getMoments(filter);
    }

    public MomentDto getMomentById(long id) {
        return momentService.getMoment(id);
    }
}
