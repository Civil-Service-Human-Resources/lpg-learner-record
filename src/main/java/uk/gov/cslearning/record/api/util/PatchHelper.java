package uk.gov.cslearning.record.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.exception.PatchResourceException;

import javax.validation.ConstraintViolationException;

@Component
public class PatchHelper {

    private final ObjectMapper mapper;

    @Autowired
    public PatchHelper(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    public <T> T patch(JsonPatch patch, T targetBean, Class<T> targetClass) {

        JsonNode target = mapper.convertValue(targetBean, JsonNode.class);
        try {
            JsonNode patchedTarget = patch.apply(target);
            return mapper.treeToValue(patchedTarget, targetClass);
        } catch (JsonPatchException e) {
            throw new PatchResourceException(e.getMessage());
        } catch (ConstraintViolationException e) {
            throw new PatchResourceException(e);
        } catch (JsonProcessingException e) {
            throw new PatchResourceException(e.getOriginalMessage());
        }
    }

}
