package uk.gov.cslearning.record.util;

import java.time.Instant;
import java.time.LocalDateTime;

public interface IUtilService {
    String generateUUID();

    String generateSaltedString(int hashLength);

    LocalDateTime getNowDateTime();

    Instant getNowInstant();
}
