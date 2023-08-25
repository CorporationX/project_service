package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.GeneralSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.service.subproject.SubprojectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subproject")
@Tag(name = "Подпроекты")
public class SubprojectController {
    private final SubprojectService subprojectService;

    @PostMapping
    @Operation(summary = "Create subproject", description = "Create subproject")
    public GeneralSubprojectDto createSubproject(
            @RequestParam Long parentProjectId,
            @Valid @RequestBody GeneralSubprojectDto generalSubprojectDto) {
        return subprojectService.createSubproject(parentProjectId, generalSubprojectDto);
    }

    @PutMapping
    @Operation(summary = "Update subproject", description = "Update subproject")
    public GeneralSubprojectDto updateSubproject(
            @RequestParam Long subprojectId,
            @RequestBody SubprojectUpdateDto subprojectUpdateDto) {
        return subprojectService.updateSubproject(subprojectId, subprojectUpdateDto);
    }

    @GetMapping
    @Operation(summary = "Get all child projects ids of a subproject by filter",
            description = "Get all child projects ids of a subproject by filter")
    public List<Long> getChildProjectsOfSubprojectByFilter(
            @RequestParam Long subprojectId,
            @RequestBody SubprojectFilterDto filterDto) {
        return subprojectService.getAllSubprojectByFilter(subprojectId, filterDto);
    }
}
