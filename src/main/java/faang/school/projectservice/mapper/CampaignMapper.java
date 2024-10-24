package faang.school.projectservice.mapper;

import faang.school.projectservice.model.dto.CampaignDto;
import faang.school.projectservice.model.entity.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {

    @Mapping(source = "project.id", target = "projectId")
    CampaignDto toDto(Campaign campaign);

    @Mapping(source = "projectId", target = "project.id")
    Campaign toEntity(CampaignDto campaignDto);

    List<CampaignDto> toDtoList(List<Campaign> campaigns);

    List<Campaign> toEntityList(List<CampaignDto> campaignDtos);
}
