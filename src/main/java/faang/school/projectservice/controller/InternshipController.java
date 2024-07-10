package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.zip.DataFormatException;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private InternshipService internshipService;

    public void create(InternshipDto internship) throws DataFormatException {
        internshipService.create(internship);
    }

    public void update(InternshipDto internship) throws DataFormatException {
        internshipService.update(internship);
    }

    public List<InternshipDto> getInternship() {
        return internshipService.getInternships();
    }

    public InternshipDto getInternship(long internshipId) throws DataFormatException {
        return internshipService.getInternshipID(internshipId);
    }

    public List<InternshipDto> getInternship(InternshipStatus internshipStatus) {
        return internshipService.getInternshipStatus(internshipStatus);
    }
}