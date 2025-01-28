package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {

    @Mapping(source = "projectId", target = "project.id")
    Campaign toEntity(CampaignDto campaignDto);

    @Mapping(source = "project.id", target = "projectId")
    CampaignDto toCampaignDto(Campaign campaign);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Campaign campaign, CampaignUpdateDto updateDto);

    List<CampaignDto> toDtoList(List<Campaign> campaignList);
}
