package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserHeaderFilter;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserHeaderFilter userHeaderFilter;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Проверка успешного создания проекта через контроллер")
    void shouldCreateSuccessfully() throws Exception {
        ProjectCreateDto createDto = new ProjectCreateDto(
                "someProject",
                "someDescription",
                new BigInteger("4934823"),
                new BigInteger("654654645646546456"),
                1L,
                new Project(),
                ProjectVisibility.PUBLIC,
                ProjectStatus.CREATED,
                "randomText",
                new ArrayList<>(List.of("randomText"))
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного обновления проекта через контроллер")
    void shouldUpdateSuccessfully() throws Exception {
        ProjectUpdateDto updateDto = new ProjectUpdateDto(
                "someProject",
                "someDescription",
                new BigInteger("4934823"),
                new BigInteger("654654645646546456"),
                1L,
                new Project(),
                ProjectVisibility.PUBLIC,
                ProjectStatus.CREATED,
                "randomText",
                List.of("randomText")
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного получения отфильтрованных проектов через контроллер")
    void getProjectsByFilterTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/projects"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного получения проекта по id")
    void getProjectByIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/projects/1"))
                .andExpect(status().isOk());
    }
}
