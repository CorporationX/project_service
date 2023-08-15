package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moment")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        validateMoment(momentDto);
        return momentService.createMoment(momentDto);
    }

    private void validateMoment(MomentDto momentDto) {
        if (momentDto == null) {
            throw new DataValidationException("Moment can't be null");
        }
        if (momentDto.getName() == null || momentDto.getName().isBlank()) {
            throw new DataValidationException("Moment name can't be null or blank");
        }
    }
}
