package faang.school.projectservice.controller.initiative;

import faang.school.projectservice.dtos.initiative.InitiativeDto;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.service.initiative.InitiativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/initiatives")
public class InitiativeController {
    private final InitiativeService initiativeService;

    @PostMapping
    public InitiativeDto createInitiative(@RequestBody InitiativeDto initiativeDto) {
        if (initiativeDto.getName() == null || initiativeDto.getName().isEmpty() || initiativeDto.getName().isBlank()) {
            throw new IllegalArgumentException("Initiative name cannot be empty");
        }
        return initiativeService.createInitiative(initiativeDto);
    }

    @PutMapping("/{id}")
    public InitiativeDto updateInitiative(@PathVariable("id") Long id, @RequestBody InitiativeDto initiativeDto) {
        if (initiativeDto.getName() == null || initiativeDto.getName().isEmpty() || initiativeDto.getName().isBlank()) {
            throw new IllegalArgumentException("Initiative name cannot be empty");
        }
        return initiativeService.updateInitiative(id, initiativeDto);
    }

    @GetMapping("/filter")
    public List<InitiativeDto> getAllInitiativesWithFilter(@RequestParam InitiativeStatus status,
                                                           @RequestParam Long curatorId) {
        return initiativeService.getAllInitiativesWithFilter(status, curatorId);
    }

    @GetMapping("/all")
    public List<InitiativeDto> getAllInitiatives() {
        return initiativeService.getAllInitiatives();
    }

    @GetMapping("{id}")
    public InitiativeDto getInitiativeById(@PathVariable Long id) {
        return initiativeService.getInitiativeById(id);
    }
}
