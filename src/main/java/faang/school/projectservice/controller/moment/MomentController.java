package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    private final MomentServiceImpl momentService;

    @PostMapping
    public ResponseEntity<MomentDto> createMoment(@RequestBody @Validated MomentDto momentDto) {
        MomentDto createMoment = momentService.createMoment(momentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createMoment);
    }

    @PutMapping
    public ResponseEntity<MomentDto> updateMomentByProjects(@RequestBody @Validated MomentDto momentDto) {
        MomentDto updateMoment = momentService.updateMomentByProjects(momentDto);
        return ResponseEntity.ok(updateMoment);
    }

    @PutMapping("/userId/{userId}")
    public ResponseEntity<MomentDto> updateMomentByUser(@PathVariable("userId") @Positive long userId,
                                                        @RequestBody @Validated MomentDto momentDto) {
        MomentDto updateMoment = momentService.updateMomentByUser(userId, momentDto);
        return ResponseEntity.ok(updateMoment);
    }

    @GetMapping("/projectId/{projectId}")
    public ResponseEntity<List<MomentDto>> getMomentsByFilters(@PathVariable("projectId") @Positive long projectId,
                                                               @RequestBody MomentFilterDto filterDto) {
        List<MomentDto> moments = momentService.getMomentsByFilters(projectId, filterDto);
        return ResponseEntity.ok(moments);
    }

    @GetMapping
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        List<MomentDto> moments = momentService.getAllMoments();
        return ResponseEntity.ok(moments);
    }

    @GetMapping("/momentId/{momentId}")
    public ResponseEntity<MomentDto> getMomentById(@PathVariable("momentId") @Positive long momentId) {
        MomentDto moment = momentService.getMomentById(momentId);
        return ResponseEntity.ok(moment);
    }
}

