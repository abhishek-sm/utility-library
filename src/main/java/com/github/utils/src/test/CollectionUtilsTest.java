package com.github.utils.src.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.utils.src.main.CollectionUtils;

public class CollectionUtilsTest {

    @Nested
    class IsEmptyTests {
        @Test
        void testNullCollection() {
            assertTrue(CollectionUtils.isEmpty((Collection<?>) null));
        }

        @Test
        void testEmptyCollection() {
            assertTrue(CollectionUtils.isEmpty(Collections.emptyList()));
        }

        @Test
        void testNonEmptyCollection() {
            assertFalse(CollectionUtils.isEmpty(List.of("item")));
        }
    }

    @Nested
    class IsNotEmptyTests {
        @Test
        void testNullCollection() {
            assertFalse(CollectionUtils.isNotEmpty((Collection<?>) null));
        }

        @Test
        void testEmptyCollection() {
            assertFalse(CollectionUtils.isNotEmpty(Collections.emptyList()));
        }

        @Test
        void testNonEmptyCollection() {
            assertTrue(CollectionUtils.isNotEmpty(List.of("item")));
        }
    }

    @Nested
    class SizeTests {
        @Test
        void testNullCollection() {
            assertEquals(0, CollectionUtils.size((Collection<?>) null));
        }

        @Test
        void testEmptyCollection() {
            assertEquals(0, CollectionUtils.size(Collections.emptyList()));
        }

        @Test
        void testNonEmptyCollection() {
            assertEquals(3, CollectionUtils.size(List.of("a", "b", "c")));
        }
    }

    @Nested
    class EmptyIfNullTests {
        @Test
        void testNullList() {
            assertTrue(CollectionUtils.emptyIfNull((List<Object>) null).isEmpty());
        }

        @Test
        void testNonNullList() {
            List<String> list = List.of("a");
            assertSame(list, CollectionUtils.emptyIfNull(list));
        }
    }

    @Nested
    class GetOrDefaultTests {
        @Test
        void testNullList() {
            assertEquals("default", CollectionUtils.getOrDefault(null, 0, "default"));
        }

        @Test
        void testValidIndex() {
            List<String> list = List.of("a", "b", "c");
            assertEquals("b", CollectionUtils.getOrDefault(list, 1, "default"));
        }

        @Test
        void testInvalidIndex() {
            List<String> list = List.of("a", "b", "c");
            assertEquals("default", CollectionUtils.getOrDefault(list, 5, "default"));
        }
    }

    @Nested
    class GetFirstOrDefaultTests {
        @Test
        void testNullCollection() {
            assertEquals("default", CollectionUtils.getFirstOrDefault(null, "default"));
        }

        @Test
        void testEmptyCollection() {
            assertEquals("default", CollectionUtils.getFirstOrDefault(Collections.emptyList(), "default"));
        }

        @Test
        void testNonEmptyCollection() {
            assertEquals("first", CollectionUtils.getFirstOrDefault(List.of("first", "second"), "default"));
        }
    }

    @Nested
    class GetLastOrDefaultTests {
        @Test
        void testNullList() {
            assertEquals("default", CollectionUtils.getLastOrDefault(null, "default"));
        }

        @Test
        void testEmptyList() {
            assertEquals("default", CollectionUtils.getLastOrDefault(Collections.emptyList(), "default"));
        }

        @Test
        void testNonEmptyList() {
            assertEquals("last", CollectionUtils.getLastOrDefault(List.of("first", "middle", "last"), "default"));
        }
    }

    @Nested
    class FilterTests {
        @Test
        void testNullCollection() {
            assertTrue(CollectionUtils.filter(null, s -> true).isEmpty());
        }

        @Test
        void testPredicateFiltering() {
            List<Integer> numbers = List.of(1, 2, 3, 4);
            List<Integer> evenNumbers = CollectionUtils.filter(numbers, n -> n % 2 == 0);
            assertEquals(List.of(2, 4), evenNumbers);
        }
    }

    @Nested
    class MapTests {
        @Test
        void testNullCollection() {
            assertTrue(CollectionUtils.map(null, Object::toString).isEmpty());
        }

        @Test
        void testMappingFunction() {
            List<Integer> numbers = List.of(1, 2, 3);
            List<String> stringNumbers = CollectionUtils.map(numbers, String::valueOf);
            assertEquals(List.of("1", "2", "3"), stringNumbers);
        }
    }
}

