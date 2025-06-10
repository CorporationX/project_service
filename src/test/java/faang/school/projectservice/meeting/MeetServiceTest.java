package faang.school.projectservice.meeting;

import faang.school.projectservice.dto.meeting.MeetDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.ParticipantNotFoundException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.meeting.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {

    @Mock
    MeetRepository meetRepository;
    @Mock
    MeetMapper meetMapper;

    @InjectMocks
    MeetService meetService;

    MeetDto dto;
    Meet entity;
    Project project;

    @BeforeEach
    void init() {
        project = new Project();
        project.setId(1L);

        dto = new MeetDto();
        dto.setProjectId(project.getId());
        dto.setTitle("Title");
        dto.setDescription("Desc");
        dto.setScheduledAt(LocalDateTime.now().plusDays(1));

        entity = new Meet();
        entity.setId(10L);
        entity.setCreatorId(2L);
        entity.setProject(project);
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setScheduledAt(dto.getScheduledAt());
        entity.setUserIds(new ArrayList<>(List.of(2L, 3L, 5L)));

    }

    @Test
    void create_ok() {
        long creator = 5L;
        when(meetMapper.toEntity(dto)).thenReturn(entity);
        when(meetRepository.save(any(Meet.class))).thenAnswer(i -> {
            Meet m = i.getArgument(0);
            m.setId(10L);
            return m;
        });
        when(meetMapper.toDto(any(Meet.class))).thenReturn(new MeetDto());

        MeetDto res = meetService.create(creator, dto);

        assertThat(res).isNotNull();
        verify(meetRepository).save(any(Meet.class));
    }

    @Test
    void cancel_ok() {
        long meetId = 3L;
        long creator = 2L;
        entity.setId(meetId);
        entity.setCreatorId(creator);
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(entity));
        when(meetRepository.save(entity)).thenReturn(entity);
        when(meetMapper.toDto(entity)).thenReturn(new MeetDto());

        meetService.cancel(meetId, creator);

        assertThat(entity.getStatus()).isEqualTo(MeetStatus.CANCELLED);
        assertThat(entity.isActive()).isFalse();
    }

    @Test
    void cancel_wrongUser() {
        entity.setId(7L);
        entity.setCreatorId(1L);
        when(meetRepository.findById(7L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> meetService.cancel(7L, 9L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void removeParticipant_ok() {
        when(meetRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(meetRepository.save(entity)).thenReturn(entity);
        MeetDto dto = new MeetDto();
        when(meetMapper.toDto(entity)).thenReturn(dto);

        MeetDto result = meetService.removeParticipant(10L, 2L, 3L);

        assertThat(result).isSameAs(dto);
        assertThat(entity.getUserIds()).doesNotContain(3L);
        verify(meetRepository).save(entity);
    }

    @Test
    void removeParticipant_wrongCreator() {
        when(meetRepository.findById(10L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> meetService.removeParticipant(10L, 99L, 3L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void removeParticipant_notPresent() {
        when(meetRepository.findById(10L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> meetService.removeParticipant(10L, 2L, 99L))
                .isInstanceOf(ParticipantNotFoundException.class);
    }

    @Test
    void update_ok() {
        long meetId = 8L;
        long creator = 4L;
        entity.setId(meetId);
        entity.setCreatorId(creator);
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(entity));
        when(meetRepository.save(entity)).thenReturn(entity);
        when(meetMapper.toDto(entity)).thenReturn(new MeetDto());

        MeetDto patch = new MeetDto();
        patch.setTitle("New");
        patch.setDescription("NewDesc");
        patch.setScheduledAt(LocalDateTime.now().plusDays(2));

        meetService.update(meetId, creator, patch);

        assertThat(entity.getTitle()).isEqualTo("New");
        assertThat(entity.getDescription()).isEqualTo("NewDesc");
    }

    @Test
    void findById_notFound() {
        when(meetRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> meetService.findById(100L))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void filter_projectAndTitle() {
        Meet m1 = new Meet();
        m1.setProject(project);
        m1.setTitle("demo one");
        m1.setScheduledAt(LocalDateTime.now().plusDays(1));

        Project p2 = new Project();
        p2.setId(99L);
        Meet m2 = new Meet();
        m2.setProject(p2);
        m2.setTitle("demo two");
        m2.setScheduledAt(LocalDateTime.now().plusDays(1));

        when(meetRepository.findAll()).thenReturn(List.of(m1, m2));
        when(meetMapper.toDtoList(Collections.singletonList(m1))).thenReturn(List.of(new MeetDto()));

        List<MeetDto> res = meetService.findProjectMeets(project.getId(), Optional.of("demo"), Optional.empty(), Optional.empty());

        assertThat(res).hasSize(1);
    }
}
