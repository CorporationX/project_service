package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public MomentDto create(@RequestBody MomentDto momentDto) {
        return momentService.create(momentDto);
    }

    @PutMapping("/update/{momentId}")
    public MomentDto update(@PathVariable Long momentId, @RequestBody MomentDto momentDto) {
        return momentService.update(momentId, momentDto);
    }

    @GetMapping("/{projectId}/filter")
    public List<MomentDto> getAllMomentsByFilters(@PathVariable Long projectId, @RequestBody MomentFilterDto filters) {
        return momentService.getAllMomentsByFilters(projectId, filters);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{momentId}")
    public MomentDto getMomentById(@PathVariable Long momentId) {
        return momentService.getMomentById(momentId);
    }
}
