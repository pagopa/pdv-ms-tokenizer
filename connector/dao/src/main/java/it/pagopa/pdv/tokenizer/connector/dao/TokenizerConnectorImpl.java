package it.pagopa.pdv.tokenizer.connector.dao;

import io.reactivex.rxjava3.core.Flowable;
import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.dao.model.GlobalFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.Status;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

@Slf4j
@Service
public class TokenizerConnectorImpl implements TokenizerConnector {

    public static final String TABLE_NAME = "Token";
    private static final Marker CONFIDENTIAL_MARKER = MarkerFactory.getMarker("CONFIDENTIAL");

    private final DynamoDbAsyncClient dbAsyncClient;
    private final BeanTableSchema<NamespacedFiscalCodeToken> namespacedTokenTableSchema;
    private final DynamoDbAsyncTable<NamespacedFiscalCodeToken> namespacedTokenTable;
    private final BeanTableSchema<GlobalFiscalCodeToken> globalTokenTableSchema;
    private final DynamoDbAsyncTable<GlobalFiscalCodeToken> globalTokenTable;


    TokenizerConnectorImpl(DynamoDbAsyncClient dbAsyncClient, DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient) {
        this.dbAsyncClient = dbAsyncClient;
        log.trace("Initializing {}", TokenizerConnectorImpl.class.getSimpleName());
        namespacedTokenTableSchema = TableSchema.fromBean(NamespacedFiscalCodeToken.class);
        namespacedTokenTable = dbEnhancedAsyncClient.table(TABLE_NAME, namespacedTokenTableSchema);
        globalTokenTableSchema = TableSchema.fromBean(GlobalFiscalCodeToken.class);
        globalTokenTable = dbEnhancedAsyncClient.table(TABLE_NAME, globalTokenTableSchema);
    }


    @Override
    public TokenDto save(String pii, String namespace) {
        log.trace("[save] start");
        log.debug(CONFIDENTIAL_MARKER, "[save] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        TokenDto tokenDto = new TokenDto();
        tokenDto.setRootToken(saveGlobalToken(pii));
        tokenDto.setToken(saveNamespacedToken(pii, namespace, tokenDto.getRootToken()));
        log.debug("[save] output = {}", tokenDto);
        log.trace("[save] end");
        return tokenDto;
    }


    @SneakyThrows
    private String saveGlobalToken(String pii) {
        GlobalFiscalCodeToken globalFiscalCodeToken = new GlobalFiscalCodeToken();
        globalFiscalCodeToken.setPii(pii);
        CompletableFuture<GlobalFiscalCodeToken> completableFuture = globalTokenTable.updateItem(globalFiscalCodeToken);//TODO: good for performance??
        return completableFuture.get().getToken();
    }


    @SneakyThrows
    private String saveNamespacedToken(String pii, String namespace, String rootToken) {
        NamespacedFiscalCodeToken namespacedFiscalCodeToken = new NamespacedFiscalCodeToken();
        namespacedFiscalCodeToken.setPii(pii);
        namespacedFiscalCodeToken.setNamespace(namespace);
        namespacedFiscalCodeToken.setGlobalToken(rootToken);
        CompletableFuture<NamespacedFiscalCodeToken> completableFuture = namespacedTokenTable.updateItem(namespacedFiscalCodeToken);//TODO: good for performance??
        return completableFuture.get().getToken();
    }


    @SneakyThrows
    @Override
    public Optional<TokenDto> findById(String pii, String namespace) {
        log.trace("[findById] start");
        log.debug(CONFIDENTIAL_MARKER, "[findById] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        Key key = Key.builder()
                .partitionValue(pii)
                .sortValue(namespace)
                .build();
        CompletableFuture<NamespacedFiscalCodeToken> response = namespacedTokenTable.getItem(key);
        Optional<TokenDto> result = Optional.ofNullable(response.get())
                .filter(p -> Status.ACTIVE.equals(p.getStatus()))
                .map(namespacedFiscalCodeToken -> {
                    TokenDto tokenDto = new TokenDto();
                    tokenDto.setToken(namespacedFiscalCodeToken.getToken());
                    tokenDto.setRootToken(namespacedFiscalCodeToken.getGlobalToken());
                    return tokenDto;
                });
        log.debug("[findById] output = {}", result);
        log.trace("[findById] end");
        return result;
    }


    @Override
    public Optional<String> findPiiByToken(String token, String namespace) {
        log.trace("[findPiiByToken] start");
        log.debug("[findPiiByToken] inputs: token = {}, namespace = {}", token, namespace);
        Assert.hasText(token, "A token is required");
        Assert.hasText(namespace, "A namespace is required");
        SdkPublisher<Page<NamespacedFiscalCodeToken>> publisher = namespacedTokenTable.index("gsi_token")
                .query(queryBuilder ->
                        queryBuilder.queryConditional(keyEqualTo(keyBuilder ->
                                keyBuilder.partitionValue(token))));
        final Optional<String> pii = Flowable.fromPublisher(publisher)
                .flatMapIterable(Page::items)
                .filter(namespacedToken -> namespace.equals(namespacedToken.getNamespace()))
                .filter(namespacedToken -> !Status.PENDING_DELETE.equals(namespacedToken.getStatus()))
                .map(NamespacedFiscalCodeToken::getPii)
                .map(Optional::ofNullable)
                .first(Optional.empty())
                .blockingGet();
        log.debug(CONFIDENTIAL_MARKER, "[findPiiByToken] output = {}", pii);
        log.trace("[findPiiByToken] end");
        return pii;
    }

}
