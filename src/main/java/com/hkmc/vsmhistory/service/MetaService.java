package com.hkmc.vsmhistory.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hkmc.vsmhistory.common.Const;
import com.hkmc.vsmhistory.common.exception.GlobalCCSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaService {
    private final WebClient webClient;
    private final PreStepService preStepService;
    private final Gson gson = new Gson();
    private final ObjectMapper mapper;
    @Value("${spring.svchub.host}")
    private String SVCHUB_HOST;

    @Value("${spring.svchub.port}")
    private String SVCHUB_PORT;

    @Value("${spring.svchub.vehicleId.path}")
    private String VEHICLEID_PATH;

    public String getVehicleIdByVin(String vin) {
        ResponseEntity<Object> response = webClient.get()
                .uri(UriComponentsBuilder.newInstance()
                        .scheme("http")
                        .host(SVCHUB_HOST)
                        .port(SVCHUB_PORT)
                        .path(VEHICLEID_PATH)
                        .queryParam("vin", "{vin}")
                        .build().toString(), vin)
                .header(Const.K_CLINET_ID, preStepService.getPreStepInfo().getClientId())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, res -> Mono.error(new GlobalCCSException(res.rawStatusCode())))
                .onStatus(HttpStatus::is5xxServerError, res -> Mono.error(new GlobalCCSException(res.rawStatusCode())))
                .toEntity(Object.class)
                .doOnNext(res -> log.info("HTTP Response for VehicleId Get | Status : {}, ResponseBody : {}",
                        res.getStatusCode(), gson.toJson(res.getBody())))
                .blockOptional()
                .orElseThrow(() -> new GlobalCCSException(500, "ResponseBody = null"));

        Map<String, Object> body = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        return (String) body.getOrDefault("vehicleId", "");
    }
}
