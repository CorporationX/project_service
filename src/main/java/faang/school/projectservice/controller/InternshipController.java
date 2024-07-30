package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

@RestController
@RequiredArgsConstructor
public class InternshipController {

    private InternshipService internshipService;

    public void create(InternshipDto internship) throws DataFormatException {
        internshipService.create(internship);
    }

    public void update(@Valid InternshipDto internship, Map<Long, TeamRole> newRoles) throws DataFormatException {
        internshipService.update(internship, newRoles);
    }

    public List<InternshipDto> getInternships() {
        return internshipService.getInternships();
    }

    public InternshipDto getInternship(long internshipId) throws DataFormatException {
        return internshipService.getInternshipByID(internshipId);
    }

    public List<InternshipDto> getInternshipByStatus(InternshipStatus internshipStatus) {
        return internshipService.getInternshipStatus(internshipStatus);
    }
}