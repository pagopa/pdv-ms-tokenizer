package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.dao.model.GlobalFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.Status;
import it.pagopa.pdv.tokenizer.connector.exception.TooManyRequestsException;
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
        tokenDto.setRootToken(saveGlobalToken(pii));
        tokenDto.setToken(saveNamespacedToken(pii, namespace, tokenDto.getRootToken()));
        log.debug("[save] output = {}", tokenDto);
        log.trace("[save] end");
        return tokenDto;
    }


    private String saveGlobalToken(String pii) {
        String rootToken;
        GlobalFiscalCodeToken globalFiscalCodeToken = new GlobalFiscalCodeToken();
        globalFiscalCodeToken.setPii(pii);
        try {
            globalFiscalCodeTableMapper.saveIfNotExists(globalFiscalCodeToken);//TODO: good for performance??
            rootToken = globalFiscalCodeToken.getToken();
        } catch (ConditionalCheckFailedException e) {
            GlobalFiscalCodeToken globalTokenFound = globalFiscalCodeTableMapper.load(globalFiscalCodeToken.getPii(), globalFiscalCodeToken.getNamespace());
            rootToken = globalTokenFound.getToken();
            if (Status.PENDING_DELETE.equals(globalTokenFound.getStatus())) {
                reactivateToken(globalFiscalCodeTableMapper.hashKey().name(),
                        globalFiscalCodeToken.getPii(),
                        globalFiscalCodeTableMapper.rangeKey().name(),
                        globalFiscalCodeToken.getNamespace(),
                        GlobalFiscalCodeToken.Fields.status);
            }
        }
        catch(ProvisionedThroughputExceededException e){
            throw new TooManyRequestsException(e.getCause());
        }
        return rootToken;
    }


    private String saveNamespacedToken(String pii, String namespace, String rootToken) {
        String token;
        NamespacedFiscalCodeToken namespacedFiscalCodeToken = new NamespacedFiscalCodeToken();
        namespacedFiscalCodeToken.setPii(pii);
        namespacedFiscalCodeToken.setNamespace(namespace);
        namespacedFiscalCodeToken.setGlobalToken(rootToken);
        try {
            namespacedFiscalCodeTableMapper.saveIfNotExists(namespacedFiscalCodeToken);//TODO: good for performance??
            token = namespacedFiscalCodeToken.getToken();
        } catch (ConditionalCheckFailedException e) {
            NamespacedFiscalCodeToken namespacedTokenFound =
                    namespacedFiscalCodeTableMapper.load(namespacedFiscalCodeToken.getPii(), namespacedFiscalCodeToken.getNamespace());
            token = namespacedTokenFound.getToken();
            if (Status.PENDING_DELETE.equals(namespacedTokenFound.getStatus())) {
                reactivateToken(namespacedFiscalCodeTableMapper.hashKey().name(),
                        namespacedFiscalCodeToken.getPii(),
                        namespacedFiscalCodeTableMapper.rangeKey().name(),
                        namespacedFiscalCodeToken.getNamespace(),
                        NamespacedFiscalCodeToken.Fields.status);
            }
        }
        catch(ProvisionedThroughputExceededException e){
            throw new TooManyRequestsException(e.getCause());
        }
        return token;
    }


    private void reactivateToken(String hashKeyName, Object hashKeyValue,
                                 String rangeKeyName, Object rangeKeyValue,
                                 String statusFieldName) {
        PrimaryKey primaryKey = new PrimaryKey(hashKeyName,
                hashKeyValue,
                rangeKeyName,
                rangeKeyValue);
        try{
            table.updateItem(new UpdateItemSpec()
                    .withPrimaryKey(primaryKey)
                    .withExpressionSpec(new ExpressionSpecBuilder()
                            .addUpdate(S(statusFieldName).set(Status.ACTIVE.toString()))
                            .withCondition(attribute_exists(hashKeyName)
                                    .and(attribute_exists(rangeKeyName))
                                    .and(S(statusFieldName).eq(Status.PENDING_DELETE.toString())))
                            .buildForUpdate()));
        }
        catch(ProvisionedThroughputExceededException e){
            throw new TooManyRequestsException(e.getCause());
        }

    }


    @Override
    public Optional<TokenDto> findById(String pii, String namespace) {
        log.trace("[findById] start");
        log.debug(CONFIDENTIAL_MARKER, "[findById] inputs: pii = {}, namespace = {}", pii, namespace);
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        Optional<TokenDto> result;
        try {
            result = Optional.ofNullable(namespacedFiscalCodeTableMapper.load(pii, namespace))
                    .filter(p -> Status.ACTIVE.equals(p.getStatus()))
                    .map(namespacedFiscalCodeToken -> {
                        TokenDto tokenDto = new TokenDto();
                        tokenDto.setToken(namespacedFiscalCodeToken.getToken());
                        tokenDto.setRootToken(namespacedFiscalCodeToken.getGlobalToken());
                        return tokenDto;
                    });
        }
        catch(ProvisionedThroughputExceededException e){
            throw new TooManyRequestsException(e.getCause());
        }
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
        Optional<String> pii = Optional.empty();
        try {
            Index index = table.getIndex("gsi_token");
            ItemCollection<QueryOutcome> itemCollection = index.query(new QuerySpec()
                    .withHashKey(NamespacedFiscalCodeToken.Fields.token, token)
                    .withExpressionSpec(new ExpressionSpecBuilder()
                            .withCondition(S(NamespacedFiscalCodeToken.Fields.status).ne(Status.PENDING_DELETE.toString())
                                    .and(S(namespacedFiscalCodeTableMapper.rangeKey().name()).eq(namespace)))
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
        }
        catch(ProvisionedThroughputExceededException e){
            throw new TooManyRequestsException(e.getCause());
        }
        log.debug(CONFIDENTIAL_MARKER, "[findPiiByToken] output = {}", pii);
        log.trace("[findPiiByToken] end");
        return pii;
    }

}
