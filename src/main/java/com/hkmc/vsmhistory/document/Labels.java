package com.hkmc.vsmhistory.document;

import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Field;
@Getter
public class Labels {

    private String app;

    @Field(name = "helm_sh/chart")
    private String helmShChart;
}
