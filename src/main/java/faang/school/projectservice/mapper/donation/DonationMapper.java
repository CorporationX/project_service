package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {

    Donation toEntity(DonationCreateDto donationCreateDto);

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationDto toDto(Donation donation);

    PaymentRequest toPaymentRequest(Donation donation);
}