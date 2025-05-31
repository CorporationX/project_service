package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DonationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    Donation toEntity(DonationDto donationDto);

    @Mapping(target = "campaignId", expression = "java(getCampaignId(donation))")
    DonationDto toDto(Donation donation);

    default Long getCampaignId(Donation donation) {
        if (donation.getCampaign() == null) {
            return null;
        }

        return donation.getCampaign().getId();
    }
}
