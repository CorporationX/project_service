package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        return internshipService.update(internshipDto);
    }

    public List<InternshipDto> filterInternship(InternshipFilterDto filters) {
        return internshipService.getFilteredInternship(filters);
    }

    public List<InternshipDto> getInternships() {
        return internshipService.getAllInternship();
    }

    public InternshipDto getInternship(Long id) {
        return internshipService.getInternship(id);
    }
}
