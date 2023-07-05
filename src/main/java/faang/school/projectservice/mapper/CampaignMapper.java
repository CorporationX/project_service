package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {
    CampaignDto toDto(Campaign entity);
    Campaign toEntity(CampaignDto dto);

    List<CampaignDto> toDto(List<Campaign> entities);
    List<Campaign> toEntity(List<CampaignDto> dtos);
}
