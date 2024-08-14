package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CampaignMapper {

    @Mapping(source = "project.id", target = "projectId")
    CampaignDto toDto (Campaign campaign);

    @Mapping(source = "projectId", target = "project.id")
    Campaign toEntity(CampaignDto campaignDto);

    List<CampaignDto> toDtoList(List<Campaign> campaigns);

    Campaign updateCampaign(CampaignUpdateDto campaignUpdateDto, @MappingTarget Campaign campaign);
}
