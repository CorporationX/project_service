package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.ApiPath;
import faang.school.projectservice.controller.BaseIntegrationTest;
import faang.school.projectservice.dto.DtoValidationConstraints;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest extends BaseIntegrationTest {

    @MockBean
    private ProjectService projectService;

    @Test
    void createProject_should_return_bad_request_if_request_body_is_empty() throws Exception {
        String invalidRequestBody = "{\"name\": \"\"}";

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_NAME_REQUIRED)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_DESCRIPTION_REQUIRED)));
    }

    @Test
    void createProject_should_return_bad_request_if_request_body_parameters_fail_length_constraints() throws Exception {
        var dto = ProjectDto.builder()
                .name("ab")
                .description("a".repeat(501))
                .build();

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_NAME_CONSTRAINT)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_DESCRIPTION_CONSTRAINT)));
    }

    @Test
    void createProject_should_return_status_created_if_request_body_is_valid() throws Exception {
        var dto = ProjectDto.builder()
                .id(123L)
                .ownerId(1L)
                .name("Project Name")
                .description("Project Description")
                .build();

        when(projectService.createProject(any(ProjectDto.class))).thenReturn(dto);

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.ownerId").value(DEFAULT_HEADER_VALUE));
    }

    @Test
    void createProject_should_throw_exception_if_project_already_exists() throws Exception {
        var dto = ProjectDto.builder()
                .id(123L)
                .ownerId(1L)
                .name("Project Name")
                .description("Project Description")
                .build();

        when(projectService.createProject(any(ProjectDto.class))).thenThrow(new IllegalArgumentException(ExceptionMessages.PROJECT_ALREADY_EXISTS_FOR_OWNER_ID));

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessages.PROJECT_ALREADY_EXISTS_FOR_OWNER_ID));
    }
}