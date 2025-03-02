package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/internship")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipMapper internshipMapper;
    private final InternshipService internshipService;

    @PostMapping
    public ResponseEntity<InternshipDto> create(@RequestBody @Valid InternshipDto internshipDto) {
        Internship createdInternship = internshipService.createInternship(internshipDto);

        return ResponseEntity
                .created(URI.create("/internship/" + createdInternship.getId()))
                .body(internshipMapper.toDto(createdInternship));
    }
}
