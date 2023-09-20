package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignGetDto;
import faang.school.projectservice.dto.campaign.CampaignUpdatedDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {

    @Mapping(source = "project.id", target = "projectId")
    CampaignGetDto toDto(Campaign campaign);

    @Mapping(source = "projectId", target = "project.id")
    Campaign toEntity(CampaignDto campaignDto);

    void updateCampaign(CampaignUpdatedDto campaignDto, @MappingTarget Campaign campaign);

    List<CampaignGetDto> toDtoList(List<Campaign> campaigns);
}
