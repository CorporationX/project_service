package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.dto.sub_project.SubProjectUpdateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.SubProjectServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SubProjectController — тестирует функциональность контроллера {@link SubProjectController}.
 *
 * @author Linempy
 * @since 23.07.2025
 */
@WebMvcTest(SubProjectController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {SubProjectController.class, SubProjectMapperImpl.class})
public class SubProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubProjectServiceImpl service;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objMapper;

    @Test
    @DisplayName("")
    public void createSuccessful() throws Exception {
        SubProjectCreateDto createDto = new SubProjectCreateDto(
                1L, "Name", "Desc", ProjectVisibility.PRIVATE, null
        );

        SubProjectViewDto expectedDto = new SubProjectViewDto(
                1L, createDto.name(), createDto.description(), createDto.visibility(),
                ProjectStatus.CREATED, null, null, null, null
        );

        when(service.create(any(SubProjectCreateDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/subprojects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedDto.id()))
                .andDo(print());

    }

    @Test
    @DisplayName("Успешно обновляет статус и видимость")
    public void updateSuccessful() throws Exception {
        long id = 1L;
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);

        SubProjectViewDto excepted = new SubProjectViewDto(
                id, "name", "desc", updateDto.visibility(), updateDto.status(),
                null, List.of(), null, null
        );

        when(service.update(id, updateDto)).thenReturn(excepted);

        mockMvc.perform(put("/subprojects/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"));
    }

    @Test
    @DisplayName("Возвращает 200 и отфильтрованный список DTO")
    public void getByFilterSuccessful() throws Exception {
        Long parentId = 1L;
        SubProjectFilterDto filterDto = new SubProjectFilterDto("name", ProjectStatus.IN_PROGRESS);

        SubProjectViewDto excepted = new SubProjectViewDto(
                2L, "name", "desc", ProjectVisibility.PUBLIC, ProjectStatus.IN_PROGRESS,
                null, List.of(), null, null
        );

        when(service.getByFilter(parentId, filterDto)).thenReturn(List.of(excepted));

        mockMvc.perform(get("/subprojects/" + parentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "name")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));
    }
}