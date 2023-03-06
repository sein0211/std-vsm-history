package com.hkmc.vsmhistory.controller;

import com.hkmc.vsmhistory.common.Const;
import com.hkmc.vsmhistory.model.NodeRequestDTO;
import com.hkmc.vsmhistory.model.ProtocolRequestDTO;
import com.hkmc.vsmhistory.model.ResponseDTO;
import com.hkmc.vsmhistory.service.VsmHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = Const.VERSION_V1)
public class VsmHistoryController {

    private final VsmHistoryService vsmHistoryService;

    @PostMapping("/node/delta")
    public ResponseDTO getNodeDeltaHistory(@RequestBody NodeRequestDTO nodeRequestDTO) {
        return vsmHistoryService.getNodeDeltaHistory(nodeRequestDTO);
    }

    @PostMapping("/node/watch")
    public ResponseDTO getNodeWatchHistory(@RequestBody NodeRequestDTO nodeRequestDTO) {
        return vsmHistoryService.getNodeWatchHistory(nodeRequestDTO);
    }

    @PostMapping("/protocol")
    public ResponseDTO getProtocolHistory(@RequestBody ProtocolRequestDTO protocolRequestDTO) {
        return vsmHistoryService.getProtocolHistory(protocolRequestDTO);
    }

    @PostMapping("/test")
    public ResponseDTO test(@RequestBody NodeRequestDTO nodeRequestDTO) {
        return vsmHistoryService.test(nodeRequestDTO);
    }

}
