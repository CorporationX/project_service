package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {StageInvitationController.class, StageInvitationService.class})
public class StageInvitationControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String REJECT = "rejection reason";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StageInvitationService stageInvitationService;

    private StageInvitationDto stageInvitationDto;
    private StageInvitationFilterDto filterDto;
    private List<StageInvitationDto> testList;
    private long testId;

    @BeforeEach
    public void setUp() {
        testId = 7L;
        stageInvitationDto = StageInvitationDto.builder()
                .id(1L)
                .authorId(2L)
                .invitedId(3L)
                .stageId(4L)
                .description("description")
                .build();

        testList = new ArrayList<>();
        testList.add(stageInvitationDto);

        filterDto = StageInvitationFilterDto.builder()
                .authorId(testId)
                .status(StageInvitationStatus.PENDING)
                .build();
    }

    @Test
    public void testCreateStageInvitationSuccess() {
        when(stageInvitationService.createStageInvitation(any(StageInvitationDto.class)))
                .thenReturn(stageInvitationDto);
        try {
            mockMvc.perform(post("/v1/invitations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(stageInvitationDto)))
                    .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(stageInvitationDto)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail(e);
        }
    }

    private static Stream<Object[]> invalidRequestDto() {
        return Stream.of(
                new Object[]{StageInvitationDto.builder()
                        .authorId(2L)
                        .invitedId(3L)
                        .stageId(4L)
                        .description("description")
                        .build()},
                new Object[]{StageInvitationDto.builder()
                        .id(1L)
                        .invitedId(3L)
                        .stageId(4L)
                        .description("description")
                        .build()},
                new Object[]{StageInvitationDto.builder()
                        .id(1L)
                        .authorId(2L)
                        .stageId(4L)
                        .description("description")
                        .build()},
                new Object[]{StageInvitationDto.builder()
                        .id(1L)
                        .authorId(2L)
                        .invitedId(3L)
                        .description("description")
                        .build()},
                new Object[]{StageInvitationDto.builder()
                        .id(1L)
                        .authorId(2L)
                        .invitedId(3L)
                        .stageId(4L)
                        .description("     ")
                        .build()}
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequestDto")
    public void testCreateStageInvitationFails(StageInvitationDto stageInvitationDto) {
        when(stageInvitationService.createStageInvitation(any(StageInvitationDto.class)))
                .thenReturn(stageInvitationDto);
        try {
            mockMvc.perform(post("/v1/invitations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(stageInvitationDto)))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testAcceptStageInvitationSuccess() {
        when(stageInvitationService.acceptStageInvitation(anyLong()))
                .thenReturn(stageInvitationDto);
        try {
            mockMvc.perform(put("/v1/invitations/acceptance/{stageInvitationId}", testId))
                    .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(stageInvitationDto)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testAcceptStageInvitationFails() {
        when(stageInvitationService.acceptStageInvitation(anyLong()))
                .thenReturn(stageInvitationDto);
        try {
            mockMvc.perform(put("/v1/invitations/acceptance/{stageInvitationId}", "text"))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testRejectStageInvitationSuccess() {
        when(stageInvitationService.rejectStageInvitation(anyLong(), anyString()))
                .thenReturn(stageInvitationDto);
        try {
            mockMvc.perform(put("/v1/invitations/rejection/{participantId}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(REJECT)))
                    .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(stageInvitationDto)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testRejectStageInvitationFails() {
        when(stageInvitationService.rejectStageInvitation(anyLong(), anyString()))
                .thenReturn(stageInvitationDto);
        try {
            mockMvc.perform(put("/v1/invitations/rejection/{participantId}", testId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void getAllInvitationsForOneParticipantSuccess() {
        when(stageInvitationService
                .getAllInvitationsForOneParticipant(anyLong(), any(StageInvitationFilterDto.class)))
                .thenReturn(testList);
        try {
            mockMvc.perform(post("/v1/invitations/participant/{participantId}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(filterDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(testList)));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void getAllInvitationsForOneParticipantFails() {
        when(stageInvitationService
                .getAllInvitationsForOneParticipant(anyLong(), any(StageInvitationFilterDto.class)))
                .thenReturn(testList);
        try {
            mockMvc.perform(post("/v1/invitations/participant/{participantId}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(" ")))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e);
        }
    }
}
