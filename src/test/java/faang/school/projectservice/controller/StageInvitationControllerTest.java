package faang.school.projectservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StageInvitationControllerTest {
    @InjectMocks
    private StageInvitationController stageInvitationController;
    @Mock
    private StageInvitationService stageInvitationService;
    private MockMvc mockMvc;
    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(stageInvitationController)
                .build();
    }

    @Test
    public void testCreate() throws Exception {
        StageInvitationDto stageInvitationDto = StageInvitationDto
                .builder()
                .stageId(1L)
                .invitedId(1L)
                .authorId(1L)
                .build();
        Mockito.when(stageInvitationService.create(stageInvitationDto))
                .thenReturn(stageInvitationDto);
        mockMvc.perform(post("/invitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageInvitationDto)))
                .andExpect(status().isOk());
    }
}