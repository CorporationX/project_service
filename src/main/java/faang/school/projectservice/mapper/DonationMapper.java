package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DonationMapper {

    @Mapping(target = "id", ignore = true)
    Donation toEntity(DonationDto donationDto);

    DonationDto toDto(Donation donation);

    void updateDonationFromDto(DonationDto donationDto, @MappingTarget Donation donation);

    List<DonationDto> toDtoList(List<Donation> donations);
}
