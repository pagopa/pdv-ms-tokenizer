package it.pagopa.pdv.tokenizer.connector.dao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
class DynamoDBConfig {

    @Configuration
    @Profile("!dev-local")
    static class Cloud {

        @Bean
        public DynamoDbAsyncClient dynamoDbAsyncClient() {
            return DynamoDbAsyncClient.builder().build();
        }


        @Bean
        public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient() {
            return DynamoDbEnhancedAsyncClient.builder()
                    .dynamoDbClient(dynamoDbAsyncClient())
                    .build();
        }

    }


    @Configuration
    @Profile("dev-local")
    @PropertySource("classpath:config/dynamodb-config.properties")
    static class DevLocal {

        @Value("${amazon.access.key}")
        private String accessKey;

        @Value("${amazon.access.secretkey}")
        private String secretKey;

        @Value("${amazon.region}")
        private String region;

        @Value("${dynamodb.endpoint.url}")
        private String dynamoDBEndpoint;


        @Bean
        public DynamoDbAsyncClient dynamoDbAsyncClient() {
            return DynamoDbAsyncClient.builder()
                    .region(Region.EU_SOUTH_1)
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                    .endpointOverride(URI.create(dynamoDBEndpoint))
                    .build();
        }


        @Bean
        public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient() {
            return DynamoDbEnhancedAsyncClient.builder()
                    .dynamoDbClient(dynamoDbAsyncClient())
                    .build();
        }

    }

}
