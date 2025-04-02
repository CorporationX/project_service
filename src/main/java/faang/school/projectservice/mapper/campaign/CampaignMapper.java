package faang.school.projectservice.mapper.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    CampaignDto toDto(Campaign campaign);
}
