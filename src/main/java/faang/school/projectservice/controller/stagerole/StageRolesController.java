package faang.school.projectservice.controller.stagerole;


import faang.school.projectservice.dto.stagerole.StageRolesDto;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.stagerole.StageRolesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/stage/role")
@RequiredArgsConstructor
@Validated
public class StageRolesController {
    private final StageRolesService stageRolesService;

    @PostMapping
    public StageRolesDto create(@Valid @RequestBody StageRolesDto dto) {
        return stageRolesService.create(dto);
    }

    @GetMapping
    public List<StageRolesDto> getAllByStageId(@PositiveOrZero @RequestHeader(value = "x-stage-id") long stageId) {
        return null;
    }

    @GetMapping(path = "/{id}")
    public StageRolesDto getById(@PositiveOrZero @PathVariable(name = "id") long stageRoleId) {
        return null;
    }

    @DeleteMapping(path = "/{id}")
    public StageRolesDto delete(@PositiveOrZero @PathVariable(name = "id") long stageRoleId) {
        return null;
    }
}
