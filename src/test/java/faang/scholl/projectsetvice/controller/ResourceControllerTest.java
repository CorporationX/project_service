package faang.scholl.projectsetvice.controller;

import faang.school.projectservice.controller.ResourceController;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {

    private static final Long TEAM_ID = 1L;
    private static final Long USER_ID = 2L;

    private MockMvc mockMvc;

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

    @Test
    public void testUploadAvatarForTeam_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "some image data".getBytes());

        ResourceDto resourceDto = ResourceDto.builder()
                .id(1L)
                .name("avatar.jpg")
                .createdBy(USER_ID)
                .build();

        when(resourceService.uploadAvatarForTeam(anyLong(), any())).thenReturn(resourceDto);

        mockMvc.perform(multipart("/resources/teams/{teamId}/avatar", TEAM_ID)
                        .file(file)
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Avatar for team successful upload"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("avatar.jpg"))
                .andExpect(jsonPath("$.data.createdBy").value(USER_ID));

        verify(resourceService, times(1)).uploadAvatarForTeam(TEAM_ID, file);
    }

    @Test
    public void testDeleteAvatarForTeam_Success() throws Exception {
        doNothing().when(resourceService).deleteAvatarForTeam(TEAM_ID);

        mockMvc.perform(delete("/resources/teams/{teamId}/avatar", TEAM_ID)
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar successful deleted for team with id " + TEAM_ID));

        verify(resourceService, times(1)).deleteAvatarForTeam(TEAM_ID);
    }

}
