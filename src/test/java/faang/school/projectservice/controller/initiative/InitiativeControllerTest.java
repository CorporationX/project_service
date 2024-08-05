package faang.school.projectservice.controller.initiative;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.ApiPath;
import faang.school.projectservice.dto.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.service.initiative.InitiativeService;
import faang.school.projectservice.config.context.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(InitiativeController.class)
public class InitiativeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InitiativeService initiativeService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private InitiativeDto initiativeDto;

    @BeforeEach
    void setUp() {
        initiativeDto = InitiativeDto.builder()
                .id(1L)
                .name("Test Initiative")
                .description("Test Description")
                .curatorId(100L)
                .status("OPEN")
                .projectId(200L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @ParameterizedTest
    @CsvSource({", Initiative name cannot be empty", "'', Initiative name cannot be empty", "'   ', Initiative name cannot be empty"})
    void testCreateInitiativeWithInvalidName(String name) throws Exception {
        InitiativeDto dto = InitiativeDto.builder().name(name).build();

        mockMvc.perform(post(ApiPath.INITIATIVES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({", Initiative name cannot be empty", "'', Initiative name cannot be empty", "'   ', Initiative name cannot be empty"})
    void testUpdateInitiativeWithInvalidName(String name) throws Exception {
        InitiativeDto dto = InitiativeDto.builder().id(1L).name(name).build();

        mockMvc.perform(put(ApiPath.INITIATIVES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateInitiative() throws Exception {
        when(initiativeService.createInitiative(any(InitiativeDto.class))).thenReturn(initiativeDto);

        mockMvc.perform(post(ApiPath.INITIATIVES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initiativeDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateInitiative() throws Exception {
        when(initiativeService.updateInitiative(any(Long.class), any(InitiativeDto.class))).thenReturn(initiativeDto);

        mockMvc.perform(put(ApiPath.INITIATIVES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initiativeDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllInitiativesWithFilter() throws Exception {
        List<InitiativeDto> initiatives = Collections.singletonList(initiativeDto);

        when(initiativeService.getAllInitiativesWithFilter(any(InitiativeFilterDto.class))).thenReturn(initiatives);

        mockMvc.perform(get("/initiatives/filter")
                        .param("status", "OPEN")
                        .param("curatorId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllInitiatives() throws Exception {
        List<InitiativeDto> initiatives = Collections.singletonList(initiativeDto);
        when(initiativeService.getAllInitiatives()).thenReturn(initiatives);

        mockMvc.perform(get(ApiPath.INITIATIVES_PATH))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInitiativeById() throws Exception {
        when(initiativeService.getInitiativeById(anyLong())).thenReturn(initiativeDto);

        mockMvc.perform(get("/initiatives/1"))
                .andExpect(status().isOk());
    }
}
