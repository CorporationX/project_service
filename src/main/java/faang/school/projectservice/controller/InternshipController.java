package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFiltersDto;
import faang.school.projectservice.dto.client.InternshipToCreateDto;
import faang.school.projectservice.dto.client.InternshipToUpdateDto;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internships")
@Data
public class InternshipController {
    private final InternshipService internshipService;
    private final UserContext userContext;

    @PostMapping("/internship")
    public InternshipDto create(@RequestBody @Valid InternshipToCreateDto internshipDto) {
        long userId = userContext.getUserId();
        return internshipService.create(userId, internshipDto);
    }

    @PutMapping("/internship/setting")
    public InternshipDto update(@RequestBody @Valid InternshipToUpdateDto internshipDto) {
        long userId = userContext.getUserId();
        return internshipService.update(userId, internshipDto);
    }

    @GetMapping("/project/{projectId}")
    public List<InternshipDto> getAllInternshipByFilters(@PathVariable Long projectId, @RequestBody InternshipFiltersDto filters) {
        return internshipService.getAllInternshipByFilters(projectId, filters);
    }

    @GetMapping
    public List<InternshipDto> getAllInternship() {
        return internshipService.getAllInternship();
    }

    @GetMapping("/internship/{internshipId}")
    public InternshipDto getInternshipById(@PathVariable Long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}
