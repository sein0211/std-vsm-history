package com.hkmc.vsmhistory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class HistoryDTO {

    @JsonProperty(value = "time")
    String time;
    @JsonProperty(value = "message")
    String message;
    @JsonProperty(value = "preVal")
    String preVal;
    @JsonProperty(value = "currVal")
    String currVal;
    @JsonProperty(value = "app")
    String app;
}
