package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class LocalDBCreationExtension implements BeforeAllCallback, AfterAllCallback {

    private DynamoDBProxyServer dynamoDBProxyServer;

    public LocalDBCreationExtension() {
        System.setProperty("sqlite4java.library.path", "src/test/native-libs");
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        dynamoDBProxyServer = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", "8000"});
        dynamoDBProxyServer.start();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        try {
            dynamoDBProxyServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
