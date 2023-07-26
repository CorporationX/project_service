package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService service;

    public Internship createInternship(Internship internship) {
        createInternshipValidation(internship);
        return service.createInternship(internship);
    }

    public InternshipDto updateInternship(long id, InternshipDto internship) {
        if (id < 1) {
            throw new DataValidationException("ID error!");
        }
        updateInternshipValidation(internship);
        service.updateInternship(id, internship);
        return internship;
    }

    public InternshipDto findInternshipbyId(long id) {
        if (id < 1) {
            throw new DataValidationException("ID error!");
        }
        return service.findInternshipbyId(id);
    }

    public List<InternshipDto> findAllInternships() {
        return service.findAllInternships();
    }

    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        return service.findInternshipsWithFilter(projectId, filterDto);
    }

    private void createInternshipValidation(Internship internship) {
        if (internship == null) {
            throw new DataValidationException("Internship is null!");
        }
        if (internship.getName() == null || internship.getName().isBlank()) {
            throw new DataValidationException("Internship name can not be blank or null!");
        }
        if (internship.getProject() == null || internship.getProject().getId() < 1) {
            throw new DataValidationException("Internship relation project error!");
        }
    }

    private void updateInternshipValidation(InternshipDto internship) {
        if (internship == null) {
            throw new DataValidationException("Internship is null!");
        }
        if (internship.getName() == null || internship.getName().isBlank()) {
            throw new DataValidationException("Internship name can not be blank or null!");
        }
        if (internship.getProjectId() == null || internship.getProjectId() < 1) {
            throw new DataValidationException("Internship relation project error!");
        }
    }
}
