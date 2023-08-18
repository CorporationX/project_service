package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/filter")
    public List<MomentDto> getAllWithFilter(@Valid @RequestBody MomentFilterDto momentFilterDto) {
        log.debug("Received request to get moments with filter: {}", momentFilterDto);
        return momentService.getAllWithFilter(momentFilterDto);
    }

    @GetMapping
    public List<MomentDto> getAll() {
        log.debug("Received request to get all moments");
        return momentService.getAll();
    }

    @GetMapping("/{id}")
    public MomentDto getById(@PathVariable Long id) {
        log.debug("Received request to get the moment with id: {}", id);
        return momentService.getById(id);
    }
}
