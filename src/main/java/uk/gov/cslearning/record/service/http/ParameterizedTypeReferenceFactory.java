package uk.gov.cslearning.record.service.http;

import com.google.common.collect.ImmutableMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.CivilServantDto;
import uk.gov.cslearning.record.dto.IdentityDto;
import uk.gov.cslearning.record.service.catalogue.Course;

import java.util.List;
import java.util.Map;

@Component
public class ParameterizedTypeReferenceFactory {
    /**
     * The maps below are a nasty hack. Unfortunately it's necessary because of the way ParameterizedTypeReference is implemented.
     * See https://stackoverflow.com/questions/21987295/using-spring-resttemplate-in-generic-method-with-generic-parameter
     */

    private final Map<String, ParameterizedTypeReference> mapParameterizedTypeReferenceMap = ImmutableMap.of(
            "uk.gov.cslearning.record.dto.IdentityDTO",
            new ParameterizedTypeReference<Map<String, IdentityDto>>() {
            },
            "uk.gov.cslearning.record.dto.CivilServantDto",
            new ParameterizedTypeReference<Map<String, CivilServantDto>>() {
            },
            "uk.gov.cslearning.record.service.catalogue.Course",
            new ParameterizedTypeReference<Map<String, List<Course>>>() {
            }
    );

    <T> ParameterizedTypeReference<Map<String, T>> createMapReference(Class<T> type) {
        if (mapParameterizedTypeReferenceMap.containsKey(type.getName())) {
            return mapParameterizedTypeReferenceMap.get(type.getName());
        }

        throw new IllegalTypeException(type);
    }

    <T> ParameterizedTypeReference<Map<String, List<T>>> createMapReferenceWithList(Class<T> type) {
        if (mapParameterizedTypeReferenceMap.containsKey(type.getName())) {
            return mapParameterizedTypeReferenceMap.get(type.getName());
        }

        throw new IllegalTypeException(type);
    }
}
