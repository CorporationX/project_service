package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/moments")
@RequiredArgsConstructor
@Validated
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public ResponseEntity<MomentDto> saveMoment(@Valid @RequestBody MomentDto momentDto) {
        MomentDto savedMoment = momentService.saveMoment(momentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMoment);
    }

    @PutMapping
    public ResponseEntity<MomentDto> updateMomentWithParthner(@Valid @RequestBody MomentDto momentDto) {
       MomentDto updateMoment =  momentService.updateMoment(momentDto);
       return ResponseEntity.ok(updateMoment);
    }

    @GetMapping("/filters")
    public ResponseEntity<List<MomentDto>> getMomentsWithFilter(@Valid @RequestParam MomentFilterDto filterDto) {
        List<MomentDto> momentDtos = momentService.getMoments(filterDto);
        if(momentDtos.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(momentDtos);
    }

    @GetMapping
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        List<MomentDto> momentDtos =  momentService.getAllMoments();
        if (momentDtos.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(momentDtos);
    }

    @GetMapping("/{momentId}")
    public ResponseEntity<MomentDto> getMoment(@PathVariable @NotNull @Positive long momentId) {
        MomentDto momentDto =  momentService.getMoment(momentId);
        return ResponseEntity.ok(momentDto);
    }
}
