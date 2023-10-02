package com.hevo.cloud.docsearch.Services;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.hevo.cloud.docsearch.clients.DropboxConnectionClient;
import com.hevo.cloud.docsearch.clients.ElasticsearchConnectionClient;
import com.hevo.cloud.docsearch.controllers.DocumentSearchController;
import com.hevo.cloud.docsearch.dao.ElasticSearchDao;
import com.hevo.cloud.docsearch.dto.Document;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final ElasticSearchDao elasticSearchDao;
    private final DropboxConnectionClient dropboxConnectionClient;

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    public DocumentService(ElasticSearchDao elasticSearchDao,
                           DropboxConnectionClient dropboxConnectionClient) {
        this.elasticSearchDao = elasticSearchDao;
        this.dropboxConnectionClient = dropboxConnectionClient;
    }

    public List<String> searchDocuments(String query) {
        SearchResponse searchResponse = elasticSearchDao.fetchFilesForgivenContent(query);
        Set<String> uniqueFilenames = new HashSet<>();

        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();
            String fileName = (String) source.get("fileName");
            uniqueFilenames.add(fileName);
        }
        return uniqueFilenames.stream().toList();
    }

    public void reIndexDocuments() throws DbxException, IOException {
        elasticSearchDao.deleteAllDocuments();
        DbxClientV2 dbClient = dropboxConnectionClient.createClient();
        ListFolderResult result = dbClient.files().listFolder("");

        try {
            while (true) {
                fetchDataAndIndexInES(dbClient, result);
                if (!result.getHasMore()) {
                    break;
                }
                result = dbClient.files().listFolderContinue(result.getCursor());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return;
    }

    private void fetchDataAndIndexInES(DbxClientV2 dbClient, ListFolderResult result) throws DbxException, IOException {
        for (Metadata metadata : result.getEntries()) {
            String filePath = metadata.getPathDisplay();
            logger.info("Received file from dropbox as {}", filePath);
            InputStream inputStream = dbClient.files().download(filePath).getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            indexDatainES(metadata, content);
            inputStream.close();
        }
    }

    private void indexDatainES(Metadata metadata, StringBuilder content) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("content", content.toString());
        jsonMap.put("fileName", metadata.getPathDisplay());

        elasticSearchDao.indexDocument(jsonMap);
    }
}
