package com.hevo.cloud.docsearch.controllers;

import com.hevo.cloud.docsearch.Services.DocumentService;
import com.hevo.cloud.docsearch.dto.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class DocumentSearchController {

    @Autowired
    private DocumentService documentService;

    private static final Logger logger = LoggerFactory.getLogger(DocumentSearchController.class);


    @GetMapping("/search")
    public List<String> searchDocuments(@RequestParam String query) {
        logger.info("Received search query: {}", query);
        try {
            // Implement the search logic in the DocumentService
            List<String> results = documentService.searchDocuments(query);
            logger.info("Search results: {}", results);
            return results;
        } catch (Exception e) {
            logger.error("Error during search:", e);
            // Handle the exception and return an appropriate response
            return Collections.emptyList();
        }
    }

    @GetMapping("/reindex-data")
    public String reindexData() {
        logger.info("Received request for reindex");
        try {
            documentService.reIndexDocuments();
            return "Reindexing successful";
        } catch (Exception e) {
            logger.error("Error during reindexing:", e);
            // Handle the exception and return an appropriate response
            return "Failed during reindexing";
        }
    }
}
