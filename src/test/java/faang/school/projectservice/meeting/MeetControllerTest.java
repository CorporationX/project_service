package faang.school.projectservice.meeting;

import faang.school.projectservice.controller.meeting.MeetController;
import faang.school.projectservice.dto.meeting.MeetDto;
import faang.school.projectservice.service.meeting.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetControllerTest {
    @Mock
    MeetService meetService;

    @InjectMocks
    MeetController controller;

    MeetDto dto;

    @BeforeEach
    void init() {
        dto = new MeetDto();
        dto.setId(10L);
        dto.setProjectId(1L);
        dto.setTitle("Sprint");
        dto.setDescription("Discuss tasks");
        dto.setScheduledAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void createMeet_returnsDto() {
        when(meetService.create(eq(7L), any(MeetDto.class))).thenReturn(dto);
        MeetDto res = controller.createMeet(7L, dto);
        assertThat(res.getId()).isEqualTo(10L);
        verify(meetService).create(7L, dto);
    }

    @Test
    void getMeet_returnsDto() {
        when(meetService.findById(10L)).thenReturn(dto);
        MeetDto res = controller.getMeet(10L);
        assertThat(res.getTitle()).isEqualTo("Sprint");
    }

    @Test
    void updateMeet_delegatesToService() {
        when(meetService.update(eq(10L), eq(7L), any(MeetDto.class))).thenReturn(dto);
        MeetDto res = controller.updateMeet(10L, 7L, dto);
        assertThat(res).isNotNull();
        verify(meetService).update(10L, 7L, dto);
    }

    @Test
    void cancelMeet_delegatesToService() {
        when(meetService.cancel(10L, 7L)).thenReturn(dto);
        MeetDto res = controller.cancelMeet(10L, 7L);
        assertThat(res.getId()).isEqualTo(10L);
        verify(meetService).cancel(10L, 7L);
    }

    @Test
    void removeParticipant_endpoint_ok() {
        MeetDto expected = new MeetDto();
        when(meetService.removeParticipant(15L, 2L, 5L)).thenReturn(expected);

        MeetDto actual = controller.removeParticipant(15L, 5L, 2L);

        assertThat(actual).isSameAs(expected);
        verify(meetService).removeParticipant(15L, 2L, 5L);
    }

    @Test
    void getAllMeets_returnsList() {
        when(meetService.findAll()).thenReturn(List.of(dto));
        List<MeetDto> res = controller.getMeets();
        assertThat(res).hasSize(1);
    }

    @Test
    void getProjectMeets_filtersByTitle() {
        when(meetService.findProjectMeets(1L, Optional.of("Sprint"), Optional.empty(), Optional.empty()))
                .thenReturn(List.of(dto));
        List<MeetDto> res = controller.getMeetsByMemberId(1L, "Sprint", null, null);
        assertThat(res).hasSize(1);
        verify(meetService).findProjectMeets(1L, Optional.of("Sprint"), Optional.empty(), Optional.empty());
    }
}
