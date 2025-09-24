package com.example.bankcards.repository.specifications;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;

public class FilterSpecification {
    public static <T> Specification<T> equal(String field, Object value) {
        return (root, query, cb) ->
                value == null ? null : cb.equal(resolvePath(field, root), value);
    }

    public static <T> Specification<T> like(String field, String value) {
        return (root, query, cb) ->
                (value == null || value.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static <T, V extends Comparable<? super V>> Specification<T> lessThanOrEqual(String field, V value, Class<V> valueType) {
        return (root, query, cb) ->
                value == null ? null : cb.lessThanOrEqualTo(root.get(field).as(valueType), value);
    }

    public static <T, V extends Comparable<? super V>> Specification<T> greaterThanOrEqual(String field, V value, Class<V> valueType) {
        return (root, query, cb) ->
                value == null ? null : cb.greaterThanOrEqualTo(root.get(field).as(valueType), value);
    }

    public static <T> Specification<T> in(String field, Iterable<?> values) {
        return (root, query, cb) ->
                values == null ? null : root.get(field).in(values);
    }

    private static <T> Path<?> resolvePath(String field, Root<T> root) {
        String[] parts = field.split("\\.");
        Path<?> path = root;

        for (String part : parts) {
            if (path instanceof Root) {
                Class<?> clazz = ((Root<?>) path).getModel().getBindableJavaType();
                if (isAssociation(clazz, part)) {
                    path = ((Root<?>) path).join(part);
                } else {
                    path = ((Root<?>) path).get(part);
                }
            } else if (path instanceof Join) {
                Class<?> clazz = ((Join<?, ?>) path).getModel().getBindableJavaType();
                if (isAssociation(clazz, part)) {
                    path = ((Join<?, ?>) path).join(part);
                } else {
                    path = path.get(part);
                }
            } else {
                path = path.get(part);
            }
        }

        return path;
    }

    private static boolean isAssociation(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.isAnnotationPresent(OneToOne.class)
                    || field.isAnnotationPresent(OneToMany.class)
                    || field.isAnnotationPresent(ManyToOne.class)
                    || field.isAnnotationPresent(ManyToMany.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
