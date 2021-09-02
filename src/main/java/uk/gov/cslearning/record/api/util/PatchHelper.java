package uk.gov.cslearning.record.api.util;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
@RequiredArgsConstructor
public class PatchHelper {

    private final ObjectMapper mapper;

    @Valid
    public <T> T patch(JsonPatch patch, T targetBean, Class<T> targetClass) throws JsonPatchException {
        JsonNode target = mapper.valueToTree(targetBean);
        JsonNode patchedTarget = patch.apply(target);
        T patchedBean = mapper.convertValue(patchedTarget, targetClass);
        return patchedBean;
    }
}
