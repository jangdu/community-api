package com.jangdu.community.post.entity;

import com.jangdu.community.category.entity.Category;
import com.jangdu.community.global.entity.BaseTimeEntity;
import com.jangdu.community.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PostStatus status;

    @Builder
    private Post(Category category, User author, String title, String content) {
        this.category = category;
        this.author = author;
        this.title = title;
        this.content = content;
        this.viewCount = 0;
        this.status = PostStatus.PUBLISHED;
    }

    public static Post create(Category category, User author, String title, String content) {
        return Post.builder()
                .category(category)
                .author(author)
                .title(title)
                .content(content)
                .build();
    }

    public void update(String title, String content, Category category) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (category != null) this.category = category;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }

    public boolean isPublished() {
        return this.status == PostStatus.PUBLISHED;
    }
}
