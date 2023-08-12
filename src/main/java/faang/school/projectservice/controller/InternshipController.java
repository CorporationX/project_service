package faang.school.projectservice.controller;


import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validation.RequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name="Internships", description = "")
@RestController
@RequestMapping("qpi/v1/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;
    private final RequestValidator requestValidator;

    @Operation(
            summary = "",
            tags = { "internships", "get" })
    @GetMapping
    public ResponseEntity<List<InternshipDto>> getAllInternships() {
        List<InternshipDto> internships = internshipService.getAllInternships();
        return ResponseEntity.ok(internships);
    }

    @Operation(
            summary = "",
            tags = { "internships", "get" })
    @GetMapping("/{internshipId}")
    public ResponseEntity<InternshipDto> getInternshipBy(@PathVariable Long internshipId) {
        requestValidator.validate(internshipId);
        InternshipDto internship = internshipService.getInternshipBy(internshipId);
        return ResponseEntity.ok(internship);
    }
}
