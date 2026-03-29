package com.jangdu.community.fixture;

import com.jangdu.community.category.entity.Category;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;

public class CategoryFixture {

    public static final String NAME = "자유게시판";
    public static final String SLUG = "free";

    public static Category createCategory() {
        return createCategory(1L, NAME, SLUG);
    }

    public static Category createCategory(Long id, String name, String slug) {
        try {
            Constructor<Category> constructor = Category.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Category category = constructor.newInstance();
            ReflectionTestUtils.setField(category, "id", id);
            ReflectionTestUtils.setField(category, "name", name);
            ReflectionTestUtils.setField(category, "slug", slug);
            ReflectionTestUtils.setField(category, "displayOrder", 1);
            return category;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
