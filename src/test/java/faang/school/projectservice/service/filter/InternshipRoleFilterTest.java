package faang.school.projectservice.service.filter;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class InternshipRoleFilterTest extends TestFiltersSetUp{
    @InjectMocks //I did not actually understand why should we use it here, even though we do not have any mock classes to inject
    InternshipRoleFilter internshipRoleFilter;


    @Test
    @DisplayName("Testing if the filters are applicable")
    void testFiltersAreApplicable(){
        Assert.assertEquals(true, internshipRoleFilter.isApplicable(firstFilters));
    }


    @Test
    @DisplayName("Testing if the filters are not applicable")
    void testFiltersAreNotApplicable(){
        Assert.assertEquals(false, internshipRoleFilter.isApplicable(secondFilters));
    }

    @Test
    @DisplayName("Test if filters applied properly")
    void testFilters(){
        Assert.assertEquals(true, internshipRoleFilter.isApplicable(thirdFilters));
        Assert.assertEquals(true, internshipRoleFilter.apply(firstInternship, thirdFilters));

    }

    @Test
    @DisplayName("Test if filters applied properly")
    void testFiltersFalse(){
        Assert.assertEquals(true, internshipRoleFilter.isApplicable(secondFilters));
        Assert.assertEquals(false, internshipRoleFilter.apply(firstInternship, secondFilters));
    }
}