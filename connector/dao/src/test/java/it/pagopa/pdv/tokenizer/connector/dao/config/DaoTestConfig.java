package it.pagopa.pdv.tokenizer.connector.dao.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import it.pagopa.pdv.tokenizer.connector.dao.TokenizerConnectorImpl;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@TestConfiguration
@Import(DynamoDBConfig.class)
public class DaoTestConfig {

    public static void dynamoDBLocalSetup(AmazonDynamoDB client, DynamoDBMapper dynamoDBMapper) {
        ListTablesResult tablesResult = client.listTables();
        if (!tablesResult.getTableNames().contains(TokenizerConnectorImpl.TABLE_NAME)) {
            CreateTableRequest tableRequest = createCreateTableRequest(dynamoDBMapper);
            client.createTable(tableRequest);
        }
    }

    private static CreateTableRequest createCreateTableRequest(DynamoDBMapper dynamoDBMapper) {
        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(NamespacedFiscalCodeToken.class);
        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

        if (tableRequest.getGlobalSecondaryIndexes() != null) {
            tableRequest.getGlobalSecondaryIndexes().forEach(gsi -> {
                gsi.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
                gsi.getProjection().setProjectionType(ProjectionType.ALL);
            });
        }
        return tableRequest;
    }

    @Test
    void generateCreateTableRequest() throws IOException {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(null);
        CreateTableRequest tableRequest = createCreateTableRequest(dynamoDBMapper);
        Path basePath = Paths.get("target/test/dynamodb-local-table-template/");
        Files.createDirectories(basePath);
        Files.write(basePath.resolve("Token.json"), tableRequest.toString().getBytes());
    }

}
