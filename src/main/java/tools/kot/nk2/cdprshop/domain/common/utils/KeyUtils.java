package tools.kot.nk2.cdprshop.domain.common.utils;


import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyUtils {

    @SneakyThrows
    public static PrivateKey getPrivateKey(File file) {
        byte[] keyBytes = Files.readAllBytes(file.toPath());

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    @SneakyThrows
    public static PublicKey getPublicKey(File file) {
        byte[] keyBytes = Files.readAllBytes(file.toPath());

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
