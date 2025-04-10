package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {TeamController.class, TeamService.class})
@WebMvcTest
public class TeamControllerTest {
    private final String REQUEST_URL = "/teams/avatar/{id}";
    @MockBean
    private TeamService teamService;
    @MockBean
    private UserContext userContext;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPositiveUpload() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "some content".getBytes());

        mockMvc.perform(multipart(REQUEST_URL, 1) // Указываем id
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositiveDelete() throws Exception {
        when(userContext.getUserId()).thenReturn(1L);

        mockMvc.perform(delete(REQUEST_URL, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
