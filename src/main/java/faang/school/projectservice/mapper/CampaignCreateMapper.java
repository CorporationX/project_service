package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CampaignCreateDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignCreateMapper {
    CampaignCreateDto toDto(Campaign entity);
    Campaign toEntity(CampaignCreateDto dto);
}
