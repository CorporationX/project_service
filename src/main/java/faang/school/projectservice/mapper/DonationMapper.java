package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {
    @Mapping(source = "campaign.id", target = "campaignId")
    DonationDto toDto(Donation donation);

    @Mapping(target = "campaign", ignore = true)
    Donation toEntity(DonationDto donationDto);
}
