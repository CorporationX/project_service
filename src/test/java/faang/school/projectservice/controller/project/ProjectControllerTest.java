package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.ApiPath;
import faang.school.projectservice.controller.BaseControllerTest;
import faang.school.projectservice.dto.DtoValidationConstraints;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigInteger;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest extends BaseControllerTest {

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
    void createProject_should_return_bad_request_if_request_body_parameters_fail_constraints() throws Exception {
        var dto = ProjectDto.builder()
                .name("ab")
                .description("a".repeat(5000))
                .storageSize(BigInteger.valueOf(-1L))
                .maxStorageSize(BigInteger.valueOf(-1L))
                .build();

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_NAME_CONSTRAINT)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_DESCRIPTION_CONSTRAINT)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_STORAGE_SIZE_LOWER_LIMIT)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_MAX_STORAGE_SIZE_LOWER_LIMIT)));
    }

    @Test
    void createProject_should_return_bad_request_if_numeric_parameters_are_too_high() throws Exception {
        var dto = ProjectDto.builder()
                .name("aasfasgagb")
                .description("asdghkjwnegjnosdnousdogeg")
                .storageSize(BigInteger.valueOf(2000L))
                .maxStorageSize(BigInteger.valueOf(2000L))
                .build();

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_MAX_STORAGE_SIZE_UPPER_LIMIT)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_STORAGE_SIZE_UPPER_LIMIT)));
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

    @Test
    void createProject_should_throw_exception_if_project_failed_to_be_persisted() throws Exception {
        var dto = ProjectDto.builder()
                .id(123L)
                .ownerId(1L)
                .name("Project Name")
                .description("Project Description")
                .build();

        when(projectService.createProject(any(ProjectDto.class))).thenThrow(new PersistenceException(ExceptionMessages.PROJECT_FAILED_PERSISTENCE));

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessages.PROJECT_FAILED_PERSISTENCE));
    }

    @Test
    void updateProject_should_return_bad_request_if_request_body_is_empty() throws Exception {
        var updateDto = ProjectUpdateDto.builder()
                .name("12")
                .description("1252")
                .build();

        mockMvc.perform(put(ApiPath.PROJECTS_PATH + "/1")
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_NAME_CONSTRAINT)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_DESCRIPTION_CONSTRAINT)));
    }

    @Test
    void updateProject_should_return_bad_request_if_numeric_values_are_not_valid() throws Exception {
        var updateDto = ProjectUpdateDto.builder()
                .name("Project Name")
                .description("Project Description")
                .storageSize(BigInteger.valueOf(2000L))
                .maxStorageSize(BigInteger.valueOf(2000L))
                .build();

        mockMvc.perform(put(ApiPath.PROJECTS_PATH + "/1")
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_MAX_STORAGE_SIZE_UPPER_LIMIT)))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.PROJECT_STORAGE_SIZE_UPPER_LIMIT)));
    }

    @Test
    void updateProject_should_return_ok_status_if_request_body_is_valid() throws Exception {
        var incomingDto = ProjectUpdateDto.builder()
                .name("Project Name")
                .description("Project Description")
                .build();

        var updatedDto = ProjectDto.builder()
                .id(124L)
                .name("Project Name")
                .description("Project Description")
                .build();

        when(projectService.updateProject(1L, incomingDto)).thenReturn(updatedDto);

        mockMvc.perform(put(ApiPath.PROJECTS_PATH + "/1")
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(124L))
                .andExpect(jsonPath("$.name").value("Project Name"))
                .andExpect(jsonPath("$.description").value("Project Description"));
    }

    @Test
    void updateProject_should_return_not_found_status_if_project_does_not_exist() throws Exception {
        var incomingDto = ProjectUpdateDto.builder()
                .name("Project Name")
                .description("Project Description")
                .build();

        when(projectService.updateProject(1L, incomingDto)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(put(ApiPath.PROJECTS_PATH + "/1")
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isNotFound());
    }
}