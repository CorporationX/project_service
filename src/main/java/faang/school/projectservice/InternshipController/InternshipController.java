package faang.school.projectservice.InternshipController;

import faang.school.projectservice.InternshipService.InternshipService;
import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    private void validateThereIsProjectForInternship(InternshipDto internshipDto) {
        if (internshipDto.getProjectId() == null) {
            throw new DataValidationException("There is not project for create internship!");
        }
        if (internshipDto.getId() <= 0) {
            throw new DataValidationException("Id of internship can't be negative!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Need create a name for the internship");
        }
    }

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        validateThereIsProjectForInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    private void validateInternship(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new DataValidationException("There is not internship!");
        }
        if (internshipDto.getId() == null) {
            throw new DataValidationException("There is not internship with this ID!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Incorrect name of internship!");
        }
    }
    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
        validateInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }
}
