package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internship")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipServiceImpl internshipServiceImpl;

    @PostMapping
    public InternshipDto createInternship(@RequestBody InternshipDto internshipDto) {
        return internshipServiceImpl.createInternship(internshipDto);
    }

    @PutMapping("/intern/{id}/add/{internshipId}")
    public InternshipDto addNewIntern(
            @PathVariable long internshipId,
            @PathVariable long id) {
        return internshipServiceImpl.addNewIntern(internshipId, id);
    }

    @PutMapping("/intern/{internId}/finish/{internshipId}")
    public InternshipDto finishInternshipForIntern(
            @PathVariable long internshipId,
            @PathVariable long internId,
            @RequestParam TeamRole role) {
        return internshipServiceImpl.finishInternshipForIntern(internshipId, internId, role);
    }

    @DeleteMapping("intern/{id}/delete/{internshipId}")
    public InternshipDto removeInternFromInternship(
            @PathVariable long internshipId,
            @PathVariable long id) {
        return internshipServiceImpl.removeInternFromInternship(internshipId, id);
    }

    @PutMapping
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto) {
        return internshipServiceImpl.updateInternship(internshipDto);
    }

    @GetMapping("/filter/status")
    public List<InternshipDto> getInternshipsByStatus(
            @RequestParam InternshipStatus status,
            @RequestBody InternshipFilterDto filters) {
        return internshipServiceImpl.getInternshipsByStatus(status, filters);
    }

    @GetMapping("/filter/{role}")
    public List<InternshipDto> getInternshipsByRole(
            @PathVariable TeamRole role,
            @RequestBody InternshipFilterDto filters) {
        return internshipServiceImpl.getInternshipsByRole(role, filters);
    }

    @GetMapping("/all")
    public List<InternshipDto> getAllInternships(@RequestBody InternshipFilterDto filters) {
        return internshipServiceImpl.getAllInternships(filters);
    }
}