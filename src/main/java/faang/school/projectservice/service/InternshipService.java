package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final ProjectJpaRepository projectRepository;
    private final InternshipMapper mapper;

    @Transactional
    public InternshipDto create(InternshipDto dto, long userId) {
        teamMemberRepository.findById(dto.getMentorId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", dto.getMentorId()))); // validation

        projectRepository.findById(dto.getProjectId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Project not found by id: %s", dto.getProjectId()))); // validation


        Internship entity = mapper.toEntity(dto);

        entity.setStatus(InternshipStatus.IN_PROGRESS);
        entity.setCreatedBy(userId);
        entity.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        internshipRepository.save(entity);

        return mapper.toDto(entity);
    }
}
