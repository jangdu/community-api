package com.jangdu.community.fixture;

import com.jangdu.community.category.entity.Category;
import com.jangdu.community.post.dto.CreatePostRequest;
import com.jangdu.community.post.dto.UpdatePostRequest;
import com.jangdu.community.post.entity.Post;
import com.jangdu.community.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public class PostFixture {

    public static final String TITLE = "테스트 게시글";
    public static final String CONTENT = "테스트 내용입니다.";

    public static Post createPost() {
        return createPost(1L, UserFixture.createUser(), CategoryFixture.createCategory());
    }

    public static Post createPost(Long id, User author, Category category) {
        Post post = Post.create(category, author, TITLE, CONTENT);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    public static CreatePostRequest createPostRequest() {
        return new CreatePostRequest(1L, TITLE, CONTENT);
    }

    public static UpdatePostRequest updatePostRequest() {
        return new UpdatePostRequest(null, "수정된 제목", "수정된 내용");
    }
}
