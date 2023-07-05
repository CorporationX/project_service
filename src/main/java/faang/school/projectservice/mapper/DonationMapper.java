package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {
    DonationDto toDto(Donation entity);
    Donation toEntity(DonationDto dto);

    List<DonationDto> toDto(List<Donation> entities);
    List<Donation> toEntity(List<DonationDto> dtos);
}
