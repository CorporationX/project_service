package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping("/moments")
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        momentValidate(momentDto);
        return momentService.create(momentDto);
    }

    @PutMapping("/moments/{id}")
    public MomentDto updateMoment(@PathVariable Long id,
                                  @RequestBody MomentDto momentDto) {
        momentValidate(momentDto);
        return momentService.update(id, momentDto);
    }

    private void momentValidate(MomentDto momentDto) {
        if (momentDto.getName() == null || momentDto.getName().isEmpty()) {
            throw new DataValidationException("moment name can not be empty");
        }
    }

}
