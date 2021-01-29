package org.example.misc.picocli;

import picocli.CommandLine;

public class Configuration {

    private static final String DEFAULT_DATABASE_HOST = "localhost";
    private static final String DEFAULT_DATABASE_PORT = "4321";

    private static final String DEFAULT_ELASTICSEARCH_URL = "http://localhost:9200";
    private static final String DEFAULT_ELASTICSEARCH_BULK_SIZE = "500";

    @CommandLine.Option(names = { "-d", "--data" }, description = "data type (valid values: ${COMPLETION-CANDIDATES})", required = true)
    private DataType dataType;

    @CommandLine.Option(names = { "-dbHost", "--databaseHost" }, description = "database host (default: ${DEFAULT-VALUE})", defaultValue = DEFAULT_DATABASE_HOST)
    private String databaseHost;

    @CommandLine.Option(names = { "-dbPort", "--databasePort" }, description = "database port (default: ${DEFAULT-VALUE})", defaultValue = DEFAULT_DATABASE_PORT)
    private int databasePort;

    @CommandLine.Option(names = { "-esUrl", "--elasticsearchUrl" }, description = "elasticsearch url (default: ${DEFAULT-VALUE})", defaultValue = DEFAULT_ELASTICSEARCH_URL)
    private String elasticsearchUrl;

    @CommandLine.Option(names = { "--elasticsearchBulkSize" }, description = "elasticsearch bulk size (default: ${DEFAULT-VALUE})", defaultValue = DEFAULT_ELASTICSEARCH_BULK_SIZE)
    private int elasticsearchBulkSize;

    @CommandLine.Option(names = "--async", description = "runs bulk inserts asynchronously")
    boolean async;

    @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
    }

    public String getElasticsearchUrl() {
        return elasticsearchUrl;
    }

    public void setElasticsearchUrl(String elasticsearchUrl) {
        this.elasticsearchUrl = elasticsearchUrl;
    }

    public int getElasticsearchBulkSize() {
        return elasticsearchBulkSize;
    }

    public void setElasticsearchBulkSize(int elasticsearchBulkSize) {
        this.elasticsearchBulkSize = elasticsearchBulkSize;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isHelpRequested() {
        return helpRequested;
    }

    public void setHelpRequested(boolean helpRequested) {
        this.helpRequested = helpRequested;
    }
}
