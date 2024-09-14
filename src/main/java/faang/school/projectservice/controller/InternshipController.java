package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.TeamRoleDto;
import faang.school.projectservice.service.InternshipServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internship")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipServiceImpl internshipService;

    @PostMapping()
    public InternshipDto createInternship(@RequestBody CreateInternshipDto createInternshipDto) {
        return internshipService.createInternship(createInternshipDto);
    }

    @PutMapping("/{internshipId}")
    public void updateInternship(@PathVariable @Positive Long internshipId, @RequestBody TeamRoleDto teamRole) {
        internshipService.updateInternship(internshipId, teamRole);
    }

    @GetMapping()
    public List<InternshipDto> getAllInternships(@RequestBody InternshipFilterDto filters) {
        return internshipService.getAllInternships(filters);
    }

    @GetMapping("/{projectId}")
    public List<InternshipDto> getAllInternshipsOnProject(@PathVariable Long projectId) {
        return internshipService.getAllInternshipsOnProject(projectId);
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getInternshipById(@PathVariable Long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}