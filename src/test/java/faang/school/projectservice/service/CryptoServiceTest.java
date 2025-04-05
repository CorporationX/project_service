package faang.school.projectservice.service;

import com.google.crypto.tink.Aead;
import faang.school.projectservice.exception.CryptoOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.GeneralSecurityException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CryptoServiceTest {

    private final String encryptedText = Base64.getEncoder().encodeToString("encrypted".getBytes());
    private final String plainText = "test-data";

    @Mock
    private Aead aead;

    @InjectMocks
    private CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cryptoService, "aead", aead);
    }

    @Test
    void testNegativeEncrypt() throws GeneralSecurityException {
        when(aead.encrypt(any(), eq(null))).thenThrow(new GeneralSecurityException("Test error"));

        assertThrows(CryptoOperationException.class, () -> cryptoService.encrypt(plainText));
    }

    @Test
    void testPositiveEncrypt() throws GeneralSecurityException {
        byte[] encryptedBytes = "encrypted".getBytes(UTF_8);
        when(aead.encrypt(plainText.getBytes(UTF_8), null)).thenReturn(encryptedBytes);

        String result = cryptoService.encrypt(plainText);

        assertEquals(Base64.getEncoder().encodeToString(encryptedBytes), result);
    }

    @Test
    void testNegativeDecrypt() throws GeneralSecurityException {
        when(aead.decrypt(any(), eq(null))).thenThrow(new GeneralSecurityException("Test error"));

        assertThrows(CryptoOperationException.class, () -> cryptoService.decrypt(encryptedText));
    }

    @Test
    void testPositiveDecrypt() throws GeneralSecurityException {
        byte[] decryptedBytes = "decrypted".getBytes(UTF_8);
        when(aead.decrypt(Base64.getDecoder().decode(encryptedText), null)).thenReturn(decryptedBytes);

        String result = cryptoService.decrypt(encryptedText);

        assertEquals("decrypted", result);
    }
}
