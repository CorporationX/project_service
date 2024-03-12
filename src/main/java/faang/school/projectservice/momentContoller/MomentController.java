package faang.school.projectservice.momentContoller;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.momentService.MomentService;
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
    public Moment createMoment(@RequestBody Moment moment) {
        momentService.createMoment(moment);
        return moment;
    }
}
