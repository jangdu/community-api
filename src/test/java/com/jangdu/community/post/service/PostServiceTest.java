package com.jangdu.community.post.service;

import com.jangdu.community.category.entity.Category;
import com.jangdu.community.category.repository.CategoryRepository;
import com.jangdu.community.fixture.CategoryFixture;
import com.jangdu.community.fixture.PostFixture;
import com.jangdu.community.fixture.UserFixture;
import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import com.jangdu.community.post.dto.*;
import com.jangdu.community.post.entity.Post;
import com.jangdu.community.post.entity.PostStatus;
import com.jangdu.community.post.repository.PostRepository;
import com.jangdu.community.user.entity.Role;
import com.jangdu.community.user.entity.User;
import com.jangdu.community.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("게시글 작성")
    class Create {

        @Test
        @DisplayName("성공 시 게시글을 반환한다")
        void success() {
            User author = UserFixture.createUser();
            Category category = CategoryFixture.createCategory();
            CreatePostRequest request = PostFixture.createPostRequest();

            given(userRepository.findById(1L)).willReturn(Optional.of(author));
            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
            given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
                Post saved = invocation.getArgument(0);
                ReflectionTestUtils.setField(saved, "id", 1L);
                return saved;
            });

            PostResponse response = postService.create(1L, request);

            assertThat(response.getTitle()).isEqualTo(PostFixture.TITLE);
            assertThat(response.getAuthor().getId()).isEqualTo(1L);
            assertThat(response.getCategorySlug()).isEqualTo(CategoryFixture.SLUG);
            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("존재하지 않는 카테고리면 CATEGORY_NOT_FOUND 예외를 던진다")
        void failWhenCategoryNotFound() {
            User author = UserFixture.createUser();
            CreatePostRequest request = PostFixture.createPostRequest();

            given(userRepository.findById(1L)).willReturn(Optional.of(author));
            given(categoryRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.create(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시글 목록 조회")
    class GetList {

        @Test
        @DisplayName("전체 목록을 반환한다")
        void allPosts() {
            Post post = PostFixture.createPost();
            Page<Post> page = new PageImpl<>(List.of(post));
            Pageable pageable = PageRequest.of(0, 20);

            given(postRepository.findAllByStatus(PostStatus.PUBLISHED, pageable)).willReturn(page);

            Page<PostListResponse> result = postService.getList(null, null, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo(PostFixture.TITLE);
        }

        @Test
        @DisplayName("카테고리별 필터링된 목록을 반환한다")
        void filteredByCategory() {
            Post post = PostFixture.createPost();
            Page<Post> page = new PageImpl<>(List.of(post));
            Pageable pageable = PageRequest.of(0, 20);

            given(postRepository.findAllByCategoryIdAndStatus(1L, PostStatus.PUBLISHED, pageable)).willReturn(page);

            Page<PostListResponse> result = postService.getList(1L, null, pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("키워드로 검색된 목록을 반환한다")
        void searchByKeyword() {
            Post post = PostFixture.createPost();
            Page<Post> page = new PageImpl<>(List.of(post));
            Pageable pageable = PageRequest.of(0, 20);

            given(postRepository.searchByKeyword("테스트", PostStatus.PUBLISHED, pageable)).willReturn(page);

            Page<PostListResponse> result = postService.getList(null, "테스트", pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("게시글 상세 조회")
    class GetDetail {

        @Test
        @DisplayName("성공 시 조회수가 증가하고 게시글을 반환한다")
        void success() {
            Post post = PostFixture.createPost();
            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.of(post));

            PostResponse response = postService.getDetail(1L);

            assertThat(response.getTitle()).isEqualTo(PostFixture.TITLE);
            assertThat(response.getViewCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 게시글이면 POST_NOT_FOUND 예외를 던진다")
        void failWhenNotFound() {
            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.getDetail(1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("삭제된 게시글이면 POST_NOT_FOUND 예외를 던진다")
        void failWhenDeleted() {
            Post post = PostFixture.createPost();
            post.delete();
            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.of(post));

            assertThatThrownBy(() -> postService.getDetail(1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    class Update {

        @Test
        @DisplayName("작성자가 수정하면 성공한다")
        void success() {
            Post post = PostFixture.createPost();
            UpdatePostRequest request = PostFixture.updatePostRequest();
            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.of(post));

            PostResponse response = postService.update(1L, 1L, request);

            assertThat(response.getTitle()).isEqualTo("수정된 제목");
            assertThat(response.getContent()).isEqualTo("수정된 내용");
        }

        @Test
        @DisplayName("작성자가 아니면 POST_NOT_AUTHOR 예외를 던진다")
        void failWhenNotAuthor() {
            Post post = PostFixture.createPost();
            UpdatePostRequest request = PostFixture.updatePostRequest();
            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.of(post));

            assertThatThrownBy(() -> postService.update(999L, 1L, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.POST_NOT_AUTHOR);
        }
    }

    @Nested
    @DisplayName("게시글 삭제")
    class Delete {

        @Test
        @DisplayName("작성자가 삭제하면 성공한다")
        void successByAuthor() {
            Post post = PostFixture.createPost();
            User author = UserFixture.createUser();

            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.of(post));
            given(userRepository.findById(1L)).willReturn(Optional.of(author));

            postService.delete(1L, 1L);

            assertThat(post.isPublished()).isFalse();
        }

        @Test
        @DisplayName("ADMIN은 다른 사람의 게시글도 삭제할 수 있다")
        void successByAdmin() {
            Post post = PostFixture.createPost();
            User admin = UserFixture.createUser(999L, "admin@test.com");
            ReflectionTestUtils.setField(admin, "role", Role.ADMIN);

            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.of(post));
            given(userRepository.findById(999L)).willReturn(Optional.of(admin));

            postService.delete(999L, 1L);

            assertThat(post.isPublished()).isFalse();
        }

        @Test
        @DisplayName("작성자가 아닌 일반 유저는 POST_NOT_AUTHOR 예외를 던진다")
        void failWhenNotAuthorAndNotAdmin() {
            Post post = PostFixture.createPost();
            User otherUser = UserFixture.createUser(999L, "other@test.com");

            given(postRepository.findByIdWithAuthorAndCategory(1L)).willReturn(Optional.of(post));
            given(userRepository.findById(999L)).willReturn(Optional.of(otherUser));

            assertThatThrownBy(() -> postService.delete(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.POST_NOT_AUTHOR);
        }
    }
}
