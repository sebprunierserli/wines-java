package org.example.database;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.example.database.beans.Wine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ImportFromDatabase {

    private static final String SQL_FILE_NAME = "/vins.sql";

    private static final String ES_HOSTNAME = "localhost";
    private static final int ES_PORT = 9200;
    private static final String ES_SCHEME = "http";
    private static final String ES_INDEX_NAME = "my-wines-2";
    private static final Integer ES_BULK_SIZE = 100;

    public static void main(String[] args) {
        Connection dbConnection = null;
        RestHighLevelClient esClient = null;
        try {
            // Create database connection (hsqldb)
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            dbConnection = DriverManager.getConnection("jdbc:hsqldb:mem:winesdb", "SA", "");

            // Insert Wines into database
            List<String> sqlLines = Files.readAllLines(Paths.get(ImportFromDatabase.class.getResource(SQL_FILE_NAME).toURI()));
            for (String sqlLine : sqlLines) {
                if (!sqlLine.startsWith("--")) {
                    Statement st = dbConnection.createStatement();
                    st.execute(sqlLine);
                }
            }

            // Get all wines from database
            List<Wine> wines = new ArrayList<>();
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT nom, couleur, region, appellation, millesime, pays FROM Wines");
            while (rs.next()) {
                Wine wine = new Wine();
                wine.setNom(rs.getString("nom"));
                wine.setCouleur(rs.getString("couleur"));
                wine.setRegion(rs.getString("region"));
                wine.setAppellation(rs.getString("appellation"));
                wine.setMillesime(rs.getString("millesime"));
                wine.setPays(rs.getString("pays"));
                wines.add(wine);
            }

            // Prepare wines chunks
            AtomicInteger counter = new AtomicInteger();
            Collection<List<Wine>> winesChunks = wines.stream()
                    .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / ES_BULK_SIZE))
                    .values();

            // Create Elasticsearch client
            HttpHost httpHost = new HttpHost(ES_HOSTNAME, ES_PORT, ES_SCHEME);
            esClient = new RestHighLevelClient(RestClient.builder(httpHost));

            // Delete index if exists
            GetIndexRequest request = new GetIndexRequest(ES_INDEX_NAME);
            boolean indexExists = esClient.indices().exists(request, RequestOptions.DEFAULT);
            if (indexExists) {
                DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(ES_INDEX_NAME);
                AcknowledgedResponse deleteIndexResponse = esClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
                System.out.println("Delete index status: " + deleteIndexResponse.isAcknowledged());
            }

            // Insert chunks into Elasticsearch with Bulk API
            for (List<Wine> wineChunk : winesChunks) {
                BulkRequest bulkRequest = new BulkRequest();
                wineChunk.forEach(wine -> {
                    bulkRequest.add(new IndexRequest(ES_INDEX_NAME).source(wine.toJson(), XContentType.JSON));
                });
                BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                System.out.println("Bulk status: " + bulkResponse.status().getStatus());
                if (bulkResponse.hasFailures()) {
                    System.err.println(bulkResponse.buildFailureMessage());
                } else {
                    System.out.println(bulkResponse.getItems().length + " wines inserted!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (esClient != null) {
                try {
                    esClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
