package faang.school.projectservice.controller.campaign;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.Campaign.CampaignDto;
import faang.school.projectservice.exception.DuplicateTitleException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.GlobalExceptionHandler;
import faang.school.projectservice.service.campaign.CampaignService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CampaignController.class)
@ContextConfiguration(classes = {CampaignController.class, GlobalExceptionHandler.class})
class CampaignControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CampaignService campaignService;

    private final long projectId = 1;
    private final long campaignID = 1;
    private final long userID = 1;

    private final String campaignCreateJson = """
                {
                    "title": "A test",
                    "description": "A test",
                    "goal": 1000,
                    "amountRaised": 0,
                    "status": "ACTIVE",
                    "currency": "USD"
                }
            """;

    private final String campaignUpdateJson = """
                {
                    "title": "A test",
                    "description": "A test"
                }
            """;

    @Nested
    class Create {
        @Test
        public void successful() throws Exception {
            CampaignDto returnedDto = new CampaignDto();
            returnedDto.setId(123L);

            when(campaignService.create(any(), eq(projectId), eq(userID)))
                    .thenReturn(returnedDto);

            mockMvc.perform(post("/api/v1/campaign/projects/{projectId}/{creatorId}", projectId, userID)
                            .contentType("application/json")
                            .content(campaignCreateJson))
                    .andExpect(status().isCreated());
            verify(campaignService, times(1)).create(any(), eq(projectId), eq(userID));
        }

        @Test
        public void duplicateTitleException() throws Exception {
            doThrow(new DuplicateTitleException("text"))
                    .when(campaignService).create(any(), eq(projectId), eq(userID));

            mockMvc.perform(post("/api/v1/campaign/projects/{projectId}/{creatorId}", projectId, userID)
                            .contentType("application/json")
                            .content(campaignCreateJson))
                    .andExpect(status().isConflict());

            verify(campaignService, times(1)).create(any(), eq(projectId), eq(userID));
        }

        @Test
        public void notFoundException() throws Exception {
            doThrow(new NotFoundException("text"))
                    .when(campaignService).create(any(), eq(projectId), eq(userID));

            mockMvc.perform(post("/api/v1/campaign/projects/{projectId}/{creatorId}", projectId, userID)
                            .contentType("application/json")
                            .content(campaignCreateJson))
                    .andExpect(status().isNotFound());

            verify(campaignService, times(1)).create(any(), eq(projectId), eq(userID));
        }
    }

    @Nested
    class Update {
        @Test
        public void successful() throws Exception {
            when(campaignService.update(any(), eq(projectId), eq(campaignID), eq(userID)))
                    .thenReturn(new CampaignDto());

            mockMvc.perform(put("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}/{updaterId}", projectId, campaignID, userID)
                            .contentType("application/json")
                            .content(campaignUpdateJson))
                    .andExpect(status().isOk());
            verify(campaignService, times(1)).update(any(), eq(projectId), eq(campaignID), eq(userID));
        }

        @Test
        public void notFoundException() throws Exception {
            doThrow(new NotFoundException("text"))
                    .when(campaignService).update(any(), eq(projectId), eq(campaignID), eq(userID));

            mockMvc.perform(put("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}/{updaterId}", projectId, campaignID, userID)
                            .contentType("application/json")
                            .content(campaignUpdateJson))
                    .andExpect(status().isNotFound());

            verify(campaignService, times(1)).update(any(), eq(projectId), eq(campaignID), eq(userID));
        }

        @Test
        public void illegalArgumentException() throws Exception {
            doThrow(new IllegalArgumentException("text"))
                    .when(campaignService).update(any(), eq(projectId), eq(campaignID), eq(userID));

            mockMvc.perform(put("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}/{updaterId}", projectId, campaignID, userID)
                            .contentType("application/json")
                            .content(campaignUpdateJson))
                    .andExpect(status().isBadRequest());

            verify(campaignService, times(1)).update(any(), eq(projectId), eq(campaignID), eq(userID));
        }

        @Test
        public void forbiddenException() throws Exception {
            doThrow(new ForbiddenException("text"))
                    .when(campaignService).update(any(), eq(projectId), eq(campaignID), eq(userID));

            mockMvc.perform(put("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}/{updaterId}", projectId, campaignID, userID)
                            .contentType("application/json")
                            .content(campaignUpdateJson))
                    .andExpect(status().isForbidden());

            verify(campaignService, times(1)).update(any(), eq(projectId), eq(campaignID), eq(userID));
        }
    }

    @Nested
    class SoftDelete {
        @Test
        public void successful() throws Exception {
            doNothing().when(campaignService).softDelete(eq(projectId), eq(campaignID), eq(userID));
            String expectedMessage = String.format("Campaign with id %d was archived successfully", campaignID);

            mockMvc.perform(delete("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}/{deleterId}", projectId, campaignID, userID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
            verify(campaignService, times(1)).softDelete(eq(projectId), eq(campaignID), eq(userID));
        }

        @Test
        public void notFoundException() throws Exception {
            doThrow(new NotFoundException("text"))
                    .when(campaignService).softDelete(eq(projectId), eq(campaignID), eq(userID));

            mockMvc.perform(delete("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}/{deleterId}", projectId, campaignID, userID))
                    .andExpect(status().isNotFound());

            verify(campaignService, times(1)).softDelete(eq(projectId), eq(campaignID), eq(userID));
        }
    }

    @Nested
    class GetCampaign {
        @Test
        public void successful() throws Exception {
            when(campaignService.getCampaign(eq(projectId), eq(campaignID))).thenReturn(new CampaignDto());

            mockMvc.perform(get("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}", projectId, campaignID))
                    .andExpect(status().isOk());

            verify(campaignService, times(1)).getCampaign(eq(projectId), eq(campaignID));
        }

        @Test
        public void notFoundException() throws Exception {
            doThrow(new NotFoundException("text"))
                    .when(campaignService).getCampaign(eq(projectId), eq(campaignID));

            mockMvc.perform(get("/api/v1/campaign/projects/{projectId}/campaigns/{campaignId}", projectId, campaignID))
                    .andExpect(status().isNotFound());

            verify(campaignService, times(1)).getCampaign(eq(projectId), eq(campaignID));
        }
    }

    @Nested
    class GetCampaignByFilter {
        @Test
        public void successful() throws Exception {
            when(campaignService.getCampaignByFilter(eq(projectId), any())).thenReturn(List.of(new CampaignDto()));

            mockMvc.perform(post("/api/v1/campaign/projects/{projectId}", projectId)
                            .contentType("application/json")
                            .content(campaignUpdateJson))
                    .andExpect(status().isOk());

            verify(campaignService, times(1)).getCampaignByFilter(eq(projectId), any());
        }

        @Test
        public void notFoundException() throws Exception {
            doThrow(new NotFoundException("text"))
                    .when(campaignService).getCampaignByFilter(eq(projectId), any());

            mockMvc.perform(post("/api/v1/campaign/projects/{projectId}", projectId, campaignID)
                            .contentType("application/json")
                            .content(campaignUpdateJson))
                    .andExpect(status().isNotFound());

            verify(campaignService, times(1)).getCampaignByFilter(eq(projectId), any());
        }
    }
}