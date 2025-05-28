package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "moment_controller_methods")
@RequestMapping("/moments")
public class MomentController {
    private final MomentService momentService;

    @Operation(summary = "create new moment",
            description = "Receive MomentDto object, save Moment entity after mapping in DB," +
                    " and return saved entity mapped to dto.")
    @PostMapping
    public MomentDto createMoment(@Valid @RequestBody MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/{id}")
    public MomentDto updateMoment(@PathVariable Long id, @Valid @RequestBody MomentDto momentDto) {
        return momentService.updateMoment(id, momentDto);
    }

    @GetMapping("/filter")
    public List<MomentDto> getMomentsWithFilter(@RequestParam MomentDto momentDto) {
        return momentService.getMomentsWithFilter(momentDto);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{id}")
    public MomentDto getMomentById(@PathVariable Long id) {
        return momentService.getMomentById(id);
    }
}
