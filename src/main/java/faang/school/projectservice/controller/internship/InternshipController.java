package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NULL_INTERNSHIP_ID_EXCEPTION;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipControllerValidation internshipControllerValidation;

    public InternshipDto create(InternshipDto internshipDto) {
        internshipControllerValidation.validationDto(internshipDto);

        return internshipService.create(internshipDto);
    }

    public InternshipDto update(InternshipDto internshipDto) {
        internshipControllerValidation.validationDto(internshipDto);

        if (internshipDto.getId() == null) {
            throw new DataValidationException(NULL_INTERNSHIP_ID_EXCEPTION.getMessage());
        }

        return internshipService.update(internshipDto);
    }

    public List<InternshipDto> getInternshipsOfProject(long projectId, @NonNull InternshipFilterDto filter) {
        return internshipService.getInternshipsOfProject(projectId, filter);
    }
}
