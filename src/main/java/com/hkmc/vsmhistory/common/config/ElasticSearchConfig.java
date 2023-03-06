package com.hkmc.vsmhistory.common.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
@EnableElasticsearchRepositories
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.host}")
    private String ES_HOST;

    @Value("${spring.elasticsearch.port}")
    private Integer ES_PORT;

    @Value("${spring.elasticsearch.userName}")
    private String ES_USERNAME;

    @Value("${spring.elasticsearch.password}")
    private String ES_PASSWORD;

    @Override
    public RestHighLevelClient elasticsearchClient() {

        try {
            SSLContext sslContext;
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(null, null);

            FileInputStream fileInputStream = new FileInputStream(new File(ResourceUtils.getFile("classpath:ca.cert").getPath()));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(bufferedInputStream);
            keyStore.setCertificateEntry("ca", certificate);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ES_USERNAME, ES_PASSWORD));

            RestClientBuilder restClientBuilder = RestClient.builder(
                            new HttpHost(ES_HOST, ES_PORT, "http"))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
//                            .setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(credentialsProvider));

            return new RestHighLevelClient(restClientBuilder);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}