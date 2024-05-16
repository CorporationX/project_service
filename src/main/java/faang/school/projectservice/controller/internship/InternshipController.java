package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
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

    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto createInternship(@RequestBody @Valid InternshipDto internshipDto) {
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/{internshipId}/add/intern/{id}")
    public InternshipDto addNewIntern(
            @PathVariable("{internshipId}") long internshipId,
            @PathVariable("{internId}") long internId) {
        return internshipService.addNewIntern(internshipId, internId);
    }

    @PutMapping("/{internshipId}/finish/intern/{internId}")
    public InternshipDto finishInternshipForIntern(
            @PathVariable("{internshipId}") long internshipId,
            @PathVariable("{internId}") long internId,
            @RequestParam String role) {
        return internshipService.finishInternshipForIntern(internshipId, internId, role);
    }

    @DeleteMapping("/{internshipId}/delete/intern/{id}")
    public InternshipDto removeInternFromInternship(
            @PathVariable long internshipId,
            @PathVariable long id) {
        return internshipService.removeInternFromInternship(internshipId, id);
    }

    @PutMapping("/update")
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto) {
        return internshipService.updateInternship(internshipDto);
    }

    @GetMapping("/filter")
    public List<InternshipDto> getInternshipsByFilter(
            @RequestBody InternshipFilterDto filters) {
        return internshipService.getInternshipsByFilter(filters);
    }

    @GetMapping("/all")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getInternshipById(@PathVariable long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}