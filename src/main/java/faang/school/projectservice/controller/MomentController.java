package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/moment")
@RequiredArgsConstructor
@Validated
public class MomentController {
    private static final String ALL_MOMENTS = "/all";
    private static final String PROJECT_ID = "/{projectId}";
    private final MomentService momentService;

    @GetMapping
    public MomentDto getMomentById(@RequestParam @NotNull Long id) {
        return momentService.getMomentById(id);
    }

    @PostMapping(PROJECT_ID)
    public MomentDto createMoment(@PathVariable @NotNull Long projectId, @RequestBody @NotNull MomentDto momentDto) {
        return momentService.createMoment(projectId, momentDto);
    }

    @PutMapping(PROJECT_ID)
    public MomentDto updateMoment(@PathVariable @NotNull Long projectId, @RequestBody @NotNull MomentDto momentDto) {
        return momentService.updateMoment(projectId, momentDto);
    }

    @GetMapping(ALL_MOMENTS)
    public List<MomentDto> getMomentsForFilter(@RequestParam @NotNull Long projectId, @RequestBody @NotNull MomentFilterDto filter) {
        return momentService.getListMomentForFilter(projectId, filter);
    }

    @GetMapping(PROJECT_ID)
    public List<MomentDto> getAllMomentByIdProject(@RequestParam @NotNull Long idProject) {
        return momentService.getAllMomentProject(idProject);
    }
}
