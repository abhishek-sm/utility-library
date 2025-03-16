package com.github.utils.src.main;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

/**
 * Utility class providing helper methods for Java collections.
 * Includes methods for safe access, transformation, filtering, 
 * and specialized collection operations.
 */
@UtilityClass
public class CollectionUtils {

    /**
     * Checks if a collection is null or empty
     *
     * @param collection the collection to check
     * @return true if the collection is null or empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if a map is null or empty
     *
     * @param map the map to check
     * @return true if the map is null or empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Checks if a collection is not null and not empty
     *
     * @param collection the collection to check
     * @return true if the collection is not null and not empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Checks if a map is not null and not empty
     *
     * @param map the map to check
     * @return true if the map is not null and not empty
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * Returns the size of a collection, or 0 if the collection is null
     *
     * @param collection the collection to get the size of
     * @return the size of the collection, or 0 if the collection is null
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * Returns the size of a map, or 0 if the map is null
     *
     * @param map the map to get the size of
     * @return the size of the map, or 0 if the map is null
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    /**
     * Returns an empty list if the input list is null
     *
     * @param <T> the type of elements in the list
     * @param list the list to check
     * @return the original list if not null, otherwise an empty list
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * Returns an empty set if the input set is null
     *
     * @param <T> the type of elements in the set
     * @param set the set to check
     * @return the original set if not null, otherwise an empty set
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * Returns an empty map if the input map is null
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param map the map to check
     * @return the original map if not null, otherwise an empty map
     */
    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * Safe way to get an element from a list by index
     *
     * @param <T> the type of elements in the list
     * @param list the list to get the element from
     * @param index the index of the element to get
     * @param defaultValue the default value to return if the element cannot be retrieved
     * @return the element at the specified index, or the default value if the list is null,
     *         empty, or the index is out of bounds
     */
    public static <T> T getOrDefault(List<T> list, int index, T defaultValue) {
        if (list == null || index < 0 || index >= list.size()) {
            return defaultValue;
        }
        return list.get(index);
    }

    /**
     * Safe way to get a value from a map by key
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param map the map to get the value from
     * @param key the key of the value to get
     * @param defaultValue the default value to return if the value cannot be retrieved
     * @return the value for the specified key, or the default value if the map is null
     *         or does not contain the key
     */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * Safe way to get the first element from a collection
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to get the first element from
     * @param defaultValue the default value to return if the first element cannot be retrieved
     * @return the first element of the collection, or the default value if the collection is null or empty
     */
    public static <T> T getFirstOrDefault(Collection<T> collection, T defaultValue) {
        if (isEmpty(collection)) {
            return defaultValue;
        }
        return collection.iterator().next();
    }

    /**
     * Safe way to get the last element from a list
     *
     * @param <T> the type of elements in the list
     * @param list the list to get the last element from
     * @param defaultValue the default value to return if the last element cannot be retrieved
     * @return the last element of the list, or the default value if the list is null or empty
     */
    public static <T> T getLastOrDefault(List<T> list, T defaultValue) {
        if (isEmpty(list)) {
            return defaultValue;
        }
        return list.getLast();
    }

    /**
     * Creates a new filtered list containing only the elements that satisfy the given predicate
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to filter
     * @param predicate the predicate to apply to each element
     * @return a new list containing only the elements that satisfy the predicate
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<? super T> predicate) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        return collection.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new list by applying the given function to each element of the input collection
     *
     * @param <T> the type of elements in the input collection
     * @param <R> the type of elements in the output list
     * @param collection the collection to transform
     * @param mapper the function to apply to each element
     * @return a new list containing the transformed elements
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * Flattens a collection of collections into a single list
     *
     * @param <T> the type of elements in the nested collections
     * @param collections the collection of collections to flatten
     * @return a new list containing all elements from all nested collections
     */
    public static <T> List<T> flatten(Collection<? extends Collection<T>> collections) {
        if (isEmpty(collections)) {
            return Collections.emptyList();
        }
        return collections.stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Splits a collection into chunks of the specified size
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to split
     * @param chunkSize the maximum size of each chunk
     * @return a list of lists, each containing at most chunkSize elements from the original collection
     * @throws IllegalArgumentException if chunkSize is less than or equal to 0
     */
    public static <T> List<List<T>> partition(Collection<T> collection, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than 0");
        }
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }

        List<List<T>> result = new ArrayList<>();
        List<T> currentChunk = new ArrayList<>(chunkSize);

        for (T element : collection) {
            currentChunk.add(element);
            if (currentChunk.size() == chunkSize) {
                result.add(new ArrayList<>(currentChunk));
                currentChunk.clear();
            }
        }

        if (!currentChunk.isEmpty()) {
            result.add(currentChunk);
        }

