package it.pagopa.pdv.tokenizer.connector.dao.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import it.pagopa.pdv.tokenizer.connector.dao.TokenizerConnectorImpl;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(DynamoDBConfig.class)
public class DaoTestConfig {

    public static void dynamoDBLocalSetup(AmazonDynamoDB client, DynamoDBMapper dynamoDBMapper) {
        ListTablesResult tablesResult = client.listTables();
        if (!tablesResult.getTableNames().contains(TokenizerConnectorImpl.TABLE_NAME)) {
            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(NamespacedFiscalCodeToken.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

            if (tableRequest.getGlobalSecondaryIndexes() != null) {
                tableRequest.getGlobalSecondaryIndexes().forEach(gsi -> {
                    gsi.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
                    gsi.getProjection().setProjectionType(ProjectionType.ALL);
                });
            }

            client.createTable(tableRequest);
        }
    }

}
