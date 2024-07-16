package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moment")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody @Valid MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping
    public MomentDto updateMoment(@RequestBody @Valid MomentDto momentDto) {
        return momentService.updateMoment(momentDto);
    }

    @GetMapping("/filters")
    public List<MomentDto> getAllMoments(@RequestBody @NotNull MomentFilterDto momentFilterDto) {
        return momentService.getAllMoments(momentFilterDto);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{idMoment}")
    public MomentDto getMomentById(@PathVariable long idMoment) {
        return momentService.getMomentById(idMoment);
    }
}
