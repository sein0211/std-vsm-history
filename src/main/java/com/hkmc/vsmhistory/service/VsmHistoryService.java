package com.hkmc.vsmhistory.service;

import com.hkmc.vsmhistory.common.Const;
import com.hkmc.vsmhistory.document.VsmDocument;
import com.hkmc.vsmhistory.model.*;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.vault.support.JsonMapFlattener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.search.sort.SortOrder.ASC;

@Slf4j
@Service
@RequiredArgsConstructor
public class VsmHistoryService {
    private final ElasticsearchOperations operations;
    private final MetaService metaService;
    private static final com.jayway.jsonpath.Configuration jaywayConfig = com.jayway.jsonpath.Configuration.defaultConfiguration()
            .setOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS);

    public ResponseDTO getNodeDeltaHistory(NodeRequestDTO nodeRequestDTO) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(QueryBuilders.rangeQuery(Const.Field.TIMESTAMP)
                .gte(processTimestamp(nodeRequestDTO.getStartTime()))
                .lte(processTimestamp(nodeRequestDTO.getLastTime())));
        queryBuilder.must(QueryBuilders.matchQuery(Const.Field.KUBERNETES_LABELS_APP, Const.Value.STREAM));
        queryBuilder.must(QueryBuilders.queryStringQuery("\"" + Const.Message.DELTA + "\""));
        queryBuilder.must(QueryBuilders.matchPhraseQuery(Const.Field.MESSAGE, Const.Message.VEHICLE_ID + nodeRequestDTO.getVehicleId()));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSorts(SortBuilders.fieldSort(Const.Field.TIMESTAMP).order(ASC))
                .build();

        List<HistoryDTO> historyDTOList = new ArrayList<>();
        Integer count = 0;

        for (SearchHit<VsmDocument> searchHit : operations.search(nativeSearchQuery, VsmDocument.class, IndexCoordinates.of("us_msa_lms_log-*")).getSearchHits()) {
            String message = searchHit.getContent().getLog_message();

            if (message.contains(Const.Message.PAYLOAD_SPLIT)) {

                int payloadIdx = message.indexOf(Const.Message.PAYLOAD_SPLIT) + Const.Value.PAYLOAD_SPLIT_LENGTH;
                String vsm = message.substring(payloadIdx); // | PAYLOAD :  이후 로그

                Map<String, Object> previous = JsonPath.using(jaywayConfig).parse(vsm).read("$.previous.state");
                Map<String, Object> preFlatten = JsonMapFlattener.flatten(previous);

                Map<String, Object> current = JsonPath.using(jaywayConfig).parse(vsm).read("$.current.state");
                Map<String, Object> currFlatten = JsonMapFlattener.flatten(current);

                if (!Objects.equals(String.valueOf(preFlatten.get(nodeRequestDTO.getNode())),
                        String.valueOf(currFlatten.get(nodeRequestDTO.getNode())))) {
//                    System.out.println("time = " + message.substring(0, 23));
//                    System.out.println("message = " + substring);
//                    System.out.println("preVal = " + preFlatten.get(nodeRequestDTO.getNode()));
//                    System.out.println("currVal = " + currFlatten.get(nodeRequestDTO.getNode()));
//                    System.out.println("----------------------");

                    count++;
                    historyDTOList.add(HistoryDTO.builder()
                            .time(message.substring(0, Const.Value.TIMESTAMP_LENGTH))
                            .preVal(String.valueOf(preFlatten.get(nodeRequestDTO.getNode())))
                            .currVal(String.valueOf(currFlatten.get(nodeRequestDTO.getNode()))).build());
                }
            }
        }
        return ResponseDTO.builder().count(count).historyDTOList(historyDTOList).build();
    }

    public ResponseDTO getNodeWatchHistory(NodeRequestDTO nodeRequestDTO) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(QueryBuilders.rangeQuery(Const.Field.TIMESTAMP)
                .gte(processTimestamp(nodeRequestDTO.getStartTime()))
                .lte(processTimestamp(nodeRequestDTO.getLastTime())));
        queryBuilder.must(QueryBuilders.matchQuery(Const.Field.KUBERNETES_LABELS_APP, Const.Value.STREAM));
        queryBuilder.must(QueryBuilders.queryStringQuery("\"" + Const.Message.WATCH + "\""));
        queryBuilder.must(QueryBuilders.matchPhraseQuery(Const.Field.MESSAGE, Const.Message.VEHICLE_ID + nodeRequestDTO.getVehicleId()));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSorts(SortBuilders.fieldSort(Const.Field.TIMESTAMP).order(ASC))
                .build();

        List<HistoryDTO> historyDTOList = new ArrayList<>();
        Integer count = 0;

        for (SearchHit<VsmDocument> searchHit : operations.search(nativeSearchQuery, VsmDocument.class, IndexCoordinates.of("us_msa_lms_log-*")).getSearchHits()) {
            String message = searchHit.getContent().getLog_message();

            if (message.contains(Const.Message.PAYLOAD_SPLIT)) {

                int payloadIdx = message.indexOf(Const.Message.PAYLOAD_SPLIT) + Const.Value.PAYLOAD_SPLIT_LENGTH;
                String vsm = message.substring(payloadIdx); // | PAYLOAD :  이후 로그

                Map<String, Object> curr = JsonPath.using(jaywayConfig).parse(vsm).read("$.state");
                Map<String, Object> currFlatten = JsonMapFlattener.flatten(curr);

                if (currFlatten.containsKey(nodeRequestDTO.getNode())) {
//                    System.out.println("time = " + message.substring(0, 23));
//                    System.out.println("currVal = " + currFlatten.get(nodeRequestDTO.getNode()));
//                    System.out.println("----------------------");
                    count++;
                    historyDTOList.add(HistoryDTO.builder()
                            .time(message.substring(0, Const.Value.TIMESTAMP_LENGTH))
                            .currVal(String.valueOf(currFlatten.get(nodeRequestDTO.getNode()))).build());
                }
            }
        }
        return ResponseDTO.builder().count(count).historyDTOList(historyDTOList).build();
    }

    /**
     * 1. 프로토콜명, vehicleId 로 sp 에서 검색
     * 2. 1 에서 검색한 결과들 중 start time 과 제일 근접한 로그의 xtid 를 추출하여 해당 xtid 를 가지는 모든 로그 검색
     */
    public ResponseDTO getProtocolHistory(ProtocolRequestDTO protocolRequestDTO) {

        List<HistoryDTO> historyDTOList = new ArrayList<>();
        Integer count = 0;

        String vehicleId = metaService.getVehicleIdByVin(protocolRequestDTO.getVin());
        String xtid = "";
        BoolQueryBuilder spaQueryBuilder = QueryBuilders.boolQuery();

        spaQueryBuilder.must(QueryBuilders.rangeQuery(Const.Field.TIMESTAMP)
                .gte(processTimestamp(protocolRequestDTO.getStartTime()))
                .lte(processTimestamp(protocolRequestDTO.getLastTime())));
        spaQueryBuilder.must(QueryBuilders.matchQuery(Const.Field.KUBERNETES_LABELS_APP, Const.Value.SP));
        spaQueryBuilder.must(QueryBuilders.queryStringQuery(protocolRequestDTO.getProtocol()));
        spaQueryBuilder.must(QueryBuilders.matchPhraseQuery(Const.Field.MESSAGE, vehicleId));

        NativeSearchQuery spaNativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(spaQueryBuilder)
                .withSorts(SortBuilders.fieldSort(Const.Field.TIMESTAMP).order(ASC))
                .build();

        SearchHit<VsmDocument> spaSearchHit = operations.search(spaNativeSearchQuery, VsmDocument.class, IndexCoordinates.of("us_msa_lms_log-*")).getSearchHits().get(0);
        String spaMessage = spaSearchHit.getContent().getLog_message();

        if (spaMessage.contains(Const.Message.XTID_SPLIT)) {

            int xtidIdx = spaMessage.indexOf(Const.Message.XTID_SPLIT) + Const.Value.XTID_SPLIT_LENGTH;
            xtid = spaMessage.substring(xtidIdx, xtidIdx + Const.Value.XTID_LENGTH);

            BoolQueryBuilder xtidQueryBuilder = QueryBuilders.boolQuery();

            xtidQueryBuilder.must(QueryBuilders.rangeQuery(Const.Field.TIMESTAMP)
                    .gte(processTimestamp(protocolRequestDTO.getStartTime()))
                    .lte(processTimestamp(protocolRequestDTO.getLastTime())));
            xtidQueryBuilder.must(QueryBuilders.matchPhraseQuery(Const.Field.MESSAGE, xtid));

            NativeSearchQuery xtidNativeSearchQuery = new NativeSearchQueryBuilder()
                    .withQuery(xtidQueryBuilder)
                    .withSorts(SortBuilders.fieldSort(Const.Field.TIMESTAMP).order(ASC))
                    .build();

            for (SearchHit<VsmDocument> xtidSearchHit : operations.search(xtidNativeSearchQuery, VsmDocument.class, IndexCoordinates.of("us_msa_lms_log-*")).getSearchHits()) {
                VsmDocument vsm = xtidSearchHit.getContent();
                String xtidMessage = vsm.getLog_message();
                count++;
                historyDTOList.add(HistoryDTO.builder()
                        .time(xtidMessage.substring(0, Const.Value.TIMESTAMP_LENGTH))
                        .message(xtidMessage)
                        .app(vsm.getApp())
                        .build());
            }
        }
        return ResponseDTO.builder().infoDTO(InfoDTO.builder().vehicleId(vehicleId).xtid(xtid).build()).count(count).historyDTOList(historyDTOList).build();
    }

    private String processTimestamp(LocalDateTime localDateTime) {
        return localDateTime.minusHours(9).format(DateTimeFormatter.ofPattern(Const.TIMESTAMP_FORMAT));
    }

    public ResponseDTO test(NodeRequestDTO nodeRequestDTO) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(QueryBuilders.rangeQuery(Const.Field.TIMESTAMP)
                .gte(processTimestamp(nodeRequestDTO.getStartTime()))
                .lte(processTimestamp(nodeRequestDTO.getLastTime())));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSorts(SortBuilders.fieldSort(Const.Field.TIMESTAMP).order(ASC))
                .build();

        List<HistoryDTO> historyDTOList = new ArrayList<>();
        Integer count = 0;

        for (SearchHit<VsmDocument> searchHit : operations.search(nativeSearchQuery, VsmDocument.class, IndexCoordinates.of("us_msa_lms_log-*")).getSearchHits()) {
            String message = searchHit.getContent().getLog_message();
            count++;
            historyDTOList.add(HistoryDTO.builder()
                    .time(message.substring(0, Const.Value.TIMESTAMP_LENGTH)).build());
        }
        return ResponseDTO.builder().count(count).historyDTOList(historyDTOList).build();
    }

//    Map<String, Object> body = mapper.readValue(substring, new TypeReference<>() { // json String -> Map
//    });
//    VsmDTO preVsmDTO = mapper.convertValue(previous, new TypeReference<>() {});
//    VsmDTO currVsmDTO = mapper.convertValue(current, new TypeReference<>() {});

}
