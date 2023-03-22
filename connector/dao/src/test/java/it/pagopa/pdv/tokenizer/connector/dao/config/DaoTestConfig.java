package it.pagopa.pdv.tokenizer.connector.dao.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import it.pagopa.pdv.tokenizer.connector.dao.TokenizerConnectorImpl;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import lombok.Data;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
                TableDescription table = client.describeTable(tableRequest.getTableName()).getTable();

                TableTestModel tableTestModel = new TableTestModel();
                tableTestModel.setAttributeDefinitions(table.getAttributeDefinitions());
                tableTestModel.setTableName(table.getTableName());
                tableTestModel.setKeySchema(table.getKeySchema());
                tableTestModel.getProvisionedThroughput().setReadCapacityUnits(5);
                tableTestModel.getProvisionedThroughput().setWriteCapacityUnits(5);
                List<TableTestModel.GlobalSecondaryIndex> indexList = new ArrayList<>();
                tableTestModel.setGlobalSecondaryIndexes(indexList);
                int idx = 0;
                for(GlobalSecondaryIndexDescription globalSecondaryIndex: table.getGlobalSecondaryIndexes()){
                    tableTestModel.getGlobalSecondaryIndexes().add(new TableTestModel.GlobalSecondaryIndex());
                    tableTestModel.getGlobalSecondaryIndexes().get(idx).setIndexName(globalSecondaryIndex.getIndexName());
                    tableTestModel.getGlobalSecondaryIndexes().get(idx).getProjection().setProjectionType(globalSecondaryIndex.getProjection().getProjectionType());
                    tableTestModel.getGlobalSecondaryIndexes().get(idx).setKeySchema(globalSecondaryIndex.getKeySchema());
                    tableTestModel.getGlobalSecondaryIndexes().get(idx).getProvisionedThroughput().setReadCapacityUnits(5);
                    tableTestModel.getGlobalSecondaryIndexes().get(idx).getProvisionedThroughput().setWriteCapacityUnits(5);
                    idx++;
                }

                String formatted = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tableTestModel);
                Path basePath = Paths.get("target/test/dynamodb-local-table-template/");
                Files.createDirectories(basePath);
                Files.write(basePath.resolve("Token.json"), formatted.getBytes());
            }
            catch (Exception e){
                throw(new RuntimeException());
            }
        }
    }
    @Data
    static class TableTestModel{
        private List<AttributeDefinition> attributeDefinitions;
        private String tableName;
        private List<KeySchemaElement> keySchema;
        private List<GlobalSecondaryIndex> globalSecondaryIndexes;
        private ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput();

        @Data
        static class ProvisionedThroughput{
            private int readCapacityUnits;
            private int writeCapacityUnits;
        }
        @Data
        static class GlobalSecondaryIndex{
            private String indexName;
            private List<KeySchemaElement> keySchema;
            private Projection projection = new Projection();
            private ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput();

            @Data
            class Projection{
                private String projectionType;
            }

        }

    }

}
