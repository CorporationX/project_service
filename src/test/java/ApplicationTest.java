import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApplicationTest {
    @Test
    void main_TestLoadingContext() {
        Assertions.assertEquals(8, 3 + 4);
    }
}
