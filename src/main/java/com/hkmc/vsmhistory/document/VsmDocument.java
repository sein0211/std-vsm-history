package com.hkmc.vsmhistory.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Document(indexName = "us_msa_lms_log-2023-03-03")
@Mapping(mappingPath = "elastic/vsm-mapping.json")
//@Mapping(mappingPath = "elastic/real-vsm-mapping.json")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VsmDocument {

    @Id
    private String id;

    private String logdate;

    private String log_message;

    private String app;

    private String namespace;

    private String classname;

}