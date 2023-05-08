package it.pagopa.pdv.tokenizer.connector.dao.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import it.pagopa.pdv.tokenizer.connector.dao.TokenizerConnectorImpl;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.internal.operations.CreateTableOperation;
import software.amazon.awssdk.enhanced.dynamodb.internal.operations.DefaultOperationContext;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@TestConfiguration
@Import(DynamoDBConfig.class)
public class DaoTestConfig {

    @SneakyThrows
    public static void dynamoDBLocalSetup(DynamoDbAsyncClient dynamoDbAsyncClient, DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient) {
        ListTablesResponse listTablesResponse = dynamoDbAsyncClient.listTables().get();
        if (!listTablesResponse.tableNames().contains(TokenizerConnectorImpl.TABLE_NAME)) {
            BeanTableSchema<NamespacedFiscalCodeToken> tableSchema = TableSchema.fromBean(NamespacedFiscalCodeToken.class);
            DynamoDbAsyncTable<NamespacedFiscalCodeToken> table = dbEnhancedAsyncClient.table(TokenizerConnectorImpl.TABLE_NAME, tableSchema);
            table.createTable(createTableRequest(tableSchema));
        }
    }

    @SneakyThrows
    private static CreateTableEnhancedRequest createTableRequest(BeanTableSchema<NamespacedFiscalCodeToken> tableSchema) {
        return CreateTableEnhancedRequest.builder()
                .provisionedThroughput(provisionedThroughputBuilder -> provisionedThroughputBuilder
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .globalSecondaryIndices(tableSchema.tableMetadata().indices().stream()
                        .filter(indexMetadata -> indexMetadata.name().startsWith("gsi_"))
                        .map(indexMetadata -> EnhancedGlobalSecondaryIndex.builder()
                                .indexName(indexMetadata.name())
                                .provisionedThroughput(provisionedThroughputBuilder -> provisionedThroughputBuilder
                                        .readCapacityUnits(5L)
                                        .writeCapacityUnits(5L)
                                        .build())
                                .projection(projBuilder -> projBuilder.projectionType(ProjectionType.ALL))
                                .build()).collect(Collectors.toList()))
                .build();
    }

    @Test
    void generateCreateTableRequest() throws IOException {
        BeanTableSchema<NamespacedFiscalCodeToken> tableSchema = TableSchema.fromBean(NamespacedFiscalCodeToken.class);
        CreateTableEnhancedRequest createTableEnhancedRequest = createTableRequest(tableSchema);
        CreateTableOperation<NamespacedFiscalCodeToken> operation = CreateTableOperation.create(createTableEnhancedRequest);
        OperationContext context = DefaultOperationContext.create(TokenizerConnectorImpl.TABLE_NAME, TableMetadata.primaryIndexName());
        CreateTableRequest createTableRequest = operation.generateRequest(tableSchema, context, null);
        Path basePath = Paths.get("target/test/dynamodb-local-table-template/");
        Files.createDirectories(basePath);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Files.write(basePath.resolve("Token.json"), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(createTableRequest.toBuilder()));
    }

}
