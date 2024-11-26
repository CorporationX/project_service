package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
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

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NULL_INTERNSHIP_ID_EXCEPTION;

@RequestMapping("/internship")
@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipControllerValidation internshipControllerValidation;

    @PostMapping
    public InternshipDto create(@Valid @RequestBody InternshipDto internshipDto) {
        internshipControllerValidation.validateInternshipDuration(internshipDto);
        internshipControllerValidation.validateInternshipDates(internshipDto);

        return internshipService.create(internshipDto);
    }

    @PutMapping
    public InternshipDto update(@Valid @RequestBody InternshipDto internshipDto) {
        internshipControllerValidation.validateInternshipDuration(internshipDto);

        if (internshipDto.getId() == null) {
            throw new DataValidationException(NULL_INTERNSHIP_ID_EXCEPTION.getMessage());
        }

        return internshipService.update(internshipDto);
    }

    @PostMapping(value = "/project/{projectId}")
    public List<InternshipDto> getInternshipsOfProject(@PathVariable Long projectId,
                                                       @RequestBody InternshipFilterDto filter) {
        return internshipService.getInternshipsOfProject(projectId, filter);
    }

    @GetMapping
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getInternshipById(@PathVariable Long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}