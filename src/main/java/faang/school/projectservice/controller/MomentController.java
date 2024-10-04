package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping ("/api/v1/moment")
@RequiredArgsConstructor
@Tag(name = "Moment", description = "Moment management APIs")
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    @Operation(summary = "Create a new moment", description = "Creates a new moment with the provided details")
    public MomentDto createMoment(@RequestBody @Valid MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a moment", description = "Updates an existing moment with the provided details")
    public ResponseEntity<MomentDto> updateMoment(@PathVariable Long id, @RequestBody MomentDto momentDto) {
        MomentDto updatedMoment = momentService.updateMoment(id, momentDto);
        return ResponseEntity.ok(updatedMoment);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a moment", description = "Retrieves a moment by its ID")
    public  ResponseEntity<MomentDto> getMoment(@PathVariable Long id){
        MomentDto moment = momentService.getMomentById(id);
        return ResponseEntity.ok(moment);
    }

    @GetMapping
    @Operation(summary = "Get all moments", description = "Retrieves all moments")
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        List<MomentDto> moments = momentService.getAllMoment();
        return ResponseEntity.ok(moments);
    }
}
