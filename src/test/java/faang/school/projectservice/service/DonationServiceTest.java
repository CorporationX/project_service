package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityStatusException;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @InjectMocks
    private DonationService donationService;

    @Spy
    private DonationMapperImpl donationMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignRepository campaignRepository;
    private Campaign campaign;
    private DonationDto donationDto;
    private Donation donation;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(2L);

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setProject(project);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setTitle("Hello ");
        campaign.setDescription("world!");

        donationDto = new DonationDto();
        donationDto.setId(1L);
        donationDto.setCurrency(Currency.getInstance("USD"));
        donationDto.setAmount(BigDecimal.valueOf(100));
        donationDto.setCampaignId(campaign.getId());
        donationDto.setUserId(2L);

        donation = donationMapper.toEntity(donationDto);
    }

    @Test
    public void send_Successful() {
        Mockito.when(campaignRepository.findById(donationDto.getCampaignId()))
                .thenReturn(Optional.of(campaign));

        donationService.send(donationDto);

        Mockito.verify(donationRepository, Mockito.times(1))
                .save(donationMapper.toEntity(donationDto));
    }

    @Test
    public void send_throwException_checkCampaignNotFound() {
        Mockito.when(campaignRepository.findById(donationDto.getCampaignId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class,
                () -> donationService.send(donationDto));
    }

    @Test
    public void send_throwException_checkStatus() {
        Campaign campaign1 = new Campaign();
        campaign1.setId(3L);
        campaign1.setProject(project);
        campaign1.setStatus(CampaignStatus.CANCELED);

        DonationDto donationDto1 = new DonationDto();
        donationDto1.setId(2L);
        donationDto1.setCurrency(Currency.getInstance("EUR"));
        donationDto1.setAmount(BigDecimal.valueOf(100));
        donationDto1.setCampaignId(campaign1.getId());
        donationDto1.setUserId(1L);

        Mockito.when(campaignRepository.findById(donationDto1.getCampaignId()))
                .thenReturn(Optional.of(campaign1));

        Assertions.assertThrows(EntityStatusException.class,
                () -> donationService.send(donationDto1));
    }

    @Test
    public void getDonation_Successful() {
        Mockito.when(donationRepository.findById(donationDto.getId()))
                .thenReturn(Optional.of(donation));

        var some = donationService.getDonation(donationDto.getId());
        Assertions.assertEquals(donationDto, some);
    }

    @Test
    public void getDonation_throwException() {
        Mockito.when(donationRepository.findById(donationDto.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class,
                () -> donationService.getDonation(donationDto.getId()));
    }

    @ParameterizedTest
    @MethodSource("getUserId")
    @DisplayName("get donations by userId")
    public void getDonationsByUserId_Successful(long userId) {
        Donation donation1 = Donation
                .builder()
                .id(2L)
                .userId(2L)
                .currency(Currency.getInstance("EUR"))
                .campaign(campaign)
                .amount(BigDecimal.valueOf(100))
                .build();

        Donation donation2 = Donation
                .builder()
                .id(3L)
                .userId(2L)
                .currency(Currency.getInstance("EUR"))
                .campaign(campaign)
                .amount(BigDecimal.valueOf(100))
                .build();

        List<Donation> donations = List.of(donation, donation1, donation2);
        Mockito.when(donationRepository.findAllByUserId(userId))
                .thenReturn(donations);
        List<DonationDto> donationDtos = donations
                .stream()
                .map(donat -> donationMapper.toDto(donat))
                .toList();

        List<DonationDto> donationsByUserId = donationService.getDonationsByUserId(userId);
        Assertions.assertEquals(donationsByUserId, donationDtos);
    }

    @ParameterizedTest
    @MethodSource("getFilters")
    @DisplayName("useFilter")
    public void getAllByFilter(Currency currency, BigDecimal minAmount, BigDecimal maxAmount, LocalDateTime time) {
        Donation donation1 = Donation
                .builder()
                .currency(Currency.getInstance("USD"))
                .amount(BigDecimal.valueOf(100))
                .donationTime(LocalDateTime.now().minusMonths(1).truncatedTo(ChronoUnit.MINUTES))
                .build();
        Donation donation2 = Donation
                .builder()
                .currency(Currency.getInstance("EUR"))
                .amount(BigDecimal.valueOf(150))
                .donationTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        Donation donation3 = Donation
                .builder()
                .currency(Currency.getInstance("STD"))
                .amount(BigDecimal.valueOf(200))
                .donationTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        Donation donation4 = Donation
                .builder()
                .currency(Currency.getInstance("EUR"))
                .amount(BigDecimal.valueOf(70))
                .donationTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        Donation donation5 = Donation
                .builder()
                .currency(Currency.getInstance("RUB"))
                .amount(BigDecimal.valueOf(700))
                .donationTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();

        List<Donation> donations = List.of(donation1, donation2, donation3, donation4, donation5);
        Mockito.lenient().when(donationRepository.findAllByFilters(currency, minAmount, maxAmount, time, Pageable.unpaged()))
                .thenReturn(donations);
        List<DonationDto> actual = donationService.getAllByFilter(currency, minAmount, maxAmount, time);
        Assertions.assertEquals(donations.size(), actual.size());
        Assertions.assertEquals(donationMapper.toDto(donation1), actual.get(0));
    }

    static Stream<Arguments> getFilters() {
        return Stream.of(
                Arguments.of(Currency.getInstance("USD"), BigDecimal.valueOf(100), BigDecimal.valueOf(1000), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("EUR"), BigDecimal.valueOf(70), BigDecimal.valueOf(700), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("EUR"), BigDecimal.valueOf(0), BigDecimal.valueOf(100), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("USD"), BigDecimal.valueOf(120), BigDecimal.valueOf(700), LocalDateTime.now().minusMonths(1).truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("STD"), BigDecimal.valueOf(10), BigDecimal.valueOf(150), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("RUB"), BigDecimal.valueOf(50), BigDecimal.valueOf(150), LocalDateTime.now().minusMonths(2).truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("RUB"), BigDecimal.valueOf(700), BigDecimal.valueOf(1000), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("USD"), BigDecimal.valueOf(480), BigDecimal.valueOf(1500), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("RUB"), BigDecimal.valueOf(0), BigDecimal.valueOf(1000), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("STD"), BigDecimal.valueOf(70), BigDecimal.valueOf(100), LocalDateTime.now().minusMonths(1).truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("USD"), BigDecimal.valueOf(100), BigDecimal.valueOf(200), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                Arguments.of(Currency.getInstance("EUR"), BigDecimal.valueOf(200), BigDecimal.valueOf(700), LocalDateTime.now().minusMonths(2).truncatedTo(ChronoUnit.MINUTES))
        );
    }

    static Stream<Arguments> getUserId() {
        return Stream.of(
                Arguments.of(2L)
        );
    }
}