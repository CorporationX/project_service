package faang.school.projectservice.team;

import faang.school.projectservice.controller.TeamController;
import faang.school.projectservice.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    private MockMvc mockMvc;
    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
    }

    @Test
    void uploadAvatar_ShouldUploadSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file",
                "avatar.jpg", "image/jpeg", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/v1/team/1/upload/avatar")
                        .file(file)
                        .header("x-user-id", "2"))
                .andExpect(status().isOk());

        verify(teamService, times(1)).uploadAvatar(1L, file, 2L);
    }

    @Test
    void getAvatar_ShouldReturnAvatarBytes() throws Exception {
        byte[] avatarBytes = new byte[]{1, 2, 3};
        when(teamService.getAvatar(1L)).thenReturn(new ByteArrayInputStream(avatarBytes));

        mockMvc.perform(get("/api/v1/team/1/avatar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(avatarBytes));

        verify(teamService, times(1)).getAvatar(1L);
    }

    @Test
    void deleteAvatar_ShouldDeleteSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/team/1/delete/avatar")
                        .header("x-user-id", "2"))
                .andExpect(status().isOk());

        verify(teamService, times(1)).deleteAvatar(1L, 2L);
    }
}
