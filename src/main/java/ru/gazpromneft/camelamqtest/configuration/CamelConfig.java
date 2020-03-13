package ru.gazpromneft.camelamqtest.configuration;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import ru.gazpromneft.camelamqtest.exception.ConnectionException;
import ru.gazpromneft.camelamqtest.exception.SslContextException;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

/**
 * Конфигурация Apache Camel
 *
 * @author Leonid Ivanov.
 */
@Configuration
@Log4j2
public class CamelConfig {

    @Value("${broker.host}")
    private String connectionUrl;
    @Value("${broker.username}")
    private String connectionUsername;
    @Value("${broker.password}")
    private String connectionPassword;

    @Bean
    @DependsOn("sslContextInit")
    ConnectionFactory connectionFactory() {
        log.info("Broker connection initialization...");
        try {
            JmsConnectionFactory connectionFactory = new JmsConnectionFactory();
            connectionFactory.setSslContext(SSLContext.getDefault());
            connectionFactory.setRemoteURI(connectionUrl);
            connectionFactory.setUsername(connectionUsername);
            connectionFactory.setPassword(connectionPassword);
            connectionFactory.createConnection();

            JmsPoolConnectionFactory pool = new JmsPoolConnectionFactory();
            pool.setMaxConnections(5);
            pool.setConnectionFactory(connectionFactory);

            log.info("DONE!");

            return pool;

        } catch (final JMSException e) {
            throw new ConnectionException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new SslContextException(e.getMessage());
        }
    }

    @Bean
    AMQPComponent amqpComponent(final ConnectionFactory connectionFactory) {
        log.info("AMQP component initialization...");
        return new AMQPComponent(connectionFactory);
    }
}
