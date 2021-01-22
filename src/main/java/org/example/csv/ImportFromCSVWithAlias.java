package org.example.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.example.csv.beans.Wine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ImportFromCSVWithAlias {

    private static final String CSV_FILE_NAME = "/vins.csv";

    private static final String ES_HOSTNAME = "localhost";
    private static final int ES_PORT = 9200;
    private static final String ES_SCHEME = "http";
    private static final String ES_INDEX_ALIAS_NAME = "all-my-wines";
    private static final Integer ES_BULK_SIZE = 100;

    public static void main(String[] args) {
        RestHighLevelClient esClient = null;

        try {
            // Get wines from CSV file
            InputStream csvStream = ImportFromCSVWithAlias.class.getResourceAsStream(CSV_FILE_NAME);
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

            // Get previous index name
            GetAliasesRequest getAliasRequest = new GetAliasesRequest();
            getAliasRequest.aliases(ES_INDEX_ALIAS_NAME);
            GetAliasesResponse getAliasResponse = esClient.indices().getAlias(getAliasRequest, RequestOptions.DEFAULT);
            Set<String> oldIndexes = getAliasResponse.getAliases().keySet();
            System.out.println("Old indexes : " + oldIndexes);

            // Insert chunks into Elasticsearch with Bulk API
            boolean insertOk = true;
            String indexName = ES_INDEX_ALIAS_NAME + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            for (List<Wine> wineChunk : winesChunks) {
                BulkRequest bulkRequest = new BulkRequest();
                wineChunk.forEach(wine -> {
                    bulkRequest.add(new IndexRequest(indexName).source(wine.toJson(), XContentType.JSON));
                });
                BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                System.out.println("Bulk status: " + bulkResponse.status().getStatus());
                boolean hasFailures = bulkResponse.hasFailures();
                insertOk = insertOk && !hasFailures;
                if (hasFailures) {
                    System.err.println(bulkResponse.buildFailureMessage());
                } else {
                    System.out.println(bulkResponse.getItems().length + " wines inserted!");
                }
            }
            if (!insertOk) {
                System.err.println("Error while inserting wines into ES... :-(");
                System.exit(1);
            }

            // Add alias on the new index
            IndicesAliasesRequest createAliasRequest = new IndicesAliasesRequest();
            IndicesAliasesRequest.AliasActions aliasAction =
                    new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                            .index(indexName)
                            .alias(ES_INDEX_ALIAS_NAME);
            createAliasRequest.addAliasAction(aliasAction);
            AcknowledgedResponse indicesAliasesResponse = esClient.indices().updateAliases(createAliasRequest, RequestOptions.DEFAULT);
            if (!indicesAliasesResponse.isAcknowledged()) {
                System.err.println("Error while creating alias");
                System.exit(1);
            }

            // Remove old indexes
            // FIXME what if index deletion fails? Alias is duplicated!
            for (String oldIndex : oldIndexes) {
                DeleteIndexRequest request = new DeleteIndexRequest(oldIndex);
                AcknowledgedResponse deleteResponse = esClient.indices().delete(request, RequestOptions.DEFAULT);
                if (!deleteResponse.isAcknowledged()) {
                    System.err.println("Error deleting index: " + oldIndex);
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
