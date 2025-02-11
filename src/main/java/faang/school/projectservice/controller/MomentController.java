package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.*;
import faang.school.projectservice.service.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Moment")
@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    @Operation(summary = "Эндпоинт для создания момента")
    public ResponseEntity<CreateMomentResponse> createMoment(@RequestBody @Valid CreateMomentRequest createMomentReq) {
        var moment = momentService.createMoment(createMomentReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(moment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Эндпоинт для обновления момента")
    public UpdateMomentResponse updateMoment(@PathVariable long id,
                                             @RequestBody @Valid UpdateMomentRequest updateMomentRequest) {
        return momentService.updateMoment(id, updateMomentRequest);
    }

    @GetMapping
    @Operation(summary = "Эндпоинт для получения списка моментов")
    public List<GetMomentResponse> getMoments(MomentFilter momentFilter) {
        return momentService.getMoments(momentFilter);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Эндпоинт для получения определенного момента")
    public GetMomentResponse getMoment(@PathVariable long id) {
        return momentService.getMoment(id);
    }
}
