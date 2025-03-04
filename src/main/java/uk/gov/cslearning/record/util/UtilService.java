package uk.gov.cslearning.record.util;

import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UtilService implements IUtilService {
    private static final String ALLOWED_CHARACTERS = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";

    private final Clock clock;

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateSaltedString(int hashLength) {
        String salt = String.valueOf(System.currentTimeMillis());
        Hashids hashids = new Hashids(salt, hashLength, ALLOWED_CHARACTERS);
        return hashids.encode(1L);
    }

    @Override
    public LocalDateTime getNowDateTime() {
        return LocalDateTime.now(clock);
    }

    @Override
    public Instant getNowInstant() {
        return getNowDateTime().toInstant(ZoneOffset.UTC);
    }

}
