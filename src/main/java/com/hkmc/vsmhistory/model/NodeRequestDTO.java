package com.hkmc.vsmhistory.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeRequestDTO extends TimestampDTO {

    String node; // ex) Vehicle.Body.Windshield.Front.WasherFluid.LevelLow
    String vehicleId;
}
