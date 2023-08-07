package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moments")
@Validated
@RequiredArgsConstructor
@Slf4j
public class MomentController {
    private final MomentService momentService;

    @PostMapping
    public MomentDto create(@Valid @RequestBody MomentDto momentDto) {
        log.debug("Received request to create moment: {}", momentDto);
        return momentService.create(momentDto);
    }
}
