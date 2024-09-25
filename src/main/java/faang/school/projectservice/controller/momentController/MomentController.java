package faang.school.projectservice.controller.momentController;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping ("/moments")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public ResponseEntity<MomentDto> createMoment(@RequestBody @Validated MomentDto momentDto) {
        MomentDto result = momentService.createMoment(momentDto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MomentDto> updateMoment(@PathVariable Long id, @RequestBody MomentDto momentDto) {
        MomentDto updatedMoment = momentService.updateMoment(id, momentDto);
        return ResponseEntity.ok(updatedMoment);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<MomentDto> getMomentById(@PathVariable Long id){
        MomentDto moment = momentService.getMomentById(id);
        return ResponseEntity.ok(moment);
    }

    @GetMapping
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        List<MomentDto> moments = momentService.getAllMoment();
        return ResponseEntity.ok(moments);
    }
}
