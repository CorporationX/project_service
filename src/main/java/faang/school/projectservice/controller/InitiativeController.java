package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InitiativeDto;
import faang.school.projectservice.service.InitiativeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/initiatives")
public class InitiativeController {

    @Autowired
    private InitiativeService initiativeService;

    @PostMapping
    public InitiativeDto createInitiative(@RequestBody InitiativeDto initiativeDto) {
        if (initiativeDto.getName() == null || initiativeDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Initiative name cannot be empty");
        }
        return initiativeService.createInitiative(initiativeDto);
    }

    @PutMapping("/{id}")
    public InitiativeDto updateInitiative(@PathVariable Long id, @RequestBody InitiativeDto initiativeDto) {
        if (initiativeDto.getName() == null || initiativeDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Initiative name cannot be empty");
        }
        return initiativeService.updateInitiative(id, initiativeDto);
    }

    @GetMapping
    public List<InitiativeDto> getAllInitiatives(@RequestParam(required = false) String status,
                                                 @RequestParam(required = false) Long curatorId) {
        return initiativeService.getAllInitiatives(status, curatorId);
    }

    @GetMapping("/{id}")
    public InitiativeDto getInitiativeById(@PathVariable Long id) {
        return initiativeService.getInitiativeById(id);
    }
}