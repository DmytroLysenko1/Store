package org.example.contoller.integrationalTests;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WireMockTest(httpPort = 54321)
@DisplayName("Інтеграційні тести для класу ProductsController")
public class ProductsControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getProductList_ReturnsProductsListPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
                .queryParam("filter", "product")
                .with(user("j.dewar").roles("MANAGER"));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("product"))
                .willReturn(WireMock.ok("""
                        [
                            {"id": 1, "title": "Product №1", "details": "Description of product №1"},
                            {"id": 2, "title": "Product №2", "details": "Description of product №2"}
                        ]""").withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter", "product"),
                        model().attribute("products", List.of(
                                new Product(1, "Product №1", "Description of product №1"),
                                new Product(2, "Product №2", "Description of product №2")
                        ))
                );

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("product")));
    }
    @Test
    void getProductsList_ReturnsProductsListPage() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.
                get("/catalogue/products/list")
                        .queryParam("filter", "product")
                        .with(user("j.dewar").roles("MANAGER"));


        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("product"))
                .willReturn(WireMock.ok("""
                        [
                        {"id": 1, "title": "Product №1", "details": "Description of product №1"},
                        {"id": 2, "title": "Product №2", "details": "Description of product №2"}
                        ]
                        """).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));


        /*
         можна використовувати методи без приписки WireMock,
         але це зроблено для того,
         щоб не сплутати методи WireMock з методами Mockito
         */

        //when
        this.mockMvc.perform(requestBuilder)
        //then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter", "product"),
                        model().attribute("products",
                                List.of(new Product(
                                        1, "Product №1", "Description of product №1"),
                                        new Product(
                                                2, "Product №2", "Description of product №2")))
                        );

        WireMock.verify(WireMock.getRequestedFor(
                        WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("product")));
    }

}
