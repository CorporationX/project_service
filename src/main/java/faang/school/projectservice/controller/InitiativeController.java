package faang.school.projectservice.controller;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.service.initiative.InitiativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/initiative")
@RequiredArgsConstructor
public class InitiativeController {
    private final InitiativeService initiativeService;

    @PostMapping("/create")
    public InitiativeDto create(@RequestBody InitiativeDto initiative) {
        return initiativeService.create(initiative);
    }

    @PutMapping("/update")
    public InitiativeDto update(@RequestBody InitiativeDto initiative) {
        return initiativeService.update(initiative);
    }

    @GetMapping
    public List<InitiativeDto> getAll() {
        return initiativeService.getAll();
    }

    @GetMapping("/{id}")
    public InitiativeDto getById(@PathVariable long id) {
        return initiativeService.getById(id);
    }
}
