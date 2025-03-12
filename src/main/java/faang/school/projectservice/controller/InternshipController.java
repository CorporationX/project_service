package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        return internshipService.create(internshipDto);

    }

    public InternshipDto getInternshipById(Long id) {
        return internshipService.getInternshipById(id);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        return null;
    }

    public List<InternshipDto> findAllInternships() {
       return internshipService.getAllInternships();
    }
}
