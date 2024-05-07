package faang.school.projectservice.controller;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.service.initiative.InitiativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/initiative")
@RequiredArgsConstructor
public class InitiativeController {
    private final InitiativeService initiativeService;

    @PostMapping("/create")
    public InitiativeDto create(@RequestBody InitiativeDto initiative) {
        return initiativeService.create(initiative);
    }
}
