package it.pagopa.pdv.tokenizer.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
@ActiveProfiles("dev-local")
class SwaggerConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private WebTestClient webTestClient;


    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }


    @Test
    void swaggerSpringPlugin() throws Exception {
        FluxExchangeResult<String> exchangeResult = webTestClient.get()
                .uri("/v3/api-docs")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(String.class);
        byte[] responseBodyContent = exchangeResult.getResponseBodyContent();
        assertNotNull(responseBodyContent);
        String content = new String(responseBodyContent);
        assertFalse(content.isBlank());
        assertFalse(content.contains("${"), "Generated swagger contains placeholders");
        Object swagger = objectMapper.readValue(responseBodyContent, Object.class);
        String formatted = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(swagger);
        Path basePath = Paths.get("src/main/resources/swagger/");
        Files.createDirectories(basePath);
        Files.write(basePath.resolve("api-docs.json"), formatted.getBytes());
//        webTestClient.perform(MockMvcRequestBuilders.get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andDo((result) -> {
//                    assertNotNull(result);
//                    assertNotNull(result.getResponse());
//                    final String content = result.getResponse().getContentAsString();
//                    assertFalse(content.isBlank());
//                    assertFalse(content.contains("${"), "Generated swagger contains placeholders");
//                    Object swagger = objectMapper.readValue(result.getResponse().getContentAsString(), Object.class);
//                    String formatted = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(swagger);
//                    Path basePath = Paths.get("src/main/resources/swagger/");
//                    Files.createDirectories(basePath);
//                    Files.write(basePath.resolve("api-docs.json"), formatted.getBytes());
//                });
    }

}
