package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService service;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        InternshipValidator.internshipControllerValidation(internshipDto);
        return service.createInternship(internshipDto);
    }

    public InternshipDto updateInternship(long id, InternshipDto internshipDto) {
        if (id < 1) {
            throw new DataValidationException("ID error!");
        }
        InternshipValidator.internshipControllerValidation(internshipDto);
        return service.updateInternship(id, internshipDto);
    }

    public InternshipDto findInternshipById(long id) {
        if (id < 1) {
            throw new DataValidationException("ID error!");
        }
        return service.findInternshipById(id);
    }

    public List<InternshipDto> findAllInternships() {
        return service.findAllInternships();
    }

    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        return service.findInternshipsWithFilter(projectId, filterDto);
    }


}
