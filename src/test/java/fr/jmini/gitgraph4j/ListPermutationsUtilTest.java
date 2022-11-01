package fr.jmini.gitgraph4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class ListPermutationsUtilTest {

    @Test
    void testAB() throws Exception {
        List<String> list = List.of("a", "b");

        List<List<String>> result = ListPermutationsUtil.computePermutations(list);
        // System.out.println(allAsString(result));

        assertThat(result).hasSize(2)
                .anySatisfy(l -> assertThat(l).containsExactly("a", "b"))
                .anySatisfy(l -> assertThat(l).containsExactly("b", "a"));
    }

    @Test
    void testABC() throws Exception {
        List<String> list = List.of("a", "b", "c");
        runTest(list, 6);
    }

    @Test
    void testABCD() throws Exception {
        List<String> list = List.of("a", "b", "c", "d");
        runTest(list, 24);
    }

    @Test
    void testABCDE() throws Exception {
        List<String> list = List.of("a", "b", "c", "d", "e");
        runTest(list, 120);
    }

    @Test
    void testABCDELimit1() throws Exception {
        List<String> list = List.of("a", "b", "c", "d", "e");
        runTest(list, 1, 5);
    }

    @Test
    void testABCDELimit2() throws Exception {
        List<String> list = List.of("a", "b", "c", "d", "e");
        runTest(list, 2, 20);
    }

    @Test
    void testABCDELimit3() throws Exception {
        List<String> list = List.of("a", "b", "c", "d", "e");
        runTest(list, 3, 60);
    }

    private void runTest(List<String> list, int expected) {
        runTest(list, null, expected);
    }

    private void runTest(List<String> list, Integer limit, int expected) {
        List<List<String>> result = ListPermutationsUtil.computePermutations(list, limit);
        // System.out.println(allAsString(result));

        assertThat(result).hasSize(expected);
        if (limit == null) {
            assertThat(result).allSatisfy(l -> assertThat(l).containsExactlyInAnyOrderElementsOf(list));
        }
        assertThat(result.stream()
                .distinct()
                .count()).isEqualTo(expected);
    }

    private static String allAsString(List<List<String>> all) {
        return all.stream()
                .map(ListPermutationsUtilTest::listAsString)
                .collect(Collectors.joining("\n ", "[\n ", "\n]"));
    }

    private static String listAsString(List<String> list) {
        return list.stream()
                .collect(Collectors.joining(", ", "[", "]"));
    }

}
