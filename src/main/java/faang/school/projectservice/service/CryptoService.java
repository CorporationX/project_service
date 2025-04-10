package faang.school.projectservice.service;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.PredefinedAeadParameters;
import faang.school.projectservice.exception.CryptoOperationException;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class CryptoService {

    private final Aead aead;

    public CryptoService() {
        try {
            AeadConfig.register();
            this.aead = KeysetHandle.generateNew(PredefinedAeadParameters.AES256_GCM)
                    .getPrimitive(Aead.class);
        } catch (GeneralSecurityException e) {
            throw new CryptoOperationException("Crypto initialization failed");
        }
    }

    public String encrypt(String plainText) {
        try {
            byte[] encrypted = aead.encrypt(plainText.getBytes(UTF_8), null);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new CryptoOperationException("Encryption error");
        }
    }

    public String decrypt(String encryptedText) {
        try {
            byte[] decrypted = aead.decrypt(Base64.getDecoder().decode(encryptedText), null);
            return new String(decrypted, UTF_8);
        } catch (GeneralSecurityException e) {
            throw new CryptoOperationException("Decryption error");
        }
    }
}
