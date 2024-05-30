package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationToSendDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {

    Donation toEntity(DonationToSendDto donationToSendDto);

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationDto toDto(Donation donation);

    @Mapping(source = "paymentNumber", target = "paymentNumber")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "currency", target = "currency")
    PaymentRequest toPaymentRequest(Donation donation);
}