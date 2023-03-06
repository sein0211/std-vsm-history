package com.hkmc.vsmhistory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    @JsonProperty(value = "info")
    InfoDTO infoDTO;
    @JsonProperty(value = "count")
    Integer count;
    @JsonProperty(value = "historyList")
    List<HistoryDTO> historyDTOList;
}
