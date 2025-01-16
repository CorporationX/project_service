package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.Internship.InternshipControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    //TODO Logs!!!
    private final InternshipService internshipService;
    private final InternshipControllerValidator internshipValidator;

    public void createInternship(InternshipDto internshipDto) {
        internshipValidator.checkDataBeforeCreate(internshipDto);
        internshipService.createInternship(internshipDto);
    }

    public InternshipDto updateInternship(Long id) {
        internshipValidator.checkDataBeforeUpdate(id);
        return internshipService.updateInternship(id);
    }

    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        return getInternshipsWithFilters(filters);
    }

    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    public InternshipDto getInternship(Long id) {
        internshipValidator.checkDataBeforeGetInternship(id);
        return internshipService.getInternship(id);
    }
}
