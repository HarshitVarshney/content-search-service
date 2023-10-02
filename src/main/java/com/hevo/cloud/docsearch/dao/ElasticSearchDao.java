package com.hevo.cloud.docsearch.dao;

import com.hevo.cloud.docsearch.clients.ElasticsearchConnectionClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ElasticSearchDao {
    private final ElasticsearchConnectionClient elasticsearchConnectionClient;

    private static final String CONTENT_DIRECTORY_ES_INDEX = "content-directory";

    @Autowired
    public ElasticSearchDao(ElasticsearchConnectionClient elasticsearchConnectionClient) {
        this.elasticsearchConnectionClient = elasticsearchConnectionClient;
    }

    public SearchResponse fetchFilesForgivenContent(String query) {

        RestHighLevelClient esClient = elasticsearchConnectionClient.createClient();

        SearchRequest searchRequest = new SearchRequest(CONTENT_DIRECTORY_ES_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("content", query));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(searchResponse);
            return searchResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllDocuments() {
        RestHighLevelClient esClient = elasticsearchConnectionClient.createClient();

        DeleteByQueryRequest deleteRequest = new DeleteByQueryRequest(CONTENT_DIRECTORY_ES_INDEX);
        deleteRequest.setQuery(QueryBuilders.matchAllQuery());


        try {
            BulkByScrollResponse response = esClient.deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
            System.out.println(response.getStatus());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void indexDocument(Map<String, Object> jsonMap) {
        RestHighLevelClient esClient = elasticsearchConnectionClient.createClient();
        try {
            IndexRequest indexRequest = new IndexRequest(CONTENT_DIRECTORY_ES_INDEX)
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .id(UUID.randomUUID().toString())
                    .source(jsonMap, XContentType.JSON);


            esClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.getMessage();
        }

    }

}
