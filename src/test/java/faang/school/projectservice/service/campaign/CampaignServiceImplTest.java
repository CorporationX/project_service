package faang.school.projectservice.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.filter.campaign.CampaignFilter;
import faang.school.projectservice.model.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.mapper.campaign.CampaignMapperImpl;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.campaign.CampaignValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserContext userContext;
    @Mock
    private CampaignValidator campaignValidator;
    @Spy
    private CampaignMapperImpl campaignMapper;
    @Mock
    private List<CampaignFilter> filters;

    @Test
    void test(){

    }
}
