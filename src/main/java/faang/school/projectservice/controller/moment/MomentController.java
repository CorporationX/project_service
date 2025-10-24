package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.SearchMomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/moments")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody @NonNull CreateMomentDto momentDto) throws Exception {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/{id}")
    public MomentDto updateMoment(
            @PathVariable("id") long momentId, @RequestBody @NonNull UpdateMomentDto momentDto) throws Exception {
        return momentService.updateMoment(momentId, momentDto);
    }

    @GetMapping("/{id}")
    public MomentDto getById(@PathVariable("id") long momentId) {
        return momentService.getById(momentId);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/by-project-id/{projectId}")
    public List<MomentDto> getMomentsByProjectId(@PathVariable long projectId) {
        return momentService.getMomentsByProjectId(projectId);
    }

    @PostMapping("/by-month")
    public List<MomentDto> getMomentsByMonth(@RequestBody SearchMomentDto searchMomentDto) {
        return momentService.getMomentsByMonth(searchMomentDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") long momentId){
        momentService.deleteById(momentId);
    }
}
