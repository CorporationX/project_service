package faang.school.projectservice.controller.moment;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/v1/moments")
@AllArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @GetMapping("/{id}")
    public MomentDto getById(@PathVariable @Positive long id) {
        return momentService.getMomentDtoById(id);
    }

    @GetMapping
    public List<MomentDto> getAll() {
        return momentService.getAll();
    }

    @GetMapping("/filter")
    public List<MomentDto> filterBy(@RequestParam(value = "projectId") @Positive long projectId,
                                    @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
                                    @RequestParam(value = "endDate", required = false) LocalDateTime endDate) {
        MomentFilterDto momentFilterDto = new MomentFilterDto(startDate, endDate);
        return momentService.filterBy(projectId, momentFilterDto);
    }

    @PostMapping
    public MomentDto create(@RequestBody @Valid MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PatchMapping("/{id}")
    public MomentDto update(@PathVariable("id") @Positive long id,
                            @RequestBody @Valid MomentDto momentDto) {
        return momentService.updateMoment(id, momentDto);
    }
}
