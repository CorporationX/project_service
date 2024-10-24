package faang.school.projectservice.validator;

import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import org.springframework.stereotype.Component;

@Component
public interface CampaignValidator {

    void validationCampaignCreator(TeamMemberDto teamMember, ProjectDto projectDto);
}

