package uk.gov.cslearning.record.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.record.config.patchHelper.PatchHelperConfig;
import uk.gov.cslearning.record.exception.PatchResourceException;
import uk.gov.cslearning.record.validation.annotations.ValidEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

enum FavouriteColor {
    BLUE,
    RED
}

@AllArgsConstructor
@NoArgsConstructor
class TestPerson {
    public String name;
    public int age;

    @ValidEnum(enumClass = FavouriteColor.class)
    @Enumerated(EnumType.STRING)
    public String favouriteColor;
}

/**
 * Test the PatchHelper class. Includes testing the objectmapper
 * class as well.
 */
@RunWith(SpringRunner.class)
public class PatchHelperTest {

    private final PatchHelperConfig phc = new PatchHelperConfig();
    private final PatchHelper patchHelper = new PatchHelper();

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonPatch generatePatch(String rawJson) throws IOException {
        JsonNode patchJson = mapper.readTree(rawJson);
        return JsonPatch.fromJson(patchJson);
    }

    private TestPerson getSamplePerson() {
        return new TestPerson("John", 20, "BLUE");
    }

    @Test
    public void testSuccessfulPatch() throws IOException {
        TestPerson testPerson = getSamplePerson();
        JsonPatch patch = generatePatch("[{ \"op\": \"replace\", \"path\": \"/name\", \"value\": \"Jack\" }]");
        TestPerson updatedPerson = patchHelper.patch(patch, testPerson, TestPerson.class);
        assertEquals("Jack", updatedPerson.name);
    }

    @Test
    public void testValidationFailure() throws IOException {
        TestPerson testPerson = getSamplePerson();
        JsonPatch patch = generatePatch("[{ \"op\": \"replace\", \"path\": \"/favouriteColor\", \"value\": \"GREEN\" }]");

        try {
            patchHelper.patch(patch, testPerson, TestPerson.class);
        } catch (Exception e) {
            assertTrue(e instanceof PatchResourceException);
        }
    }

    @Test
    public void testInvalidFieldFailure() throws IOException {
        TestPerson testPerson = getSamplePerson();
        JsonPatch patch = generatePatch("[{ \"op\": \"replace\", \"path\": \"/favouriteFood\", \"value\": \"eggs\" }]");

        try {
            patchHelper.patch(patch, testPerson, TestPerson.class);
        } catch (Exception e) {
            assertTrue(e instanceof PatchResourceException);
        }
    }

}
