package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.service.internship.InternshipService;
import faang.school.projectservice.validator.internship.InternshipControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    //TODO Logs!!! + Postman
    private final InternshipService internshipService;
    private final InternshipControllerValidator internshipValidator;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipValidator.checkDataBeforeCreate(internshipDto);
        internshipService.createInternship(internshipDto);
        return internshipDto;
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
