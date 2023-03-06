package com.hkmc.vsmhistory.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtocolRequestDTO extends TimestampDTO {

    String protocol;
    String vin;
}
