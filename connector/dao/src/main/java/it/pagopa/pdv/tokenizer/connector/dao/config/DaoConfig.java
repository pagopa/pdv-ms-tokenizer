package it.pagopa.pdv.tokenizer.connector.dao.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import it.pagopa.pdv.tokenizer.connector.dao.TokenizerConnectorImpl;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/dao-config.properties")
class DaoConfig {

    @Value("${amazon.region}")
    private String region;

    @Value("${dynamodb.endpoint.url}")
    private String dynamoDBEndpoint;


    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamoDBEndpoint, region))
                .build();
    }


//    @Bean
//    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
//        return new DynamoDBMapper(amazonDynamoDB);
//    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        dynamoDBLocalSetup(amazonDynamoDB, dynamoDBMapper);
        return dynamoDBMapper;
    }


    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDB(amazonDynamoDB);
    }


    // FIXME remove
    private void dynamoDBLocalSetup(AmazonDynamoDB client, DynamoDBMapper dynamoDBMapper) {
        try {
            ListTablesResult tablesResult = client.listTables();
            if (!tablesResult.getTableNames().contains(TokenizerConnectorImpl.TABLE_NAME)) {
                // Single table design: any of the domain class will contain the data needed to create the table
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
