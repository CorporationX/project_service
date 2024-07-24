package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.initiative.ReadInitiativeDto;
import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.service.InitiativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InitiativeController {

    private final InitiativeService initiativeService;

    public ReadInitiativeDto createInitiative(WriteInitiativeDto writeInitiativeDto) {
        return initiativeService.create(writeInitiativeDto);
    }

    public ReadInitiativeDto updateInitiative(Long id, WriteInitiativeDto writeInitiativeDto) {
        return initiativeService.update(id, writeInitiativeDto);
    }

    public List<ReadInitiativeDto> getInitiativesByFilter(InitiativeFilterDto filter) {
        return initiativeService.findAllBy(filter);
    }

    public List<ReadInitiativeDto> getAllInitiative() {
        return initiativeService.findAll();
    }

    public ReadInitiativeDto getByInitiativeId(Long initiativeId) {
        return initiativeService.findById(initiativeId);
    }
}
