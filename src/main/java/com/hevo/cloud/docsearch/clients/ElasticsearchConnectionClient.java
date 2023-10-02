package com.hevo.cloud.docsearch.clients;

import com.hevo.cloud.docsearch.properties.ElasticsearchProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchConnectionClient {

    private static ElasticsearchProperties elasticsearchProperties;

    public ElasticsearchConnectionClient(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    public static RestHighLevelClient createClient() {
        RestHighLevelClient esClient = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", elasticsearchProperties.getClusterName())
                    .build();

            esClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(elasticsearchProperties.getHost(), elasticsearchProperties.getPort(), elasticsearchProperties.getScheme())
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return esClient;
    }
}
