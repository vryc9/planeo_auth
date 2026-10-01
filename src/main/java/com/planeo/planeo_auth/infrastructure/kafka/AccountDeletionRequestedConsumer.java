package com.planeo.planeo_auth.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planeo.planeo_auth.service.AccountErasureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Reacts to AccountDeletionRequested. Erasing then confirming is safe to replay: erasing an
 * absent user is a no-op and the confirmation is deduplicated by planeo_back. Any failure
 * propagates so the error handler retries, then routes the record to the DLT.
 */
@Component
public class AccountDeletionRequestedConsumer {

    static final String REQUESTED_TOPIC = "account.deletion.requested";
    static final String CONFIRMATION_TOPIC = "account.data.deleted";
    private static final Logger log = LoggerFactory.getLogger(AccountDeletionRequestedConsumer.class);

    private final ObjectMapper objectMapper;
    private final AccountErasureService erasureService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AccountDeletionRequestedConsumer(ObjectMapper objectMapper,
                                            AccountErasureService erasureService,
                                            KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.erasureService = erasureService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = REQUESTED_TOPIC, groupId = "planeo-auth-group")
    public void consume(String payload) throws Exception {
        AccountDeletionRequestedMessage event = parse(payload);

        erasureService.erase(event.username());

        String confirmation = objectMapper.writeValueAsString(
                new AccountDataDeletedMessage(event.requestId(), "AUTH", Instant.now().toString()));
        kafkaTemplate.send(CONFIRMATION_TOPIC, event.requestId(), confirmation).get(10, TimeUnit.SECONDS);
        log.info("Compte supprimé dans planeo_auth (requestId={})", event.requestId());
    }

    private AccountDeletionRequestedMessage parse(String payload) {
        try {
            AccountDeletionRequestedMessage event = objectMapper.readValue(payload, AccountDeletionRequestedMessage.class);
            if (event.requestId() == null || event.requestId().isBlank()
                    || event.username() == null || event.username().isBlank()) {
                throw new InvalidMessageException("account.deletion.requested incomplet", null);
            }
            return event;
        } catch (JsonProcessingException e) {
            throw new InvalidMessageException("Payload illisible", e);
        }
    }
}
