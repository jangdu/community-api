package com.jangdu.community.post.repository;

import com.jangdu.community.post.entity.Post;
import com.jangdu.community.post.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.category WHERE p.id = :id")
    Optional<Post> findByIdWithAuthorAndCategory(@Param("id") Long id);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.category WHERE p.status = :status",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.status = :status")
    Page<Post> findAllByStatus(@Param("status") PostStatus status, Pageable pageable);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.category " +
            "WHERE p.category.id = :categoryId AND p.status = :status",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId AND p.status = :status")
    Page<Post> findAllByCategoryIdAndStatus(@Param("categoryId") Long categoryId,
                                            @Param("status") PostStatus status,
                                            Pageable pageable);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.category " +
            "WHERE p.status = :status AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.status = :status AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchByKeyword(@Param("keyword") String keyword,
                               @Param("status") PostStatus status,
                               Pageable pageable);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.category " +
            "WHERE p.category.id = :categoryId AND p.status = :status " +
            "AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId AND p.status = :status " +
                    "AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchByKeywordAndCategory(@Param("keyword") String keyword,
                                          @Param("categoryId") Long categoryId,
                                          @Param("status") PostStatus status,
                                          Pageable pageable);
}
