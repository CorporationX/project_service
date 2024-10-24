package faang.school.projectservice;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.dto.CampaignDto;
import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.impl.CampaignServiceImpl;
import faang.school.projectservice.validator.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    @Mock
    private UserContext userContext;
    @Mock
    private CampaignValidator validator;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private CampaignMapper campaignMapper;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    private CampaignDto campaignDto;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        campaignDto = new CampaignDto();
        campaignDto.setId(1L);
        campaignDto.setCreatedBy(1L);
        campaignDto.setProjectId(1L);

        campaign = new Campaign();
        campaign.setId(1L);

        when(userContext.getUserId()).thenReturn(1L);
    }

    @Test
    void testCreate_ShouldSaveCampaign() {
        when(campaignMapper.toEntity(campaignDto)).thenReturn(campaign);
        when(campaignRepository.save(campaign)).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

        CampaignDto savedCampaignDto = campaignService.create(campaignDto);

        verify(validator).validateUser(1L);
        verify(validator).validateUserIsCreator(1L, 1L);
        verify(validator).validateProjectExists(1L);
        verify(validator).validateManagerOrOwner(1L, 1L);
        assertNotNull(savedCampaignDto);
    }

    @Test
    void testUpdate_ShouldUpdateCampaign() {
        CampaignDto beforeUpdateCampaignDto = new CampaignDto();
        beforeUpdateCampaignDto.setId(1L);
        beforeUpdateCampaignDto.setCreatedBy(1L);
        beforeUpdateCampaignDto.setProjectId(1L);
        beforeUpdateCampaignDto.setTitle("Different title");

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignMapper.toDto(campaign)).thenReturn(beforeUpdateCampaignDto);

        when(campaignMapper.toEntity(campaignDto)).thenReturn(campaign);
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

        CampaignDto updatedCampaignDto = campaignService.update(1L, campaignDto);

        verify(validator).validateUser(1L);
        verify(validator).validateIdFromPath(1L, campaignDto.getId());
        verify(validator).validateProjectExists(campaignDto.getProjectId());
        verify(validator).validateManagerOrOwner(1L, campaignDto.getProjectId());
        verify(validator).validateCreatorIsTheSame(campaign.getCreatedBy(), campaignDto.getCreatedBy());
        assertNotNull(updatedCampaignDto);
        verify(campaignRepository).save(campaign);
    }

    @Test
    void testDelete_ShouldMarkCampaignAsDeleted() {
        Project project = new Project();
        project.setId(1L);
        campaign.setProject(project);
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        campaignService.delete(1L);

        verify(validator).validateUser(1L);
        verify(validator).validateManagerOrOwner(1L, campaign.getProject().getId());
        assertTrue(campaign.isDeleted());
        verify(campaignRepository).save(campaign);
    }

    @Test
    void testGetById_ShouldReturnCampaign() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

        CampaignDto resultDto = campaignService.getById(1L);

        verify(validator).validateUser(1L);
        assertNotNull(resultDto);
    }

    @Test
    void testGetById_ShouldThrowWhenNotFound() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> campaignService.getById(1L));
    }
}
