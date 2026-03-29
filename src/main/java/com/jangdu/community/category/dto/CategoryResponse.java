package com.jangdu.community.category.dto;

import com.jangdu.community.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private final Long id;
    private final String name;
    private final String slug;
    private final int displayOrder;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
}
