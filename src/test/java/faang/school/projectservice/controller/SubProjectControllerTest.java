package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SubProjectController — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
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
    private ProjectServiceImpl service;

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
}