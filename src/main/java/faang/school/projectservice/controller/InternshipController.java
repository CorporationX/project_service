package faang.school.projectservice.controller;


import faang.school.projectservice.dto.client.internship.InternshipCreateRequest;
import faang.school.projectservice.dto.client.internship.InternshipFilterRequest;
import faang.school.projectservice.dto.client.internship.InternshipResponse;
import faang.school.projectservice.dto.client.internship.InternshipUpdateRequest;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping()
    public void createInternship(@RequestBody @Valid InternshipCreateRequest dto) {
        internshipService.createInternship(dto);
    }

    @PutMapping("/")
    public void updateInternship(@RequestBody @Valid InternshipUpdateRequest dto) {
        internshipService.updateInternship(dto);
    }

    @PostMapping("/filter")
    public List<InternshipResponse> getInternshipsByFilter(@RequestBody @Valid InternshipFilterRequest filterRequest) {
        return internshipService.getInternshipsByFilter(filterRequest);
    }

    @GetMapping("/all")
    public List<InternshipResponse> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{internshipId}")
    public InternshipResponse getInternshipById(@RequestParam long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }

    @PutMapping("/{internshipId}/remove-interns")
    public void removeInternFromInternship(
            @RequestBody @Valid List<Long> internIds,
            @PathVariable long internshipId) {
        internshipService.removeInternFromInternship(internIds, internshipId);
    }

    @PutMapping("/{internshipId}/finish-early")
    public void finishTheInternshipAheadOfScheduleFor(
            @RequestBody @Valid List<Long> internIds,
            @PathVariable long internshipId) {
        internshipService.finishTheInternshipAheadOfScheduleFor(internIds, internshipId);
    }
}
