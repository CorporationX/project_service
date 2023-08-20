package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentDtoUpdate;
import faang.school.projectservice.filter.moment.FilterMomentDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/moments")
public class MomentController {
    private final MomentService momentService;
    private final UserContext userContext;

    @PostMapping
    public void createMoment(@Valid MomentDto momentDto) {
        momentService.createMoment(momentDto, userContext.getUserId());
    }

    @PutMapping
    public void updateMoment(@Valid MomentDtoUpdate momentDtoUpdate) {
        momentService.updateMoment(momentDtoUpdate, userContext.getUserId());
    }

    @GetMapping("/{idProject}/filter")
    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto, @PathVariable Long idProject) {
        return momentService.getFilteredMoments(filterMomentDto, idProject, userContext.getUserId());
    }

    @GetMapping("/{idProject}")
    public List<MomentDtoUpdate> getAllMoments(@PathVariable Long idProject) {
        return momentService.getAllMoments(userContext.getUserId(), idProject);
    }

    @GetMapping("{id}")
    public MomentDtoUpdate getMoment(@PathVariable("id") @Valid @Min(0) long momentId) {
        return momentService.getMoment(momentId, userContext.getUserId());
    }
}
