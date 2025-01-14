package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public void createInternship(InternshipDto internshipDto) {
        internshipService.createInternship(internshipDto);
    }

    public InternshipDto updateInternship(Long id) {
        return internshipService.updateInternship(id);
    }

    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        return getInternshipsWithFilters(filters);
    }

    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    public InternshipDto getInternship(Long id) {
        return internshipService.getInternship(id);
    }
}
