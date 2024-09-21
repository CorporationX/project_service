package faang.school.projectservice.service.filter;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternshipStatusFilterTest extends TestFiltersSetUp{
    @InjectMocks //I did not actually understand why should we use it here, even though we do not have any mock classes to inject
    InternshipStatusFilter internshipStatusFilter;

//    @Test
//    @DisplayName("Testing if the filters are applicable")
//    void testFiltersAreApplicable(){
//        Assert.assertEquals(true, internshipStatusFilter.isApplicable(firstFilters));
//    }

    @Test
    @DisplayName("Testing if the filters are applicable")
    void testFiltersAreApplicable(){
        Assert.assertEquals(false, internshipStatusFilter.isApplicable(firstFilters));
    }


    @Test
    @DisplayName("Testing if the filters are not applicable")
    void testFiltersAreNotApplicable(){
        Assert.assertEquals(false, internshipStatusFilter.isApplicable(forthFilters));
    }

    @Test
    @DisplayName("Test if filters applied properly")
    void testFilters(){
        Assert.assertEquals(true, internshipStatusFilter.isApplicable(secondFilters));
        Assert.assertEquals(true, internshipStatusFilter.apply(secondInternship, secondFilters));

    }

    @Test
    @DisplayName("Test if filters applied properly")
    void testFiltersFalse(){
        Assert.assertEquals(true, internshipStatusFilter.isApplicable(secondFilters));
        Assert.assertEquals(false, internshipStatusFilter.apply(firstInternship, secondFilters));
    }
}