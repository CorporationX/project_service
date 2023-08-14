package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoReqCreate;
import faang.school.projectservice.service.subproject.SubprojectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subproject")
@Tag(name = "Подпроекты")
public class SubprojectController {
    private final SubprojectService subprojectService;

    @PostMapping("/create")
    public SubprojectDtoReqCreate createSubproject(
            @RequestParam Long parentProjectId,
            @Valid @RequestBody SubprojectDtoReqCreate subprojectDtoReqCreate) {
        return subprojectService.createSubproject(parentProjectId, subprojectDtoReqCreate);
    }
}
