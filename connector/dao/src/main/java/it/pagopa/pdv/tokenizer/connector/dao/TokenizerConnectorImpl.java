package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.dao.model.GlobalFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

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
            Item item = table.getItem(globalFiscalCodeTableMapper.hashKey().name(),
                    globalFiscalCodeToken.getPii(),
                    globalFiscalCodeTableMapper.rangeKey().name(),
                    GlobalFiscalCodeToken.NAMESPACE,
                    "#0",
                    Map.of("#0", GlobalFiscalCodeToken.Fields.token));
            tokenDto.setRootToken(item.getString(GlobalFiscalCodeToken.Fields.token));
        }
        NamespacedFiscalCodeToken namespacedFiscalCodeToken = new NamespacedFiscalCodeToken();
        namespacedFiscalCodeToken.setPii(pii);
        namespacedFiscalCodeToken.setNamespace(namespace);
        namespacedFiscalCodeToken.setGlobalToken(tokenDto.getRootToken());
        try {
            namespacedFiscalCodeTableMapper.saveIfNotExists(namespacedFiscalCodeToken);//TODO: good for performance??
            tokenDto.setToken(namespacedFiscalCodeToken.getToken());
        } catch (ConditionalCheckFailedException e) {
            Item item = table.getItem(namespacedFiscalCodeTableMapper.hashKey().name(),
                    namespacedFiscalCodeToken.getPii(),
                    namespacedFiscalCodeTableMapper.rangeKey().name(),
                    namespacedFiscalCodeToken.getNamespace(),
                    "#0",
                    Map.of("#0", NamespacedFiscalCodeToken.Fields.token));
            tokenDto.setToken(item.getString(NamespacedFiscalCodeToken.Fields.token));
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
        Item item = table.getItem(namespacedFiscalCodeTableMapper.hashKey().name(),
                pii,
                namespacedFiscalCodeTableMapper.rangeKey().name(),
                namespace,
                "#0,#1",
                Map.of("#0", NamespacedFiscalCodeToken.Fields.token,
                        "#1", NamespacedFiscalCodeToken.Fields.globalToken)
        );
        if (item != null) {
            TokenDto tokenDto = new TokenDto();
            tokenDto.setToken(item.getString(NamespacedFiscalCodeToken.Fields.token));
            tokenDto.setRootToken(item.getString(NamespacedFiscalCodeToken.Fields.globalToken));
            result = Optional.of(tokenDto);
        }
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
                .withProjectionExpression(namespacedFiscalCodeTableMapper.hashKey().name()));
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
