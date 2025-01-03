package org.example.controller.integrationalTest;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Locale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class ProductRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    void findProduct_ProductExists_ReturnsProductsList() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id": 1,
                                    "title": "Product №1",
                                    "details": "Description of Product №1"
                                }""")
                )
                .andDo(document("catalogue/products/find_all",
                        preprocessResponse(prettyPrint(), modifyHeaders().remove("Vary")),
                        responseFields(
                                fieldWithPath("id").description("Product identifier").type("int"),
                                fieldWithPath("title").description("Product title").type("string"),
                                fieldWithPath("details").description("Product description").type("string")
                        )));
    }

    @Test
    void findProduct_ProductDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void findProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void updateProduct_RequestIsValid_ReturnsNoContent() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "New product title",
                            "details": "New product description"
                        }""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void updateProduct_RequestIsInvalid_ReturnsBadRequest() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .locale(Locale.ENGLISH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "   ",
                            "details": null
                        }""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    "errors": ["Product title must be specified"]
                                }""")
                );
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .locale(Locale.ENGLISH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "New product title",
                            "details": "New product description"
                        }""")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void updateProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .locale(Locale.ENGLISH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "New product title",
                            "details": "New product description"
                        }""")
                .with(jwt());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void deleteProduct_ProductExists_ReturnsNoContent() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    void deleteProduct_ProductDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void deleteProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/products/1")
                .with(jwt());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isForbidden()
                );
    }
}
