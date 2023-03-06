package com.hkmc.vsmhistory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class InfoDTO {

    @JsonProperty(value = "vehicleId")
    String vehicleId;
    @JsonProperty(value = "xtid")
    String xtid;

}
