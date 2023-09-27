package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validator.MomentValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
@Tag(name = "Moments", description = "important moments of project")
public class MomentController {

    private final MomentService momentService;
    private final MomentValidator momentValidator;

    @PostMapping
    public MomentDto createMoment(@RequestBody @Valid MomentDto momentDto) {
        momentValidator.validateMoment(momentDto);
        return momentService.createMoment(momentDto);
    }

    @PutMapping("{momentId}")
    public MomentDto updateMoment(@PathVariable Long momentId, @RequestBody MomentDto momentDto) {
        momentValidator.validateMoment(momentDto);
        return momentService.updateMoment(momentId, momentDto);
    }

    @GetMapping
    public Page<MomentDto> getAllMoments(@PageableDefault(sort = {"id"}, size = 25) Pageable pageable) {
        return momentService.getAllMoments(pageable);
    }

    @GetMapping("/filter")
    public Page<MomentDto> getAllMomentsByFilter(@RequestBody MomentFilterDto filter,
                                                 @PageableDefault(sort = {"id"}, size = 25) Pageable pageable) {
        return momentService.getAllMoments(filter, pageable);
    }

    @GetMapping("{momentId}")
    public MomentDto getMomentById(@PathVariable Long momentId) {
        return momentService.getMomentById(momentId);
    }
}
