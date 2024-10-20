package faang.school.projectservice.model.mapper.donation;

import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.model.entity.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {

    @Mapping(source = "campaignId", target = "campaign.id")
    Donation toEntity(DonationDto donationDto);

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationDto toDto(Donation donation);
}
