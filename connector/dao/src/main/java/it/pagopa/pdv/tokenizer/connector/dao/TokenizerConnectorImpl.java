package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.dao.model.GlobalFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.Status;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Optional;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.attribute_exists;

@Slf4j
@Service
public class TokenizerConnectorImpl implements TokenizerConnector {

    public static final String TABLE_NAME = "Token";
    private static final Marker CONFIDENTIAL_MARKER = MarkerFactory.getMarker("CONFIDENTIAL");

    private final Table table;
    private final DynamoDBTableMapper<NamespacedFiscalCodeToken, String, String> namespacedFiscalCodeTableMapper;
    private final DynamoDBTableMapper<GlobalFiscalCodeToken, String, String> globalFiscalCodeTableMapper;


    TokenizerConnectorImpl(DynamoDBMapper dynamoDBMapper, DynamoDB dynamoDB) {
        log.trace("Initializing {}", TokenizerConnectorImpl.class.getSimpleName());
        table = dynamoDB.getTable(TABLE_NAME);
        namespacedFiscalCodeTableMapper = dynamoDBMapper.newTableMapper(NamespacedFiscalCodeToken.class);
        globalFiscalCodeTableMapper = dynamoDBMapper.newTableMapper(GlobalFiscalCodeToken.class);
    }


    @Override
    public TokenDto save(String pii, String namespace) {
        log.trace("[save] start");
        log.debug(CONFIDENTIAL_MARKER, "[save] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        TokenDto tokenDto = new TokenDto();
        GlobalFiscalCodeToken globalFiscalCodeToken = new GlobalFiscalCodeToken();
        globalFiscalCodeToken.setPii(pii);
        try {
            globalFiscalCodeTableMapper.saveIfNotExists(globalFiscalCodeToken);//TODO: good for performance??
            tokenDto.setRootToken(globalFiscalCodeToken.getToken());
        } catch (ConditionalCheckFailedException e) {
            GlobalFiscalCodeToken globalTokenFound = globalFiscalCodeTableMapper.load(globalFiscalCodeToken.getPii(), globalFiscalCodeToken.getNamespace());
            tokenDto.setRootToken(globalTokenFound.getToken());
            if (Status.PENDING_DELETE.equals(globalTokenFound.getStatus())) {
                PrimaryKey primaryKey = new PrimaryKey(globalFiscalCodeTableMapper.hashKey().name(),
                        globalFiscalCodeToken.getPii(),
                        globalFiscalCodeTableMapper.rangeKey().name(),
                        globalFiscalCodeToken.getNamespace());
                table.updateItem(new UpdateItemSpec()
                        .withPrimaryKey(primaryKey)
                        .withExpressionSpec(new ExpressionSpecBuilder()
                                .addUpdate(S(GlobalFiscalCodeToken.Fields.status).set(Status.ACTIVE.toString()))
                                .withCondition(attribute_exists(globalFiscalCodeTableMapper.hashKey().name())
                                        .and(attribute_exists(globalFiscalCodeTableMapper.rangeKey().name()))
                                        .and(S(GlobalFiscalCodeToken.Fields.status).eq(Status.PENDING_DELETE.toString())))
                                .buildForUpdate()));
            }
        }
        NamespacedFiscalCodeToken namespacedFiscalCodeToken = new NamespacedFiscalCodeToken();
        namespacedFiscalCodeToken.setPii(pii);
        namespacedFiscalCodeToken.setNamespace(namespace);
        namespacedFiscalCodeToken.setGlobalToken(tokenDto.getRootToken());
        try {
            namespacedFiscalCodeTableMapper.saveIfNotExists(namespacedFiscalCodeToken);//TODO: good for performance??
            tokenDto.setToken(namespacedFiscalCodeToken.getToken());
        } catch (ConditionalCheckFailedException e) {
            NamespacedFiscalCodeToken namespacedTokenFound =
                    namespacedFiscalCodeTableMapper.load(namespacedFiscalCodeToken.getPii(), namespacedFiscalCodeToken.getNamespace());
            tokenDto.setToken(namespacedTokenFound.getToken());
            if (Status.PENDING_DELETE.equals(namespacedTokenFound.getStatus())) {
                PrimaryKey primaryKey = new PrimaryKey(namespacedFiscalCodeTableMapper.hashKey().name(),
                        namespacedFiscalCodeToken.getPii(),
                        namespacedFiscalCodeTableMapper.rangeKey().name(),
                        namespacedFiscalCodeToken.getNamespace());
                table.updateItem(new UpdateItemSpec()
                        .withPrimaryKey(primaryKey)
                        .withExpressionSpec(new ExpressionSpecBuilder()
                                .addUpdate(S(NamespacedFiscalCodeToken.Fields.status).set(Status.ACTIVE.toString()))
                                .withCondition(attribute_exists(namespacedFiscalCodeTableMapper.hashKey().name())
                                        .and(attribute_exists(namespacedFiscalCodeTableMapper.rangeKey().name()))
                                        .and(S(NamespacedFiscalCodeToken.Fields.status).eq(Status.PENDING_DELETE.toString())))
                                .buildForUpdate()));
            }
        }
        log.debug("[save] output = {}", tokenDto);
        log.trace("[save] end");
        return tokenDto;
    }


    @Override
    public Optional<TokenDto> findById(String pii, String namespace) {
        log.trace("[findById] start");
        log.debug(CONFIDENTIAL_MARKER, "[findById] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        Optional<TokenDto> result = Optional.empty();
        result = Optional.ofNullable(namespacedFiscalCodeTableMapper.load(pii, namespace))
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
    public Optional<String> findPiiByToken(String token) {
        log.trace("[findPiiByToken] start");
        log.debug("[findPiiByToken] inputs: token = {}", token);
        Assert.hasText(token, "A token is required");
        Optional<String> pii = Optional.empty();
        Index index = table.getIndex("gsi_token");
        ItemCollection<QueryOutcome> itemCollection = index.query(new QuerySpec()
                .withHashKey(NamespacedFiscalCodeToken.Fields.token, token)
                .withExpressionSpec(new ExpressionSpecBuilder()
                        .withCondition(S(NamespacedFiscalCodeToken.Fields.status).ne(Status.PENDING_DELETE.toString()))
                        .addProjection(namespacedFiscalCodeTableMapper.hashKey().name())
                        .buildForQuery())
        );
        Iterator<Page<Item, QueryOutcome>> iterator = itemCollection.pages().iterator();
        if (iterator.hasNext()) {
            Page<Item, QueryOutcome> page = iterator.next();
            if (page.getLowLevelResult().getItems().size() == 1) {
                pii = Optional.ofNullable(page.getLowLevelResult().getItems().get(0).getString(namespacedFiscalCodeTableMapper.hashKey().name()));
            }
        }
        log.debug(CONFIDENTIAL_MARKER, "[findPiiByToken] output = {}", pii);
        log.trace("[findPiiByToken] end");
        return pii;
    }

}
