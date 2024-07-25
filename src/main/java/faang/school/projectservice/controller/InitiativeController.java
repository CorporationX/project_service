package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.initiative.ReadInitiativeDto;
import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.service.InitiativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/initiative")
public class InitiativeController {

    private final InitiativeService initiativeService;

    @GetMapping()
    public List<ReadInitiativeDto> getAllInitiativesByFilter(@RequestParam(required = false) InitiativeStatus status,
                                                             @RequestParam(required = false) Long curatorId) {
        return initiativeService.findAllByFilter(new InitiativeFilterDto(status, curatorId));
    }

    @GetMapping("/{id}")
    public ReadInitiativeDto getInitiativeById(@PathVariable Long id) {
        return initiativeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadInitiativeDto createInitiative(@RequestBody WriteInitiativeDto writeInitiativeDto) {
        return initiativeService.create(writeInitiativeDto);
    }

    @PutMapping("/{id}")
    public ReadInitiativeDto updateInitiative(@PathVariable Long id, @RequestBody WriteInitiativeDto writeInitiativeDto) {
        return initiativeService.update(id, writeInitiativeDto);
    }
}
