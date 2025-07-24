package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserHeaderFilter;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

    @Test
    @DisplayName("Проверка успешного создания проекта через контроллер")
    void shouldCreateSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/projects"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного обновления проекта через контроллер")
    void shouldUpdateSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/projects/1"))
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
