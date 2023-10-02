package com.hevo.cloud.docsearch.clients;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.hevo.cloud.docsearch.properties.DropboxProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DropboxConnectionClient {

    private final DropboxProperties dropboxProperties;

    @Autowired
    public DropboxConnectionClient(DropboxProperties dropboxProperties) {
        this.dropboxProperties = dropboxProperties;
    }
    public DbxClientV2 createClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(dropboxProperties.getAppName()).build();
        return new DbxClientV2(config, dropboxProperties.getAccessToken());
    }
}
