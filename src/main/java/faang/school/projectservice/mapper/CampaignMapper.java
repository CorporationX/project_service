package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {
    Campaign toEntity(CreateCampaignDto createCampaignDto);

    @Mapping(target = "projectId", source = "project.id")
    CampaignDto toCampaignDto(Campaign campaign);
}
