package uk.gov.cslearning.record.util;

import java.time.LocalDateTime;

public interface IUtilService {
    String generateUUID();

    LocalDateTime getNowDateTime();
}
