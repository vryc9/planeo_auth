package com.planeo.planeo_auth.infrastructure.kafka;

/** Wire format of account.data.deleted: confirms planeo_auth erased its data. */
public record AccountDataDeletedMessage(String requestId, String service, String deletedAt) {
}
