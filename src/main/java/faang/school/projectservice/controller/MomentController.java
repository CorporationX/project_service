package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    public void createMoment(@Valid MomentDto momentDto) {
        momentService.createMoment(momentDto);
    }

    public void updateMoment(@Valid MomentDto momentDto) {
        momentService.updateMoment(momentDto);
    }

    public List<MomentDto> getAllMoments(@NotNull MomentFilterDto momentFilterDto) {
        return momentService.getAllMoments(momentFilterDto);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentDto getMomentById(@NotNull Long id) {
        return momentService.getMomentById(id);
    }
}
