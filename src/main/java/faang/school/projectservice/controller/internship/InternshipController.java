package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
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

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NULL_INTERNSHIP_ID_EXCEPTION;

@RequestMapping("/internship")
@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipControllerValidation internshipControllerValidation;

    @PostMapping
    public InternshipDto create(@RequestBody InternshipDto internshipDto) {
        internshipControllerValidation.validationDto(internshipDto);

        return internshipService.create(internshipDto);
    }

    @PutMapping
    public InternshipDto update(@RequestBody InternshipDto internshipDto) {
        internshipControllerValidation.validationDto(internshipDto);

        internshipControllerValidation.checkForNull(internshipDto.getId(), NULL_INTERNSHIP_ID_EXCEPTION);

        return internshipService.update(internshipDto);
    }

    @GetMapping("")
    public List<InternshipDto> getInternshipsOfProject(@RequestParam(required = false) Long projectId,
                                                       @RequestBody(required = false) InternshipFilterDto filter) {
        if (projectId != null && filter != null) {
            return internshipService.getInternshipsOfProject(projectId, filter);
        }

        return internshipService.getAllInternships();
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getInternshipById(@PathVariable Long internshipId) {
        internshipControllerValidation.checkForNull(internshipId, NULL_INTERNSHIP_ID_EXCEPTION);

        return internshipService.getInternshipById(internshipId);
    }
}
