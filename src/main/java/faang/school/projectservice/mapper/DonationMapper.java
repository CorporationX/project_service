package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationViewDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для {@link Donation} entity.
 */
@Mapper(componentModel = "spring")
public interface DonationMapper {

    @Mapping(target = "campaign", ignore = true)
    Donation toEntity(DonationCreateDto donationCreateDto);

    @Mapping(source = "campaign.id", target = "campaignId")
    DonationViewDto toDto(Donation donation);
}
