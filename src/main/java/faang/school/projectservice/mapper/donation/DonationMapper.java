package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.campaign.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {
    @Mapping(target = "campaign", source = "campaign", qualifiedByName = "toEntityCampaign")
    Donation toEntity(DonationDto donationDto);

    @Named(value = "toEntityCampaign")
    default Campaign toEntityCampaign(Long id) {
        return Campaign.builder().id(id).build();
    }

    @Mapping(target = "campaign", source = "campaign", qualifiedByName = "toDtoCampaign")
    DonationDto toDto(Donation donation);

    @Named(value = "toDtoCampaign")
    default Long toDtoCampaign(Campaign campaign) {
        return campaign.getId();
    }
}
