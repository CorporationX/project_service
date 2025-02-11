package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {

    @Mapping(source = "campaignId", target = "campaign.id")
    @Mapping(source = "userId", target = "userId")
    Donation toEntity(DonationDto donationDto);

    @Mapping(source = "campaign.id", target = "campaignId")
    @Mapping(source = "userId", target = "userId")
    DonationDto toDto(Donation donation);
}
