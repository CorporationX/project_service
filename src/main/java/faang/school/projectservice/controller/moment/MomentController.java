package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moments")
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@Valid @RequestBody MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/{id}")
    public MomentDto updateMoment(@PathVariable Long id, @Valid @RequestBody MomentDto momentDto) {
        return momentService.updateMoment(id, momentDto);
    }

    @GetMapping("/filter")
    public List<MomentDto> getMomentsWithFilter(MomentDto momentDto) {
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
