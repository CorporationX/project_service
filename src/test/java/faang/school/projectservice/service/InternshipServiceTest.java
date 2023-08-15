package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @Spy
    private InternshipMapperImpl mapper;

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectJpaRepository projectRepository;

    @InjectMocks
    private InternshipService service;

    @Test
    void create_MentorWasNotFound_ShouldThrowException() {
        Mockito.when(teamMemberRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException e = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.create(buildInternshipDto(), 1L);
        });
        Assertions.assertEquals("Team member doesn't exist by id: 1", e.getMessage());
    }

    @Test
    void create_ProjectWasNotFound_ShouldThrowException() {
        Mockito.when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(TeamMember.builder().id(1L).build()));
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException e = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.create(buildInternshipDto(), 1L);
        });
        Assertions.assertEquals("Project not found by id: 1", e.getMessage());
    }

    @Test
    void create_ShouldMapCorrectlyToEntity() {
        Assertions.assertEquals(buildExpectedInternship(), mapper.toEntity(buildInternshipDto()));
    }

    @Test
    void create_ShouldBeSavedAndMapToDtoCorrectly() {
        Mockito.when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(TeamMember.builder().id(1L).build()));
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(Project.builder().id(1L).build()));

        InternshipDto actual = service.create(buildInternshipDto(), 2L);

        Assertions.assertEquals(buildExpectedInternshipDto(), actual);
        Mockito.verify(internshipRepository).save(Mockito.any(Internship.class));
    }

    private InternshipDto buildInternshipDto() {
        return InternshipDto.builder()
                .mentorId(1L)
                .projectId(1L)
                .build();
    }

    private Internship buildExpectedInternship() {
        return Internship.builder()
                .mentor(TeamMember.builder().id(1L).build())
                .project(Project.builder().id(1L).build())
                .build();
    }

    private InternshipDto buildExpectedInternshipDto() {
        return InternshipDto.builder()
                .mentorId(1L)
                .projectId(1L)
                .internIds(Collections.emptyList())
                .status(InternshipStatus.IN_PROGRESS)
                .build();
    }
}
