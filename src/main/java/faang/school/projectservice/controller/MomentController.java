package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        log.debug("Received request to create the moment: {}", momentDto);
        return momentService.create(momentDto);
    }

    @PutMapping("/{id}")
    public MomentDto update(@PathVariable Long id, @Valid @RequestBody MomentDto momentDto) {
        log.debug("Received request to update the moment with id: {}", id);
        return momentService.update(id, momentDto);
    }
}
