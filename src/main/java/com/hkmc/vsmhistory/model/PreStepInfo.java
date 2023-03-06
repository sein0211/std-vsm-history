package com.hkmc.vsmhistory.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreStepInfo {

    private String clientId;
    private String serviceId;
    private byte[] token;
}
