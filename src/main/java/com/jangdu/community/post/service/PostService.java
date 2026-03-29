package com.jangdu.community.post.service;

import com.jangdu.community.category.entity.Category;
import com.jangdu.community.category.repository.CategoryRepository;
import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import com.jangdu.community.post.dto.*;
import com.jangdu.community.post.entity.Post;
import com.jangdu.community.post.entity.PostStatus;
import com.jangdu.community.post.repository.PostRepository;
import com.jangdu.community.user.entity.Role;
import com.jangdu.community.user.entity.User;
import com.jangdu.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public PostResponse create(Long userId, CreatePostRequest request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Post post = Post.create(category, author, request.getTitle(), request.getContent());
        postRepository.save(post);

        return PostResponse.from(post);
    }

    public Page<PostListResponse> getList(Long categoryId, String keyword, Pageable pageable) {
        Page<Post> posts;

        if (keyword != null && !keyword.isBlank()) {
            posts = categoryId != null
                    ? postRepository.searchByKeywordAndCategory(keyword, categoryId, PostStatus.PUBLISHED, pageable)
                    : postRepository.searchByKeyword(keyword, PostStatus.PUBLISHED, pageable);
        } else {
            posts = categoryId != null
                    ? postRepository.findAllByCategoryIdAndStatus(categoryId, PostStatus.PUBLISHED, pageable)
                    : postRepository.findAllByStatus(PostStatus.PUBLISHED, pageable);
        }

        return posts.map(PostListResponse::from);
    }

    @Transactional
    public PostResponse getDetail(Long postId) {
        Post post = findPublishedPost(postId);
        post.increaseViewCount();
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse update(Long userId, Long postId, UpdatePostRequest request) {
        Post post = findPublishedPost(postId);
        validateAuthor(post, userId);

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        post.update(request.getTitle(), request.getContent(), category);
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = findPublishedPost(postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            validateAuthor(post, userId);
        }

        post.delete();
    }

    private Post findPublishedPost(Long postId) {
        Post post = postRepository.findByIdWithAuthorAndCategory(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isPublished()) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        return post;
    }

    private void validateAuthor(Post post, Long userId) {
        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.POST_NOT_AUTHOR);
        }
    }
}
