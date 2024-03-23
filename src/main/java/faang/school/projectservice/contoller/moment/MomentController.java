package faang.school.projectservice.contoller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.moment.MomentService;
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
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        momentService.createMoment(momentDto);
        return momentDto;
    }
    @PutMapping
    public MomentDto updateMoment(@RequestBody MomentDto momentDto) {
        momentService.updateMoment(momentDto);
        return momentDto;
    }
    @PostMapping("/{projectId}/filtered")
    public List<MomentDto> getAllMomentsByDateAndProject(@PathVariable long projectId, @RequestBody MomentFilterDto filters){
        return momentService.getAllMomentsByDateAndProject(projectId, filters);
    }
    @GetMapping
    public List<MomentDto> getAllMoments(){
        return momentService.getAllMoments();
    }
    @GetMapping("/{momentId}/")
    public MomentDto getMomentById(@PathVariable long momentId){
        return momentService.getMomentById(momentId);
    }

}
