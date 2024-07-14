package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/moment")
@RequiredArgsConstructor
@Validated
public class MomentController {
    private static final String ALL_MOMENTS_FOR_FILTER = "/moments";
    private static final String ALL_MOMENTS_BY_PROJECT = "/moments-project";
    private final MomentService momentService;

    @GetMapping
    public MomentDto getMomentById(Long id) {

    }

    @PostMapping
    public MomentDto createMoment(@RequestParam Long projectId, @RequestParam MomentDto momentDto) {
        return momentService.createMoment(projectId, momentDto);
    }

    @PatchMapping
    public MomentDto updateMoment(@RequestParam Long projectId, @RequestParam MomentDto momentDto) {
        return momentService.updateMoment(projectId, momentDto);
    }

    @GetMapping(ALL_MOMENTS_FOR_FILTER)
    public List<MomentDto> getMomentsForFilter(Long projectId, MomentFilterDto filter) {
        return momentService.getListMomentForFilter(projectId, filter);
    }

    @GetMapping(ALL_MOMENTS_BY_PROJECT)
    public List<MomentDto> getAllMomentByIdProject(Long idProject) {
        return momentService.getAllMomentProject(idProject);
    }
}
