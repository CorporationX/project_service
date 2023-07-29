package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

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
