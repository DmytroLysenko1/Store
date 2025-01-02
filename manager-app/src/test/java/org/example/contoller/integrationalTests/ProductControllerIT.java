package org.example.contoller.integrationalTests;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.contoller.payload.UpdateProductPayload;
import org.example.entity.Product;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WireMockTest(httpPort = 54321)
public class ProductControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getProduct_ProductExists_ReturnsProductPage() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1")
                        .with(user("j.dewar").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id" : 1,
                            "title" : "Product",
                            "details" : "Product description"
                    }""")));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isOk(),
                        view().name("/catalogue/products/product"),
                        model().attribute("product",
                                new Product(1, "Product", "Product description")));
    }

    @Test
    void getProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1")
                        .with(user("j.dewar").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.notFound()));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isNotFound(),
                        view().name("error/404"),
                        model().attribute("error", "Product not found"));
    }

    @Test
    void getProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1")
                        .with(user("j.daniels"));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void getProductEditPage_ProductExists_ReturnsProductEditPage() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1")
                        .with(user("j.dewar").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id" : 1,
                            "title" : "Product",
                            "details" : "Product description"
                    }""")));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/edit"),
                        model().attribute("product",
                                new Product(1, "Product", "Product description")));
    }

    @Test
    void getProductEditPage_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1")
                        .with(user("j.dewar").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.notFound()));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isNotFound(),
                        view().name("error/404"),
                        model().attribute("error", "Product not found"));

    }

    @Test
    void getProductEditPage_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1/edit")
                        .with(user("j.daniels"));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void updateProduct_RequestIsValid_RedirectsToProductPage() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1/edit")
                        .param("title", "New title")
                        .param("details", "New product description")
                        .with(user("j.dewar").roles("MANAGER"))
                        .with(csrf());

        //when
        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id" : 1,
                            "title" : "Product",
                            "details" : "Product description"
                    }""")));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title" : "New title",
                            "details" : "New product description"
                    }""")).willReturn(WireMock.noContent()));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/catalogue/products/1"));

        WireMock.verify(WireMock.patchRequestedFor(
                WireMock.urlPathMatching("/catalogue-api/products/1"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title" : "New title",
                            "details" : "New product description"
                    }""")));
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductEditPage() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1/edit")
                        .param("title", "   ")
                        .with(user("j.dewar").roles("MANAGER"))
                        .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id" : 1,
                            "title" : "Product",
                            "details" : "Product description"
                    }""")));

        WireMock.stubFor(WireMock.patch("/catalogue-api/products/1")
                .withRequestBody(WireMock.equalToJson("""
                    {
                        "title" : "   ",
                        "details" : null
                    }
                """)).willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                    {
                                        "errors" : ["Exception 1", "Exception 2"]
                                    }
                                """)));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isBadRequest(),
                        view().name("catalogue/products/edit"),
                        model().attribute("product",
                                new Product(1, "Product", "Product description")),
                        model().attribute("errors", List.of("Exception 1", "Exception 2")),
                        model().attribute("payload",
                                new UpdateProductPayload("   ", null))
                );

        WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1"))
                .withRequestBody(WireMock.equalToJson("""
                    {
                        "title" : "   ",
                        "details" : null
                    }""")));
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1/edit")
                        .param("title", "New title")
                        .param("details", "New product description")
                        .with(user("j.dewar").roles("MANAGER"))
                        .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.notFound()));

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isNotFound(),
                        view().name("error/404"),
                        model().attribute("error", "Product not found"));
    }

    @Test
    void updateProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilders =
                MockMvcRequestBuilders.get("/catalogue/products/1/edit")
                        .param("title", "New title")
                        .param("details", "New product description")
                        .with(user("j.daniels").roles("MANAGER"))
                        .with(csrf());

        //when
        this.mockMvc.perform(requestBuilders)
                //then
                .andExpectAll(
                        status().isForbidden());
    }

    @Test
    void deleteProduct_ProductExists_RedirectsToProductsListPage() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/catalogue/products/1/delete")
                .with(user("j.dewar").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                    {
                        "id" : 1,
                        "title" : "Product",
                        "details" : "Product description"
                    }
                """)));

        WireMock.stubFor(WireMock.delete("/catalogue-api/products/1")
                .willReturn(WireMock.noContent()));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/catalogue/products/list"));

        WireMock.verify(WireMock.deleteRequestedFor(
                WireMock.urlPathMatching("/catalogue-api/products/1")));
    }

    @Test
    void deleteProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/catalogue/products/1/delete")
                .with(user("j.dewar").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.notFound()));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Product not found"));
    }

    @Test
    void deleteProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/catalogue/products/1/delete")
                        .with(user("j.dewar"))
                        .with(csrf());

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isForbidden());
    }
}
