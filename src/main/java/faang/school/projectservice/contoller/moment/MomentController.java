package faang.school.projectservice.contoller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        momentService.createMoment(momentDto);
        return momentDto;
    }
}
