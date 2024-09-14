package faang.school.projectservice.service;

import faang.school.projectservice.exception.ConstraintViolation;
import faang.school.projectservice.exception.MessageError;
//import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.repository.InternshipRepository;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest extends TestSetUp {
//    @Mock
//    private InternshipRepository internshipRepository;
//    @Mock
//    private InternshipMapperImpl internshipMapper;
//    @Mock
//    private InternshipUpdateLogicService internshipUpdateLogicService;
//    @InjectMocks
//    private InternshipService internshipService;
//
//
//    @Test
//    @DisplayName("The creation of internship")
//    void test_CreateInternship() {
//
//        Mockito.when(internshipMapper.toEntity(firstInternshipDto)).thenReturn(firstInternship);
//
//        internshipService.create(firstInternshipDto);
//
//        Mockito.verify(internshipMapper, times(1)).toEntity(firstInternshipDto);
//        Mockito.verify(internshipRepository, times(1)).save(firstInternship);
//        Mockito.verify(internshipMapper, times(1)).toDto(firstInternship);
//    }
//
//    @Test
//    @DisplayName("The updating of internship")
//    void test_UpdateLogic() {
//        internshipService.update(firstInternshipDto);
//
//        verify(internshipUpdateLogicService, times(1)).updateLogic(firstInternshipDto);
//    }
//
//
//    @Test
//    @DisplayName("There is no internship in repository")
//    void test_GetAllInternships() {
//        Mockito.when(internshipRepository.findAll()).thenReturn(internshipsList);
//        Mockito.when(internshipMapper.toDto(internshipsList)).thenReturn(internshipDtosList);
//
//        internshipService.getAllInternships();
//
//        verify(internshipRepository, times(1)).findAll();
//        verify(internshipMapper, times(1)).toDto(internshipsList);
//
//    }
//
//    @Test
//    @DisplayName("There is no internship with such id")
//    void test_FindById_NoSuchElementException() {
//        Mockito.when(internshipRepository.findById(11111L)).thenReturn(Optional.empty());
//
//        ConstraintViolation exception = Assert.assertThrows(ConstraintViolation.class, () -> internshipService.findById(11111L));
//
//        Assert.assertEquals(exception.getMessage(), MessageError.INTERNSHIP_NOT_FOUND_EXCEPTION.getMessage());
//    }
//
//    @Test
//    @DisplayName("Testing finding internship by id")
//    void test_FindById() {
//        Mockito.when(internshipRepository.findById(firstInternship.getId())).thenReturn(Optional.of(firstInternship));
//
//        internshipService.findById(firstInternship.getId());
//
//        verify(internshipMapper, times(1)).toDto(firstInternship);
//    }

}