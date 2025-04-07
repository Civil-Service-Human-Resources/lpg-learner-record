package uk.gov.cslearning.record.csrs.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPageResponse<T> {
    private List<T> content;
    private Integer page;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
}
