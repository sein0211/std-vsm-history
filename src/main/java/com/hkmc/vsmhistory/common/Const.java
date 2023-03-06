package com.hkmc.vsmhistory.common;

public class Const {

    public static final String VERSION_V1 = "vsmhistory/v1";
    public static final String K_UNIT = "unit";
    public static final String K_SERVICE_ID = "serviceId";
    public static final String V_SERVICE_ID = "vsm-history";
    public static final String K_CLINET_ID = "client-id";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static class Message {
        public static final String DELTA = "Kafka Consume Event (Delta -> Stream)";
        public static final String WATCH = "Kafka Consume Event (Watch -> Stream)";
        public static final String PAYLOAD_SPLIT = "| PAYLOAD : ";
        public static final String XTID_SPLIT = "XTID : ";
        public static final String VEHICLE_ID = "vehicle-id=";
    }

    public static class Field {
        public static final String TIMESTAMP = "logdate";
        public static final String MESSAGE = "log_message";
        public static final String KUBERNETES_LABELS_APP = "app";
    }

    public static class Value {
        public static final String STREAM = "stream";
        public static final String SP = "sp";
        public static final int TIMESTAMP_LENGTH = 23;
        public static final int XTID_LENGTH = 36;
        public static final int PAYLOAD_SPLIT_LENGTH = Message.PAYLOAD_SPLIT.length();
        public static final int XTID_SPLIT_LENGTH = Message.XTID_SPLIT.length();
    }
}
