package org.nurim.nurim.config.auth;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AES128Config {
    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;
    private static final String INSTANCE_TYPE = "AES/CBC/PKCS5Padding";

    @Value("${aes.secret}")
    private String secret;
    private IvParameterSpec ivParameterSpec;
    private SecretKeySpec secretKeySpec;
    private Cipher cipher;

    @PostConstruct
    public void init() throws NoSuchPaddingException, NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);
        secretKeySpec = new SecretKeySpec(secret.getBytes(ENCODING_TYPE), "AES");
        cipher = Cipher.getInstance(INSTANCE_TYPE);
    }

    // AES 암호화
    public String encryptAes(String plainText) throws Exception {

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(ENCODING_TYPE));

            return new String(Base64.getEncoder().encode(encrypted), ENCODING_TYPE);

        } catch (Exception e) {
            throw new Exception("암호화 중 오류 발생: " + e.getMessage());
        }
    }

    // AES 디코드
    public String decryptAes(String plainText) throws Exception {

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = Base64.getDecoder().decode(plainText.getBytes(ENCODING_TYPE));

            return new String(cipher.doFinal(decrypted), ENCODING_TYPE);

        } catch (Exception e) {
            throw new Exception("디코드 중 오류 발생: " + e.getMessage());
        }
    }

}
