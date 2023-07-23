package faang.school.projectservice.contoller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    @PostMapping("/moments")
    public MomentDto create(MomentDto momentDto) {
        return momentService.create(momentDto);
    }
}
