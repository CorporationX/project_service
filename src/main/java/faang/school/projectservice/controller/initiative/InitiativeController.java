package faang.school.projectservice.controller.initiative;

import faang.school.projectservice.controller.ApiPath;
import faang.school.projectservice.dto.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.service.initiative.InitiativeService;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping(ApiPath.INITIATIVES_PATH)
public class InitiativeController {
    private final InitiativeService initiativeService;

    @PostMapping
    public InitiativeDto createInitiative(@RequestBody @Valid InitiativeDto initiativeDto) {
        return initiativeService.createInitiative(initiativeDto);
    }

    @PutMapping
    public InitiativeDto updateInitiative(@RequestBody @Valid InitiativeDto initiativeDto) {
        return initiativeService.updateInitiative(initiativeDto.getId(), initiativeDto);
    }

    @PostMapping(ApiPath.FILTER_FUNCTIONALITY)
    public List<InitiativeDto> getAllInitiativesWithFilter(@RequestBody @Valid InitiativeFilterDto filterDto) {
        return initiativeService.getAllInitiativesWithFilter(filterDto);
    }

    @GetMapping
    public List<InitiativeDto> getAllInitiatives() {
        return initiativeService.getAllInitiatives();
    }

    @GetMapping(ApiPath.GENERAL_ID_PLACEHOLDER)
    public InitiativeDto getInitiativeById(@PathVariable Long id) {
        return initiativeService.getInitiativeById(id);
    }
}
