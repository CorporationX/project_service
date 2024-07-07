package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentRestDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentRestService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentRestController {
    private final MomentRestService momentService;

    @PostMapping
    public MomentRestDto create(@RequestBody @Valid MomentRestDto momentDto) {
        return momentService.create(momentDto);
    }

    @PatchMapping("/update/{momentId}")
    public MomentRestDto update(@PathVariable @NonNull Long momentId, @RequestBody @Valid MomentRestDto momentDto) {
        return momentService.update(momentId, momentDto);
    }

    @PostMapping("/{projectId}/filter")
    public List<MomentRestDto> getAllMomentsByFilters(@PathVariable @NonNull Long projectId, @RequestBody @Valid MomentFilterDto filters) {
        return momentService.getAllMomentsByFilters(projectId, filters);
    }

    @GetMapping
    public List<MomentRestDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{momentId}")
    public MomentRestDto getMomentById(@PathVariable @NonNull Long momentId) {
        return momentService.getMomentById(momentId);
    }
}
