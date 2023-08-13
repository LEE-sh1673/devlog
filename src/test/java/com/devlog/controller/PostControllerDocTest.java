package com.devlog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devlog.annotation.CustomAcceptanceTest;
import com.devlog.annotation.WithMockTestUser;
import com.devlog.domain.Post;
import com.devlog.repository.post.PostRepository;
import com.devlog.request.post.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CustomAcceptanceTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(
    uriScheme = "https",
    uriHost = "api.devlog.com",
    uriPort = 443
)
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerDocTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("글 단건 조회")
    void test1() throws Exception {
        // given
        Post post = postRepository.save(Post.builder()
            .title("title 1")
            .content("content 1")
            .build());

        // when
        ResultActions result = mockMvc.perform(
            get("/posts/{postId}", post.getId())
                .accept(APPLICATION_JSON));

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andDo(document("post-inquiry",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("postId").description("게시글 ID")
                ),
                responseFields(
                    fieldWithPath("success").description(
                        "Indicates whether the request was successful."),
                    fieldWithPath("error")
                        .type(OBJECT)
                        .optional()
                        .description("Indicates whether the request failed."),
                    fieldWithPath("response").description("Response value to user's request."),
                    fieldWithPath("response.id").description("The post's id"),
                    fieldWithPath("response.title").description("The post's title"),
                    fieldWithPath("response.content").description("The post's content")))
            );
    }

    @Test
    @DisplayName("존재하지 않는 글 조회")
    void test2() throws Exception {
        // given
        long nonExistPostId = -1L;

        // when
        ResultActions result = mockMvc.perform(
            get("/posts/{postId}", nonExistPostId)
                .accept(APPLICATION_JSON));

        // then
        result.andDo(print())
            .andExpect(status().isNotFound())
            .andDo(document("post-inquiry-error",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("postId").description("게시글 ID")
                ),
                responseFields(
                    fieldWithPath("success").description(
                        "Indicates whether the request was successful."),
                    fieldWithPath("response")
                        .type(OBJECT)
                        .optional()
                        .description("Response value to user's request."),
                    fieldWithPath("error.message").description("Message for error result."),
                    fieldWithPath("error.status").description("HTTP response code")))
            );
    }

    @Test
    @WithMockTestUser
    @DisplayName("글 등록")
    void test3() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("글 제목")
            .content("글 본문")
            .build();

        // when
        ResultActions result = mockMvc.perform(
            post("/posts")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andDo(print())
            .andExpect(status().isOk())
            .andDo(document("post-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title")
                        .description("The post's title")
                        .attributes(key("constraint").value("좋은 제목 작성")),
                    fieldWithPath("content").description("The post's content").optional()
                )));
    }
}
