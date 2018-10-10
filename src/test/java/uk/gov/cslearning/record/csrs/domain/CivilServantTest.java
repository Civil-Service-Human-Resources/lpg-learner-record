package uk.gov.cslearning.record.csrs.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CivilServantTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldConvertJsonToCivilServant() throws IOException {
        CivilServant civilServant = objectMapper.readValue(getClass().getResource("/civil-servant.json"), CivilServant.class);

        assertEquals("John Smith", civilServant.getFullName());
        assertEquals("PB3", civilServant.getGrade().getCode());
        assertEquals("Senior Civil Service - Director General", civilServant.getGrade().getName());
        assertEquals("co", civilServant.getOrganisationalUnit().getCode());
        assertEquals("Cabinet Office", civilServant.getOrganisationalUnit().getName());
        assertEquals(Collections.singletonList("PURCHASE_ORDER"), civilServant.getOrganisationalUnit().getPaymentMethods());

        assertEquals("Digital", civilServant.getProfession().getName());
        assertEquals("Commercial Specialist", civilServant.getJobRole().getName());
        assertEquals("Corporate finance", civilServant.getOtherAreasOfWork().get(0).getName());
        assertEquals("Contract management", civilServant.getInterests().get(0).getName());
        assertEquals("line-manager@domain.com", civilServant.getLineManagerEmailAddress());
        assertEquals("Johnny Bananas", civilServant.getLineManagerName());
    }

    @Test
    public void shouldNotThrowNullPointerExceptionIfPropertiesMissing() throws IOException {
        CivilServant civilServant = objectMapper.readValue("{}", CivilServant.class);

        assertNull(civilServant.getFullName());
        assertNull(civilServant.getGrade().getCode());
        assertNull(civilServant.getGrade().getName());
        assertNull(civilServant.getOrganisationalUnit().getCode());
        assertNull(civilServant.getOrganisationalUnit().getName());
        assertEquals(Collections.emptyList(), civilServant.getOrganisationalUnit().getPaymentMethods());

        assertNull(civilServant.getProfession().getName());
        assertNull(civilServant.getJobRole().getName());
        assertEquals(Collections.emptyList(), civilServant.getOtherAreasOfWork());
        assertEquals(Collections.emptyList(), civilServant.getInterests());
        assertNull(civilServant.getLineManagerEmailAddress());
        assertNull(civilServant.getLineManagerName());
    }

}