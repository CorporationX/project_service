package faang.school.projectservice.controller;

import faang.school.projectservice.service.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = TeamController.class)
class TeamControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TeamService teamService;

    private static final String BASE_PATH = "/v1/teams";
    private static final long TEAM_ID = 1;
    private static final String FILE_PARAM_NAME = "file";
    private static final String ORIGINAL_FILENAME = "originalFilename.jpeg";
    private static final byte[] bytes = "image".getBytes();

    @Test
    @DisplayName("200 OK вызов POST /{id}/avatar")
    void positive_whenRequestUploadAvatar_returns200Ok() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(FILE_PARAM_NAME, ORIGINAL_FILENAME, MediaType.IMAGE_JPEG_VALUE, bytes);

        mockMvc.perform(multipart(BASE_PATH + "/{id}/avatar", TEAM_ID)
                                .file(file))
                .andExpect(status().isOk());

        verify(teamService, times(1)).uploadAvatar(TEAM_ID, file);
    }

    @Test
    @DisplayName("200 OK вызов DELETE /{id}/avatar")
    void positive_whenRequestDeleteAvatar_returns200Ok() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/{id}/avatar", TEAM_ID))
                .andExpect(status().isOk());

        verify(teamService, times(1)).deleteAvatar(TEAM_ID);
    }

    @Test
    @DisplayName("400 BadRequest вызов POST /{id}/avatar")
    void negative_whenFileNotPassed_returns400BadRequest() throws Exception {
        mockMvc.perform(multipart(BASE_PATH + "/{id}/avatar", TEAM_ID))
                .andExpect(status().isBadRequest());

        verify(teamService, never()).uploadAvatar(anyLong(), any(MultipartFile.class));
    }
}