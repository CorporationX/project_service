package faang.school.projectservice.mapper.campaign;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {
    @Mapping(target = "projectId", source = "project.id")
    CampaignDto toDto(Campaign entity);

    List<CampaignDto> toDto(List<Campaign> entities);

    Campaign toEntity(CampaignCreateDto createDto);
}
