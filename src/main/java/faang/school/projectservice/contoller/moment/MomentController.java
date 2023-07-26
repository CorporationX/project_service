package faang.school.projectservice.contoller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public MomentDto create(MomentDto momentDto) {
        return momentService.create(momentDto);
    }

    @GetMapping("/{id}")
    public MomentDto getMomentById(long momentId) {
        return momentService.getMomentById(momentId);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping
    public List<MomentDto> getMomentsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return momentService.getMomentsByDate(startDate, endDate);
    }

    @GetMapping
    public List<MomentDto> getMomentsByProjects(List<Long> projectIds) {
        return momentService.getMomentsByProjects(projectIds);
    }

    @PutMapping("/{id}")
    public MomentDto update(MomentDto source) {
        return momentService.update(source);
    }
}
