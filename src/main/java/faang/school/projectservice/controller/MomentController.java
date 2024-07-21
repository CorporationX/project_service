package faang.school.projectservice.controller;

import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MomentController {
    private final MomentService momentService;
    private final MomentValidator momentValidator;
}
