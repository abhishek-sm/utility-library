package com.github.utils.src.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        @Test
        void testNullMap() { assertTrue( CollectionUtils.isEmpty((Map<?, ?>) null)); }

        @Test
        void testEmptyMap() {assertTrue(CollectionUtils.isEmpty(Collections.emptyMap()));}

        @Test
        void testNonEmptyMap() {assertFalse(CollectionUtils.isEmpty(Map.of("item","item1")));}
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

        @Test
        void testNullMap() { assertFalse(CollectionUtils.isNotEmpty((Map<?, ?>) null));}

        @Test
        void testEmptyMap() {assertFalse(CollectionUtils.isNotEmpty(Collections.emptyMap()) );}

        @Test
        void testNonEmptyMap() {assertTrue(CollectionUtils.isNotEmpty(Map.of("item","item1")));}
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

        @Test
        void testNullMap() {
            assertEquals(0, CollectionUtils.size((Map<?, ?>) null));
        }

        @Test
        void testEmptyMap() {
            assertEquals(0, CollectionUtils.size(Collections.emptyMap()));
        }

        @Test
        void testNonEmptyMap() {
            Map<String, String> map = Map.of("key1", "value1", "key2", "value2");
            assertEquals(2, CollectionUtils.size(map));
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

        @Test
        void testNullSet() {
            assertTrue(CollectionUtils.emptyIfNull((Set<Object>) null).isEmpty());
        }

        @Test
        void testNonNullSet() {
            Set<String> set = Set.of("a");
            assertSame(set, CollectionUtils.emptyIfNull(set));
        }

        @Test
        void testNullMap() {
            assertTrue(CollectionUtils.emptyIfNull((Map<Object, Object>) null).isEmpty());
        }

        @Test
        void testNonNullMap() {
            Map<String, String> map = Map.of("key", "value");
            assertSame(map, CollectionUtils.emptyIfNull(map));
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

        @Test
        void testNullMap() {
            assertEquals("default", CollectionUtils.getOrDefault(null, "key", "default"));
        }

        @Test
        void testEmptyMap() {
            assertEquals("default", CollectionUtils.getOrDefault(Collections.emptyMap(), "key", "default"));
        }

        @Test
        void testKeyNotInMap() {
            Map<String, String> map = Map.of("key1", "value1");
            assertEquals("default", CollectionUtils.getOrDefault(map, "key2", "default"));
        }

        @Test
        void testKeyInMap() {
            Map<String, String> map = Map.of("key1", "value1");
            assertEquals("value1", CollectionUtils.getOrDefault(map, "key1", "default"));
        }

        @Test
        void testNullValueInMap() {
            Map<String, String> map = new HashMap<>();
            map.put("key1", null);
            assertNull(CollectionUtils.getOrDefault(map, "key1", "default"));
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

