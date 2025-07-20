package faang.school.projectservice.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public int testMethod(int a, int b) {
        // This is a test method to verify the service is working
        System.out.println("Test method executed successfully.");
        return a + b;
    }
}
