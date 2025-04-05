package faang.school.projectservice.controller.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/internships")
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipMapper internshipMapper;
    private final InternshipService internshipService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<InternshipDto> create(@RequestBody @Valid InternshipDto internshipDto,
                                                @RequestHeader("x-user-id") String userId) {
        userContext.setUserId(Long.parseLong(userId));
        Internship createdInternship = internshipService.createInternship(internshipDto);

        return ResponseEntity
                .created(URI.create("/internship/" + createdInternship.getId()))
                .body(internshipMapper.toDto(createdInternship));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternshipDto> getById(@RequestParam Long id) {
        Internship internship = internshipService.getById(id);
        return ResponseEntity.ok(internshipMapper.toDto(internship));
    }

    @GetMapping
    public ResponseEntity<List<InternshipDto>> getAll(@RequestParam(required = false) InternshipStatus status) {
        List<Internship> internships = internshipService.getAll(status);
        return ResponseEntity.ok(internshipMapper.toDtoList(internships));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternshipDto> update(@PathVariable Long id,
                                                @RequestBody @Valid InternshipUpdateDto internshipUpdateDto) {
        log.info("id = {}, internshipUpdateDto = {}", id, internshipUpdateDto);
        Internship updatedInternship = internshipService.updateInternship(id, internshipUpdateDto);
        return ResponseEntity.ok(internshipMapper.toDto(updatedInternship));
    }
}
