package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.*;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public ResponseEntity<CreateMomentResponse> createMoment(@RequestBody @Valid CreateMomentRequest createMomentReq) {
        var moment = momentService.createMoment(createMomentReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(moment);
    }

    @PutMapping("/{id}")
    public UpdateMomentResponse updateMoment(@PathVariable long id,
                                             @RequestBody @Valid UpdateMomentRequest updateMomentRequest) {
        return momentService.updateMoment(id, updateMomentRequest);
    }

    @GetMapping
    public List<GetMomentResponse> getMoments(MomentFilter momentFilter) {
        return momentService.getMoments(momentFilter);
    }

    @GetMapping("/{id}")
    public GetMomentResponse getMoment(@PathVariable long id) {
        return momentService.getMoment(id);
    }
}
