package faang.school.projectservice.contoller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MomentController {
    private MomentService momentService;

    @PostMapping("/moments")
    public ResponseEntity<MomentDto> create(MomentDto momentDto) {
        return ResponseEntity.ok(momentService.create(momentDto));
    }
}
