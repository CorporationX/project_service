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
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest extends BaseControllerTest {

    @MockBean
    private ProjectService projectService;

    @Test
    void createProjectShouldReturnBadRequestIfRequestBodyIsEmpty() throws Exception {
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
    void createProjectShouldReturnBadRequestIfRequestBodyParametersFailConstraints() throws Exception {
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
    void createProjectShouldReturnBadRequestIfNumericParametersAreTooHigh() throws Exception {
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
    void createProjectShouldReturnStatusCreatedIfRequestBodyIsValid() throws Exception {
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
    void createProjectShouldThrowExceptionIfProjectAlreadyExists() throws Exception {
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
    void createProjectShouldThrowExceptionIfProjectFailedToBePersisted() throws Exception {
        var dto = ProjectDto.builder()
                .id(123L)
                .ownerId(1L)
                .name("Project Name")
                .description("Project Description")
                .build();

        when(projectService.createProject(any(ProjectDto.class))).thenThrow(new PersistenceException(ExceptionMessages.FAILED_PERSISTENCE));

        mockMvc.perform(post(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessages.FAILED_PERSISTENCE));
    }

    @Test
    void updateProjectShouldReturnBadRequestIfRequestBodyIsEmpty() throws Exception {
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
    void updateProjectShouldReturnBadRequestIfNumericValuesAreNotValid() throws Exception {
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
    void updateProjectShouldReturnOkStatusIfRequestBodyIsValid() throws Exception {
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
    void updateProjectShouldReturnNotFoundStatusIfProjectDoesNotExist() throws Exception {
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

    @Test
    void getProjectShouldReturnNotFoundStatusIfProjectDoesNotExist() throws Exception {
        when(projectService.retrieveProject(1L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(ApiPath.PROJECTS_PATH + "/1")
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjectShouldReturnProjectIfProjectExists() throws Exception {
        var projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project Name")
                .description("Project Description")
                .build();

        when(projectService.retrieveProject(1L)).thenReturn(projectDto);

        mockMvc.perform(get(ApiPath.PROJECTS_PATH + "/1")
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Project Name"))
                .andExpect(jsonPath("$.description").value("Project Description"));
    }

    @Test
    void getProjectsShouldReturnEmptyListIfNoProjectsExist() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of());

        mockMvc.perform(get(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getProjectsShouldReturnListOfProjectsIfProjectsExist() throws Exception {
        var projects = List.of(
                ProjectDto.builder().id(1L).name("Project Name").description("Project Description").build(),
                ProjectDto.builder().id(2L).name("Project Name 2").description("Project Description 2").build(),
                ProjectDto.builder().id(3L).name("Project Name 3").description("Project Description 3").build()
        );

        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getProjectsShouldReturnBadRequestIfDatabaseErrorOccurs() throws Exception {
        when(projectService.getAllProjects()).thenThrow(new PersistenceException(ExceptionMessages.FAILED_RETRIEVAL));

        mockMvc.perform(get(ApiPath.PROJECTS_PATH)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessages.FAILED_RETRIEVAL));
    }

    @Test
    void filterProjectsShouldReturnEmptyListIfNoProjectsExist() throws Exception {
        when(projectService.filterProjects(any())).thenReturn(List.of());

        mockMvc.perform(post(ApiPath.PROJECTS_PATH + ApiPath.FILTER_FUNCTIONALITY)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void filterProjectsShouldReturnListOfProjectsIfProjectsExist() throws Exception {
        var projects = List.of(
                ProjectDto.builder().id(1L).name("Project Name").description("Project Description").build(),
                ProjectDto.builder().id(2L).name("Project Name 2").description("Project Description 2").build(),
                ProjectDto.builder().id(3L).name("Project Name 3").description("Project Description 3").build()
        );

        when(projectService.filterProjects(any())).thenReturn(projects);

        mockMvc.perform(post(ApiPath.PROJECTS_PATH + ApiPath.FILTER_FUNCTIONALITY)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void filterProjectsShouldReturnBadRequestIfDatabaseErrorOccurs() throws Exception {
        when(projectService.filterProjects(any())).thenThrow(new PersistenceException(ExceptionMessages.FAILED_RETRIEVAL));

        mockMvc.perform(post(ApiPath.PROJECTS_PATH + ApiPath.FILTER_FUNCTIONALITY)
                        .header(USER_HEADER, DEFAULT_HEADER_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessages.FAILED_RETRIEVAL));
    }
}