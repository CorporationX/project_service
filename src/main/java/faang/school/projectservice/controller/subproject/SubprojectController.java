package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.GeneralSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.service.subproject.SubprojectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
