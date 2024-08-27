package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Moment")
@RestController
@RequestMapping("/moment")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @Operation(summary = "Create moment")
    @PostMapping
    public MomentDto createMoment(@RequestBody @Valid MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @Operation(summary = "Update moment")
    @PutMapping
    public MomentDto updateMoment(@RequestBody @Valid MomentDto momentDto) {
        return momentService.updateMoment(momentDto);
    }

    @Operation(summary = "All Moments filters")
    @GetMapping("/filters")
    public List<MomentDto> getAllMoments(@RequestBody @NotNull MomentFilterDto momentFilterDto) {
        return momentService.getFilteredMoments(momentFilterDto);
    }

    @Operation(summary = "All Moments")
    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @Operation(summary = "Moment by Id")
    @GetMapping("/{idMoment}")
    public MomentDto getMomentById(@PathVariable long idMoment) {
        return momentService.getMomentById(idMoment);
    }
}
