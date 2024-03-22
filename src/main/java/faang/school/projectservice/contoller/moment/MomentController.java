package faang.school.projectservice.contoller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping("/createMoment")
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        momentService.createMoment(momentDto);
        return momentDto;
    }
    @PostMapping("/updateMoment")
    public MomentDto updateMoment(@RequestBody MomentDto momentDto) {
        momentService.updateMoment(momentDto);
        return momentDto;
    }
    @PostMapping("/filterMoment")
    public List<MomentDto> getAllMomentsByDateAndProject(@RequestBody Long projectId, MomentFilterDto filters){
        return momentService.getAllMomentsByDateAndProject(projectId, filters);
    }
}
