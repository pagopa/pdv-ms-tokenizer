package it.pagopa.pdv.tokenizer.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.pdv.tokenizer.web.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "people", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "people")
public class PersonController {


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.getUserByInternalId.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.getUserByInternalId.notes}")
    @GetMapping(value = "search")
    @ResponseStatus(HttpStatus.OK)
    public UserResource getUserByInternalId(@ApiParam("${swagger.ms-tokenizer.token.model.id}")
                                            @PathVariable("id")
                                                    UUID id,
                                            @RequestBody FilterCriteria request) {
        return null;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.updateUserFieldsByInternalId.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.updateUserFieldsByInternalId.notes}")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserFieldsByInternalId(@ApiParam("${swagger.ms-tokenizer.token.model.id}")
                                             @PathVariable("id")
                                                     UUID id,
                                             @RequestBody
                                                     MutableUserFieldsDto request) {
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.searchUser.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.searchUser.notes}")
    @PostMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public UserResource searchUser(@RequestBody
                                           UserSearchDto request,
                                   @ApiParam("${swagger.ms-tokenizer.token.model.fields}")
                                   @RequestParam("fields")
                                           List<String> fields) {
        return null;
    }


    @ApiOperation(value = "${swagger.ms-tokenizer.tokens.api.updateOrCreateUserByExternalId.summary}",
            notes = "${swagger.ms-tokenizer.tokens.api.updateOrCreateUserByExternalId.notes}")
    @PutMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public UserInternalId updateOrCreateUserByExternalId(@RequestBody
                                                                 UserDto request) {
        return null;
    }

}
