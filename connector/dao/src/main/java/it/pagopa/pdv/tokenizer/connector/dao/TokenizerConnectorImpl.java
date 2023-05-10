package it.pagopa.pdv.tokenizer.connector.dao;

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
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

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
    public Mono<TokenDto> save(String pii, String namespace) {
        log.trace("[save] start");
        log.debug(CONFIDENTIAL_MARKER, "[save] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        return saveGlobalToken(pii)
                .flatMap(rootToken -> saveNamespacedToken(pii, namespace, rootToken)
                        .map(namespacedToken -> new TokenDto(namespacedToken, rootToken)))
                .doOnSuccess(tokenDto -> {
                    log.debug("[save] output = {}", tokenDto);
                    log.trace("[save] end");
                });
    }


    @SneakyThrows
    private Mono<String> saveGlobalToken(String pii) {
        GlobalFiscalCodeToken globalFiscalCodeToken = new GlobalFiscalCodeToken();
        globalFiscalCodeToken.setPii(pii);
        return Mono.fromFuture(globalTokenTable.updateItem(globalFiscalCodeToken))//TODO: good for performance??
                .map(GlobalFiscalCodeToken::getToken);
    }


    @SneakyThrows
    private Mono<String> saveNamespacedToken(String pii, String namespace, String rootToken) {
        NamespacedFiscalCodeToken namespacedFiscalCodeToken = new NamespacedFiscalCodeToken();
        namespacedFiscalCodeToken.setPii(pii);
        namespacedFiscalCodeToken.setNamespace(namespace);
        namespacedFiscalCodeToken.setGlobalToken(rootToken);
        return Mono.fromFuture(namespacedTokenTable.updateItem(namespacedFiscalCodeToken))//TODO: good for performance??
                .map(NamespacedFiscalCodeToken::getToken);
    }


    @SneakyThrows
    @Override
    public Mono<TokenDto> findById(String pii, String namespace) {
        log.trace("[findById] start");
        log.debug(CONFIDENTIAL_MARKER, "[findById] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        Key key = Key.builder()
                .partitionValue(pii)
                .sortValue(namespace)
                .build();
        return Mono.fromFuture(namespacedTokenTable.getItem(key))
                .filter(namespacedToken -> Status.ACTIVE.equals(namespacedToken.getStatus()))
                .mapNotNull(namespacedToken -> new TokenDto(namespacedToken.getToken(), namespacedToken.getGlobalToken()))
                .doOnSuccess(tokenDto -> {
                    log.debug("[findById] output = {}", tokenDto);
                    log.trace("[findById] end");
                });
    }


    @Override
    public Mono<String> findPiiByToken(String token, String namespace) {
        log.trace("[findPiiByToken] start");
        log.debug("[findPiiByToken] inputs: token = {}, namespace = {}", token, namespace);
        Assert.hasText(token, "A token is required");
        Assert.hasText(namespace, "A namespace is required");
        SdkPublisher<Page<NamespacedFiscalCodeToken>> publisher = namespacedTokenTable.index("gsi_token")
                .query(queryBuilder ->
                        queryBuilder.queryConditional(keyEqualTo(keyBuilder ->
                                keyBuilder.partitionValue(token))));
        return Mono.fromDirect(publisher)
                .flatMapIterable(Page::items)
                .filter(namespacedToken -> namespace.equals(namespacedToken.getNamespace()))
                .filter(namespacedToken -> !Status.PENDING_DELETE.equals(namespacedToken.getStatus()))
                .map(NamespacedFiscalCodeToken::getPii)
                .singleOrEmpty()
                .doOnSuccess(pii -> {
                    log.debug(CONFIDENTIAL_MARKER, "[findPiiByToken] output = {}", pii);
                    log.trace("[findPiiByToken] end");
                });
    }

}
