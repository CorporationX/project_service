package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.repository.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping ("/moment")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody @Valid MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MomentDto> updateMoment(@PathVariable Long id, @RequestBody MomentDto momentDto) {
        MomentDto updatedMoment = momentService.updateMoment(id, momentDto);
        return ResponseEntity.ok(updatedMoment);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<MomentDto> getMoment(@PathVariable Long id){
        MomentDto moment = momentService.getMomentById(id);
        return ResponseEntity.ok(moment);
    }

    @GetMapping
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        List<MomentDto> moments = momentService.getAllMoment();
        return ResponseEntity.ok(moments);
    }
}
