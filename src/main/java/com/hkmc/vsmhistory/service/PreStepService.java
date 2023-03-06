package com.hkmc.vsmhistory.service;

import com.hkmc.vsmhistory.common.Const;
import com.hkmc.vsmhistory.common.exception.PreStepCallException;
import com.hkmc.vsmhistory.model.PreStepInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RefreshScope
@Slf4j
@Service
@RequiredArgsConstructor
public class PreStepService {

    @Value("${spring.svchub.host}")
    private String SVCHUB_HOST;
    @Value("${spring.svchub.port}")
    private String SVCHUB_PORT;
    @Value("${spring.svchub.pre-step.path}")
    private String PRE_STEP_URL;
    @Value("${spring.svchub.pre-step.unit}")
    private String UNIT;

    private PreStepInfo preStepInfo;
    private final WebClient webClient;

    @PostConstruct
    public Mono<Void> preStepInfoMono() {
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(SVCHUB_HOST)
                        .port(SVCHUB_PORT)
                        .path(PRE_STEP_URL)
                        .build())
                .bodyValue(Map.of(Const.K_UNIT, UNIT, Const.K_SERVICE_ID, getHostName()))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, res -> Mono.error(new IllegalArgumentException()))
                .onStatus(HttpStatus::is5xxServerError, res -> Mono.error(new PreStepCallException()))
                .bodyToMono(PreStepInfo.class)
                .flatMap(info -> {
                    String pwd = info.getServiceId().concat(":").concat(info.getClientId());
                    info.setToken(Base64.getEncoder().encode(pwd.getBytes(StandardCharsets.UTF_8)));
                    setPreStepInfo(info);
                    return Mono.empty();
                }).block();
        return Mono.empty();
    }

    private String getHostName() {
        try {
            log.info(InetAddress.getLocalHost().getHostName());
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            return Const.V_SERVICE_ID;
        }
    }

    private void setPreStepInfo(PreStepInfo preStepInfo) {
        this.preStepInfo = preStepInfo;
    }

    public PreStepInfo getPreStepInfo() {
        return preStepInfo;
    }
}