        return result;
    }

    /**
     * Returns a new map with entries from the two input maps combined
     * In case of duplicate keys, the values from map2 will overwrite the values from map1
     *
     * @param <K> the type of keys in the maps
     * @param <V> the type of values in the maps
     * @param map1 the first map
     * @param map2 the second map
     * @return a new map containing all entries from both input maps
     */
    public static <K, V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2) {
        Map<K, V> result = new HashMap<>();

        if (map1 != null) {
            result.putAll(map1);
        }

        if (map2 != null) {
            result.putAll(map2);
        }

        return result;
    }

    /**
     * Returns a new map with entries from the two input maps combined using the provided merge function
     * In case of duplicate keys, the merge function is used to compute the merged value
     *
     * @param <K> the type of keys in the maps
     * @param <V> the type of values in the maps
     * @param map1 the first map
     * @param map2 the second map
     * @param mergeFunction the function to resolve conflicts between values associated with the same key
     * @return a new map containing all entries from both input maps, with conflicts resolved using the merge function
     */
    public static <K, V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2,
                                         BiFunction<? super V, ? super V, ? extends V> mergeFunction) {
        Map<K, V> result = new HashMap<>();

        if (map1 != null) {
            result.putAll(map1);
        }

        if (map2 != null) {
            map2.forEach((key, value) ->
                    result.merge(key, value, mergeFunction));
        }

        return result;
    }

    /**
     * Creates a new map by transforming a collection using the provided key and value extractors
     *
     * @param <T> the type of elements in the collection
     * @param <K> the type of keys in the resulting map
     * @param <V> the type of values in the resulting map
     * @param collection the collection to transform
     * @param keyExtractor the function to extract keys from elements
     * @param valueExtractor the function to extract values from elements
     * @return a new map with keys and values extracted from the collection elements
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection,
                                            Function<? super T, ? extends K> keyExtractor,
                                            Function<? super T, ? extends V> valueExtractor) {
        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }

        return collection.stream()
                .collect(Collectors.toMap(
                        keyExtractor,
                        valueExtractor,
                        (v1, v2) -> v2 // In case of duplicate keys, keep the latest value
                ));
    }

    /**
     * Creates a new multimap (map from keys to collections of values) from a collection
     *
     * @param <T> the type of elements in the collection
     * @param <K> the type of keys in the resulting map
     * @param <V> the type of values in the resulting collections
     * @param collection the collection to transform
     * @param keyExtractor the function to extract keys from elements
     * @param valueExtractor the function to extract values from elements
     * @return a map from keys to lists of values
     */
    public static <T, K, V> Map<K, List<V>> toMultimap(Collection<T> collection,
                                                       Function<? super T, ? extends K> keyExtractor,
                                                       Function<? super T, ? extends V> valueExtractor) {
        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }

        return collection.stream()
                .collect(Collectors.groupingBy(
                        keyExtractor,
                        Collectors.mapping(valueExtractor, Collectors.toList())
                ));
    }

    /**
     * Groups elements of a collection by a key extracted from each element
     *
     * @param <T> the type of elements in the collection
     * @param <K> the type of keys used for grouping
     * @param collection the collection to group
     * @param keyExtractor the function to extract keys from elements
     * @return a map from keys to lists of elements with that key
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection,
                                                 Function<? super T, ? extends K> keyExtractor) {
        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }

        return collection.stream()
                .collect(Collectors.groupingBy(keyExtractor));
    }

    /**
     * Returns a new list containing the distinct elements of the input collection,
     * based on equality of elements
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to get distinct elements from
     * @return a new list containing the distinct elements of the input collection
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }

        return collection.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Returns a new list containing the distinct elements of the input collection,
     * based on a key extracted from each element
     *
     * @param <T> the type of elements in the collection
     * @param <K> the type of keys used for determining distinctness
     * @param collection the collection to get distinct elements from
     * @param keyExtractor the function to extract keys from elements
     * @return a new list containing elements with distinct keys
     */
    public static <T, K> List<T> distinctBy(Collection<T> collection,
                                            Function<? super T, ? extends K> keyExtractor) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }

        Map<K, Boolean> seen = new HashMap<>();
        return collection.stream()
                .filter(element -> seen.putIfAbsent(keyExtractor.apply(element), Boolean.TRUE) == null)
                .collect(Collectors.toList());
    }

    /**
     * Joins the elements of a collection into a single string with the specified delimiter
     *
     * @param collection the collection of elements to join
     * @param delimiter the delimiter to use between elements
     * @return a string containing all elements joined with the delimiter
     */
    public static String join(Collection<?> collection, String delimiter) {
        if (isEmpty(collection)) {
            return "";
        }

        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Returns the intersection of two collections
     *
     * @param <T> the type of elements in the collections
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return a new set containing elements that are present in both input collections
     */
    public static <T> Set<T> intersection(Collection<T> collection1, Collection<T> collection2) {
        if (isEmpty(collection1) || isEmpty(collection2)) {
            return Collections.emptySet();
        }

        Set<T> result = new HashSet<>(collection1);
        result.retainAll(collection2);
        return result;
    }

    /**
     * Returns the union of two collections
     *
     * @param <T> the type of elements in the collections
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return a new set containing elements that are present in either input collection
     */
    public static <T> Set<T> union(Collection<T> collection1, Collection<T> collection2) {
        Set<T> result = new HashSet<>();

        if (isNotEmpty(collection1)) {
            result.addAll(collection1);
        }

        if (isNotEmpty(collection2)) {
            result.addAll(collection2);
        }

        return result;
    }

    /**
     * Returns the difference of two collections (elements in the first collection that are not in the second)
     *
     * @param <T> the type of elements in the collections
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return a new set containing elements that are present in the first collection but not in the second
     */
    public static <T> Set<T> difference(Collection<T> collection1, Collection<T> collection2) {
        if (isEmpty(collection1)) {
            return Collections.emptySet();
        }

        Set<T> result = new HashSet<>(collection1);

        if (isNotEmpty(collection2)) {
            result.removeAll(collection2);
        }

        return result;
    }

    /**
     * Returns a new list containing the elements of the input collection in reversed order
     *
     * @param <T> the type of elements in the list
     * @param list the list to reverse
     * @return a new list containing the elements in reversed order
     */
    public static <T> List<T> reverse(List<T> list) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }

    /**
     * Returns a new thread-safe map
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @return a new thread-safe map
     */
    public static <K, V> Map<K, V> concurrentMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a synchronized wrapper around the specified collection
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to synchronize
     * @return a synchronized view of the specified collection
     */
    public static <T> Collection<T> synchronizedCollection(Collection<T> collection) {
        return Collections.synchronizedCollection(collection);
    }

    /**
     * Creates a synchronized wrapper around the specified list
     *
     * @param <T> the type of elements in the list
     * @param list the list to synchronize
     * @return a synchronized view of the specified list
     */
    public static <T> List<T> synchronizedList(List<T> list) {
        return Collections.synchronizedList(list);
    }

    /**
     * Creates a synchronized wrapper around the specified set
     *
     * @param <T> the type of elements in the set
     * @param set the set to synchronize
     * @return a synchronized view of the specified set
     */
    public static <T> Set<T> synchronizedSet(Set<T> set) {
        return Collections.synchronizedSet(set);
    }

    /**
     * Creates a synchronized wrapper around the specified map
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param map the map to synchronize
     * @return a synchronized view of the specified map
     */
    public static <K, V> Map<K, V> synchronizedMap(Map<K, V> map) {
        return Collections.synchronizedMap(map);
    }

    /**
     * Checks if all elements in the collection satisfy the given predicate
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to check
     * @param predicate the predicate to apply to each element
     * @return true if all elements satisfy the predicate, or if the collection is empty
     */
    public static <T> boolean allMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (isEmpty(collection)) {
            return true;
        }

        return collection.stream().allMatch(predicate);
    }

    /**
     * Checks if any element in the collection satisfies the given predicate
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to check
     * @param predicate the predicate to apply to each element
     * @return true if any element satisfies the predicate
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (isEmpty(collection)) {
            return false;
        }

        return collection.stream().anyMatch(predicate);
    }

    /**
     * Checks if no element in the collection satisfies the given predicate
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to check
     * @param predicate the predicate to apply to each element
     * @return true if no element satisfies the predicate, or if the collection is empty
     */
    public static <T> boolean noneMatch(Collection<T> collection, Predicate<? super T> predicate) {
        if (isEmpty(collection)) {
            return true;
        }

        return collection.stream().noneMatch(predicate);
    }

    /**
     * Returns a new list containing the first n elements of the input collection
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to get elements from
     * @param n the maximum number of elements to take
     * @return a new list containing at most n elements from the beginning of the input collection
     * @throws IllegalArgumentException if n is negative
     */
    public static <T> List<T> take(Collection<T> collection, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of elements to take must not be negative");
        }

        if (isEmpty(collection) || n == 0) {
            return Collections.emptyList();
        }

        return collection.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Returns a new list containing all but the first n elements of the input collection
     *
     * @param <T> the type of elements in the collection
     * @param collection the collection to get elements from
     * @param n the number of elements to skip
     * @return a new list containing elements from the input collection after skipping n elements
     * @throws IllegalArgumentException if n is negative
     */
    public static <T> List<T> skip(Collection<T> collection, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of elements to skip must not be negative");
        }

        if (isEmpty(collection) || size(collection) <= n) {
            return Collections.emptyList();
        }

        return collection.stream()
                .skip(n)
                .collect(Collectors.toList());
    }

    /**
     * Zips two collections into a single list of pairs
     *
     * @param <T> the type of elements in the first collection
     * @param <U> the type of elements in the second collection
     * @param collection1 the first collection
     * @param collection2 the second collection
     * @return a new list of pairs where the first element of each pair comes from the first collection
     *         and the second element comes from the second collection
     */
    public static <T, U> List<Map.Entry<T, U>> zip(Collection<T> collection1, Collection<U> collection2) {
        if (isEmpty(collection1) || isEmpty(collection2)) {
            return Collections.emptyList();
        }

        List<Map.Entry<T, U>> result = new ArrayList<>();
        Iterator<T> iter1 = collection1.iterator();
        Iterator<U> iter2 = collection2.iterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            result.add(Map.entry(iter1.next(), iter2.next()));
        }

        return result;
    }
}