package ru.gazpromneft.camelamqtest.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gazpromneft.camelamqtest.exception.SslContextException;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;

/**
 * Ssl конфигурация.
 *
 * @author Leonid Ivanov.
 */
@Configuration
@Slf4j
public class SslConfig {

    @Value("${server.ssl.protocol}")
    private String protocol;
    @Value("${server.ssl.trust-store}")
    private String serverTruststorePath;
    @Value("${server.ssl.key-store}")
    private String serverKeystorePath;
    @Value("${server.ssl.key-store-password}")
    private String serverKeystorePassword;

    /**
     * Инициализация и установка Ssl контекста.
     */
    @Bean(name = "sslContextInit")
    void createServerSSLContext() {
        log.info("Creating SSL Context...");
        final char[] password = serverKeystorePassword.toCharArray();
        try {
            final KeyStore serverKeyStore = getKeyStore(password);
            KeyManager[] serverKeyManagers = getKeyManagers(serverKeyStore, password);
            final KeyStore serverTrustStore = getTrustStore();
            TrustManager[] serverTrustManagers = getTrustManagers(serverTrustStore);

            SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(serverKeyManagers, serverTrustManagers, null);

            SSLContext.setDefault(sslContext);

            log.info("DONE!");
        } catch (GeneralSecurityException e) {
            throw new SslContextException(e.getMessage());
        }
    }

    /**
     * Инициализация хранилища закрытых ключей.
     *
     * @param password пароль к хранилищу
     * @throws GeneralSecurityException исключение при получении экземпляра класса
     */
    private KeyStore getKeyStore(final char[] password) throws GeneralSecurityException {
        log.info("Loading keystore...");
        try (FileInputStream inputStream = new FileInputStream(serverKeystorePath)) {
            final KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(inputStream, password);

            return store;
        } catch (IOException e) {
            throw new SslContextException(e.getMessage());
        }
    }

    /**
     * Инициализация менеджеров хранилища ключей.
     *
     * @param keyStore сгенерированное ранее хранилище закрытых ключей
     * @param password пароль к хранилищу
     * @throws GeneralSecurityException исключение при получении экземпляра класса
     */
    private KeyManager[] getKeyManagers(final KeyStore keyStore, final char[] password) throws GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);

        return keyManagerFactory.getKeyManagers();
    }

    /**
     * Инициализация хранилища доверенных сертификатов.
     *
     * @throws SslContextException исключение при инициализации параметров
     * @throws GeneralSecurityException исключение при получении экземпляра класса
     */
    private KeyStore getTrustStore() throws GeneralSecurityException {
        log.info("Loading truststore...");
        try (FileInputStream inputStream = new FileInputStream(serverTruststorePath)) {
            final KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(inputStream, null);

            return store;
        } catch (IOException e) {
            throw new SslContextException(e.getMessage());
        }
    }

    /**
     * Инициализация менеджеров хранилища доверенных сертификатов.
     *
     * @param truststore сгенерированное ранее хранилище доверенных сертификатов
     * @throws GeneralSecurityException исключение при инициализации
     */
    private TrustManager[] getTrustManagers(final KeyStore truststore) throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(truststore);

        return trustManagerFactory.getTrustManagers();
    }
}

