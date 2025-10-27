package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/internships")
public class InternshipController {

private final InternshipService internshipService;

@PostMapping
public InternshipDto createInternship(@RequestParam Long projectId,
                                      @Valid @RequestBody CreateInternshipDto internshipDto) {

    return internshipService.createInternship(projectId, internshipDto);
}

@PutMapping("/{internshipId}")
public InternshipDto updateInternship(@NotNull Long internshipId, @Valid @RequestBody UpdateInternshipDto internshipDto) {
    return internshipService.updateInternship(internshipId, internshipDto);
}

@GetMapping("/filter")
public List<InternshipDto> getByFilter(SearchDto searchDto) {
    return internshipService.findInternships(searchDto);
}

@GetMapping("/{internshipId}")
public InternshipDto getById(@NotNull Long internshipId) {
    return internshipService.findById(internshipId);
}

@GetMapping
public List<InternshipDto> getAllInternships() {
    return internshipService.findAll();
}
}
