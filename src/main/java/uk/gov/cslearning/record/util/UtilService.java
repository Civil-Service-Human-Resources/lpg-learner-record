package uk.gov.cslearning.record.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UtilService implements IUtilService {

    private final Clock clock;

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime getNowDateTime() {
        return LocalDateTime.now(clock);
    }

}
