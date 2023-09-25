package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationDto toDto(Donation donation);

    @Mapping(target = "campaign.id", source = "campaignId")
    Donation toEntity(DonationDto donationDto);
}
