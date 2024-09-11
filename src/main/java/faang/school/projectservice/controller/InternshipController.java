package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipInfoDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
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

    @PostMapping("/create")
    public InternshipDto createInternship(@RequestBody CreateInternshipDto createInternshipDto) {
        return internshipService.createInternship(createInternshipDto);
    }

    @PutMapping("{internshipId}/update")
    public void updateInternship(@PathVariable @Positive Long internshipId,
                                 @RequestBody UpdateInternshipDto updatedInternshipDto) {
        internshipService.updateInternship(internshipId, updatedInternshipDto);
    }

    /*
    это не должно быть "project/{projectId}/internships" ?
     */
    @GetMapping()
    public List<InternshipDto> getAllInternshipsOnProject(@PathVariable Long projectId) {
        return internshipService.getAllInternshipsOnProject(projectId);
    }

    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("{internshipId}")
    public InternshipInfoDto getInternshipById(@PathVariable Long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}

