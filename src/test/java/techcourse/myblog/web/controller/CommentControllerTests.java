package techcourse.myblog.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import techcourse.myblog.domain.Comment;
import techcourse.myblog.domain.repository.CommentRepository;
import techcourse.myblog.dto.ArticleDto;
import techcourse.myblog.dto.CommentDto;
import techcourse.myblog.dto.UserDto;
import techcourse.myblog.web.controller.common.ControllerTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

class CommentControllerTests extends ControllerTestTemplate {
    @Autowired
    private CommentRepository commentRepository;

    private UserDto authorDto = savedUserDto;

    private CommentDto commentDto = new CommentDto("comment");
    private CommentDto updateCommentDto = new CommentDto("new comment");
    private String savedArticleUrl;

    @BeforeEach
    public void setup() {
        super.setup();
        ArticleDto articleDto = new ArticleDto("title", "coverUrl", "contents");
        savedArticleUrl = getRedirectUrl(
                loginAndRequest(authorDto, POST, "/articles/write", parseArticle(articleDto)));
    }

    @Test
    public void 로그아웃_상태_댓글작성_리다이렉트() {
        String logoutWriteCommentRedirectUrl = getRedirectUrl(httpRequest(POST, savedArticleUrl + "/comments", parser(commentDto)));
        assertThat(logoutWriteCommentRedirectUrl.equals("/login"));
    }

    @Test
    public void 로그인_상태_댓글작성_성공() {
        String loginWriteCommentRedirectUrl = getRedirectUrl(loginAndRequest(authorDto, POST, savedArticleUrl + "/comments", parser(commentDto)));
        String responseBody = getResponseBody(loginAndRequest(authorDto, GET, loginWriteCommentRedirectUrl));

        assertThat(responseBody.contains(commentDto.getContents())).isTrue();
    }

    @Test
    void 로그아웃_상태_댓글삭제_리다이렉트() {
        String logoutDeleteCommentRedirectUrl = getRedirectUrl(httpRequest(DELETE, getCommentUrl()));

        assertThat(logoutDeleteCommentRedirectUrl.equals("/login")).isTrue();
    }

    @Test
    void 작성자_로그인_상태_댓글삭제_성공() {
        String authorDeleteCommentRedirectUrl = getRedirectUrl(loginAndRequest(authorDto, DELETE, getCommentUrl()));

        assertThat(authorDeleteCommentRedirectUrl.equals(savedArticleUrl)).isTrue();
    }

    @Test
    void 작성자가_아닌_사용자_로그인_상태_댓글삭제_실패() {
        String otherUserDeleteCommentRedirectUrl = getRedirectUrl(loginAndRequest(otherUserDto, DELETE, getCommentUrl()));

        assertThat(otherUserDeleteCommentRedirectUrl.equals("/")).isTrue();
    }

    @Test
    void 로그아웃_상태_댓글수정_리다이렉트() {
        String logoutUpdateCommentRedirectUrl = getRedirectUrl(httpRequest(PUT, getCommentUrl(), parser(updateCommentDto)));

        assertThat(logoutUpdateCommentRedirectUrl.equals("/login")).isTrue();
    }

    @Test
    void 작성자_로그인_상태_댓글수정_성공() {
        String authorUpdateCommentRedirectUrl = getRedirectUrl(loginAndRequest(authorDto, PUT, getCommentUrl(), parser(updateCommentDto)));
        String responseBody = getResponseBody(loginAndRequest(authorDto, GET, authorUpdateCommentRedirectUrl));

        assertThat(responseBody.contains(commentDto.getContents())).isTrue();
    }

    @Test
    void 작성자가_아닌_사용자_로그인_상태_댓글수정_실패() {
        String otherUserUpdateCommentRedirectUrl = getRedirectUrl(loginAndRequest(otherUserDto, PUT, getCommentUrl(), parser(updateCommentDto)));

        assertThat(otherUserUpdateCommentRedirectUrl.equals("/")).isTrue();
    }

    private String getCommentUrl() {
        loginAndRequest(authorDto, POST, savedArticleUrl + "/comments", parser(commentDto));
        Comment comment = commentRepository.findAll().get(0);

        return String.format("/articles/%d/comments/%d", comment.getArticle().getId(), comment.getId());
    }

    private MultiValueMap<String, String> parser(CommentDto commentDto) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("contents", commentDto.getContents());
        return multiValueMap;
    }
}