package uk.gov.cslearning.record.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.exception.PatchResourceException;

@Component
@RequiredArgsConstructor
public class PatchHelper {

    private final ObjectMapper mapper;

    public <T> T patch(JsonPatch patch, T targetBean, Class<T> targetClass) {

        JsonNode target = mapper.convertValue(targetBean, JsonNode.class);
        try {
            JsonNode patchedTarget = patch.apply(target);
            return mapper.treeToValue(patchedTarget, targetClass);
        } catch (JsonPatchException e) {
            throw new PatchResourceException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new PatchResourceException(e.getOriginalMessage());
        }
    }

}
