package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
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


@RequiredArgsConstructor
@RequestMapping("/moment")
@RestController
public class MomentController {
    private final MomentService momentService;

    @PostMapping("/")
    public MomentDto createMoment(@Valid @RequestBody MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/")
    public MomentDto updateMoment(@Valid @RequestBody MomentDto momentDto) {
        return momentService.updateMoment(momentDto);
    }

    @GetMapping("/filtered/{projectId}")
    public List<MomentDto> getMomentsFilteredByDateFromProjects(@PathVariable Long projectId, @RequestBody MomentFilterDto filter) {
        return momentService.getMomentsFilteredByDateFromProjects(projectId, filter);
    }

    @GetMapping("/all/{projectId}")
    public List<MomentDto> getAllMoments(@PathVariable Long projectId) {
        return momentService.getAllMoments(projectId);
    }

    @GetMapping("/{momentId}")
    public MomentDto getMomentById(@PathVariable Long momentId) {
        return momentService.getMomentById(momentId);
    }
}
