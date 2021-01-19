package org.example.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
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
import org.example.csv.beans.Wine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ImportFromCSV {

    private static final String CSV_FILE_NAME = "/vins.csv";

    private static final String ES_HOSTNAME = "localhost";
    private static final int ES_PORT = 9200;
    private static final String ES_SCHEME = "http";
    private static final String ES_INDEX_NAME = "my-wines";
    private static final Integer ES_BULK_SIZE = 100;

    public static void main(String[] args) {
        RestHighLevelClient esClient = null;

        try {
            // Get wines from CSV file
            InputStream csvStream = ImportFromCSV.class.getResourceAsStream(CSV_FILE_NAME);
            CsvToBean<Wine> csvToBean = new CsvToBeanBuilder<Wine>(new InputStreamReader(csvStream))
                    .withSeparator(',')
                    .withType(Wine.class)
                    .build();
            List<Wine> wines = csvToBean.parse();

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
