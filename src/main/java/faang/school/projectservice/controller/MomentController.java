package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/moments")
    public Page<MomentDto> getAllMoments(@RequestParam(value = "page") int page,
                                         @RequestParam(value = "pageSize") int pageSize) {
        return momentService.getAllMoments(page, pageSize);
    }

    @GetMapping("/moments/{id}")
    public MomentDto getMomentById(@PathVariable Long id) {
        return momentService.getById(id);
    }

    private void momentValidate(MomentDto momentDto) {
        if (momentDto.getName() == null || momentDto.getName().isEmpty()) {
            throw new DataValidationException("moment name can not be empty");
        }
    }

}
