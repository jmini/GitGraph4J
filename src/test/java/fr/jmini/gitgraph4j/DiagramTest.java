package fr.jmini.gitgraph4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DiagramTest {

    @Test
    void diag01() throws Exception {
        Diagram d = DiagTestUtil.diag01();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");
        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");

        assertThat(commitB.commonParentsWithPath(commitF))
                .hasSize(1)
                .containsEntry(commitA, List.of(List.of(commitA, commitF)));
        assertThat(commitF.commonParentsWithPath(commitB))
                .hasSize(1)
                .containsEntry(commitA, List.of(List.of(commitA, commitB)));

        assertThat(commitA.commonParentsWithPath(commitF))
                .hasSize(1)
                .containsEntry(commitA, List.of(List.of(commitA, commitF)));
        assertThat(commitF.commonParentsWithPath(commitA))
                .hasSize(1)
                .containsEntry(commitA, List.of());

        assertThat(commitA.commonParentsWithPath(commitF))
                .hasSize(1)
                .containsEntry(commitA, List.of(List.of(commitA, commitF)));
        assertThat(commitF.commonParentsWithPath(commitA))
                .hasSize(1)
                .containsEntry(commitA, List.of());

        assertThat(commitA.commonParentsWithPath(commitC))
                .hasSize(1)
                .containsKey(commitA)
                .hasValueSatisfying(new Condition<List<List<Commit>>>(l -> l.size() == 2, "list has size 2"));

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(4)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitF);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(4)
                .containsAll(allCommits)
                .containsSubsequence(commitA, commitB, commitC)
                .containsSubsequence(commitA, commitF, commitC);

    }

    @Test
    void diag02() throws Exception {
        Diagram d = DiagTestUtil.diag02();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");
        Commit commitD = DiagTestUtil.findCommitByInternalName(d, "d");
        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");

        assertThat(commitA.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitB)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitC)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitD)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitF)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitG)).containsExactlyInAnyOrder(commitA);

        assertThat(commitB.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitB.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitC)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitD)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitF)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitG)).containsExactlyInAnyOrder(commitB);

        assertThat(commitC.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitC.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitC.commonParents(commitC)).containsExactlyInAnyOrder(commitC);
        assertThat(commitC.commonParents(commitD)).containsExactlyInAnyOrder(commitC);
        assertThat(commitC.commonParents(commitF)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitC.commonParents(commitG)).containsExactlyInAnyOrder(commitA, commitB);

        assertThat(commitD.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitD.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitD.commonParents(commitC)).containsExactlyInAnyOrder(commitC);
        assertThat(commitD.commonParents(commitD)).containsExactlyInAnyOrder(commitD);
        assertThat(commitD.commonParents(commitF)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitD.commonParents(commitG)).containsExactlyInAnyOrder(commitA, commitB);

        assertThat(commitF.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitF.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitF.commonParents(commitC)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitF.commonParents(commitD)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitF.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitG)).containsExactlyInAnyOrder(commitF);

        assertThat(commitG.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitG.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitG.commonParents(commitC)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitG.commonParents(commitD)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitG.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitG.commonParents(commitG)).containsExactlyInAnyOrder(commitG);

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(commitA.isParentOf(commitB)).isTrue();
        assertThat(commitA.isParentOf(commitC)).isTrue();
        assertThat(commitA.isParentOf(commitD)).isTrue();
        assertThat(commitA.isParentOf(commitF)).isTrue();
        assertThat(commitA.isParentOf(commitG)).isTrue();

        assertThat(commitA.pathToChild(commitD)).hasSize(1)
                .allSatisfy(l -> assertThat(l).containsExactly(commitA, commitB, commitC, commitD));
        assertThat(commitA.pathToChild(commitC)).hasSize(1)
                .allSatisfy(l -> assertThat(l).containsExactly(commitA, commitB, commitC));
        assertThat(commitA.pathToChild(commitG)).hasSize(1)
                .allSatisfy(l -> assertThat(l).containsExactly(commitA, commitB, commitF, commitG));
        assertThat(commitA.pathToChild(commitF)).hasSize(1)
                .allSatisfy(l -> assertThat(l).containsExactly(commitA, commitB, commitF));
        assertThat(commitA.pathToChild(commitB)).hasSize(1)
                .allSatisfy(l -> assertThat(l).containsExactly(commitA, commitB));

        assertThat(commitG.isChildOf(commitA)).isTrue();
        assertThat(commitD.isChildOf(commitA)).isTrue();
        assertThat(commitG.isChildOf(commitB)).isTrue();
        assertThat(commitD.isChildOf(commitB)).isTrue();

        assertThat(commitD.isChildOf(commitG)).isFalse();
        assertThat(commitG.isChildOf(commitD)).isFalse();
        assertThat(commitD.isParentOf(commitG)).isFalse();
        assertThat(commitG.isParentOf(commitD)).isFalse();

        Comparator<Commit> comparator = new CommitComparator(d);
        assertThat(comparator.compare(commitF, commitC)).isNegative();
        assertThat(comparator.compare(commitC, commitF)).isPositive();

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(6)
                .containsSubsequence(commitA, commitB, commitC, commitD)
                .containsSubsequence(commitA, commitB, commitF, commitG)
                .containsSubsequence(commitF, commitC);
    }

    @Test
    void diag03() throws Exception {
        Diagram d = DiagTestUtil.diag03();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitM = DiagTestUtil.findCommitByInternalName(d, "m");

        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");

        Commit commitX = DiagTestUtil.findCommitByInternalName(d, "x");

        assertThat(commitM.commonParentsWithPath(commitX))
                .hasSize(1)
                .containsKey(commitA)
                .containsValue(List.of(List.of(commitA, commitX)));
        assertThat(commitX.commonParentsWithPath(commitM))
                .hasSize(1)
                .containsKey(commitA)
                .allSatisfy((k, v) -> {
                    assertThat(v).hasSize(2)
                            .containsExactlyInAnyOrder(List.of(commitA, commitB, commitM), List.of(commitA, commitF, commitG, commitM));
                });

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(6)
                .contains(
                        commitA,
                        commitB,
                        commitF,
                        commitG,
                        commitM,
                        commitX);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(6)
                .containsSubsequence(commitA, commitB, commitM)
                .containsSubsequence(commitA, commitF, commitG, commitM)
                .containsSubsequence(commitA, commitX);
    }

    @Test
    void diag04() throws Exception {
        Diagram d = DiagTestUtil.diag04();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");

        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");
        Commit commitH = DiagTestUtil.findCommitByInternalName(d, "h");

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(6)
                .containsExactly(
                        commitA,
                        commitF,
                        commitB,
                        commitG,
                        commitC,
                        commitH);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(6)
                .containsExactly(
                        commitA,
                        commitF,
                        commitB,
                        commitG,
                        commitC,
                        commitH);
    }

    @Test
    void diag06() throws Exception {
        Diagram d = DiagTestUtil.diag06();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");
        Commit commitD = DiagTestUtil.findCommitByInternalName(d, "d");

        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");
        Commit commitH = DiagTestUtil.findCommitByInternalName(d, "h");

        Commit commitU = DiagTestUtil.findCommitByInternalName(d, "u");
        Commit commitV = DiagTestUtil.findCommitByInternalName(d, "v");

        assertThat(commitC.commonParentsWithPath(commitV))
                .hasSize(2)
                .containsEntry(commitA, List.of(List.of(commitA, commitB, commitU, commitV)))
                .containsEntry(commitB, List.of(List.of(commitB, commitU, commitV)));
        assertThat(commitV.commonParentsWithPath(commitC))
                .hasSize(2)
                .containsKeys(commitA, commitB)
                .allSatisfy((k, v) -> {
                    if (k == commitA) {
                        assertThat(v).hasSize(2)
                                .containsExactlyInAnyOrder(List.of(commitA, commitB, commitC), List.of(commitA, commitF, commitG, commitH, commitC));
                    } else {
                        assertThat(v).hasSize(1)
                                .containsExactly(List.of(commitB, commitC));
                    }
                });

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(9)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitD,
                        commitF,
                        commitG,
                        commitH,
                        commitU,
                        commitV);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(9)
                .containsSubsequence(commitA, commitB, commitC, commitD)
                .containsSubsequence(commitA, commitF, commitG, commitH, commitC, commitD)
                .containsSubsequence(commitA, commitB, commitU, commitV, commitD);

    }

    @Test
    void diag06AddCommitsToDiagram() throws Exception {
        // tag::createDiagramInOrder[]
        // create commits:
        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);

        Commit commitF = new Commit("f").parent(commitA);
        Commit commitG = new Commit("g").parent(commitF);
        Commit commitH = new Commit("h").parent(commitG);

        Commit commitU = new Commit("u").parent(commitB);
        Commit commitV = new Commit("v").parent(commitU);

        Commit commitC = new Commit("c", Color.RED)
                .parent(commitH)
                .parent(commitB);
        Commit commitD = new Commit("d")
                .parent(commitC)
                .parent(commitV);

        // create diagram and branches:
        Diagram d = new Diagram("diag06");
        Branch branchMain = new Branch("main", Color.BLUE);
        Branch branch1 = new Branch("feature_1", Color.GRAY);
        Branch branch2 = new Branch("feature_2", Color.YELLOW);

        // add branches to diagram (can be omitted if you do not care about the vertical order of the branches):
        d.addBranch(branch1);
        d.addBranch(branchMain);
        d.addBranch(branch2);

        // add commits to diagram in order:
        d.addCommit(commitA, branchMain);
        d.addCommit(commitF, branch1);
        d.addCommit(commitB, branchMain);
        d.addCommit(commitG, branch1);
        d.addCommit(commitU, branch2);
        d.addCommit(commitH, branch1);
        d.addCommit(commitV, branch2);
        d.addCommit(commitC, branchMain);
        d.addCommit(commitD, branchMain);
        // end::createDiagramInOrder[]

        Diagram d6 = DiagTestUtil.diag06();
        List<String> expected = d6.allOrderedCommits()
                .stream()
                .map(Commit::getInternalName)
                .toList();
        System.out.println(expected);

        assertThat(d.allCommits())
                .hasSize(9)
                .extracting(Commit::getInternalName)
                .containsExactlyElementsOf(expected);
        assertThat(d.allOrderedCommits())
                .hasSize(9)
                .extracting(Commit::getInternalName)
                .containsExactlyElementsOf(expected);
    }

    @Test
    void diag08() throws Exception {
        Diagram d = DiagTestUtil.diag08();
        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");

        assertThat(commitA.isParentOf(commitB)).isTrue();
        assertThat(commitA.isParentOf(commitC)).isTrue();
        assertThat(commitA.isParentOf(commitA)).isFalse();

        assertThat(commitA.pathToChild(commitB)).hasSize(1)
                .allSatisfy(l -> assertThat(l).containsExactly(commitA, commitB));
        assertThat(commitA.pathToChild(commitC)).hasSize(1)
                .allSatisfy(l -> assertThat(l).containsExactly(commitA, commitB, commitC));

        assertThat(commitC.isChildOf(commitA)).isTrue();
        assertThat(commitC.isChildOf(commitB)).isTrue();
        assertThat(commitC.isChildOf(commitC)).isFalse();

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(d.allOrderedCommits()).containsExactly(commitA, commitB, commitC);
    }

    @Test
    void diag10() throws Exception {
        Diagram d = DiagTestUtil.diag10();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");
        Commit commitD = DiagTestUtil.findCommitByInternalName(d, "d");

        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");
        Commit commitH = DiagTestUtil.findCommitByInternalName(d, "h");
        Commit commitI = DiagTestUtil.findCommitByInternalName(d, "i");
        Commit commitL = DiagTestUtil.findCommitByInternalName(d, "l");
        Commit commitM = DiagTestUtil.findCommitByInternalName(d, "m");
        Commit commitN = DiagTestUtil.findCommitByInternalName(d, "n");
        Commit commitO = DiagTestUtil.findCommitByInternalName(d, "o");
        Commit commitU = DiagTestUtil.findCommitByInternalName(d, "u");
        Commit commitV = DiagTestUtil.findCommitByInternalName(d, "v");
        Commit commitW = DiagTestUtil.findCommitByInternalName(d, "w");
        Commit commitX = DiagTestUtil.findCommitByInternalName(d, "x");

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(16)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitD,
                        commitF,
                        commitG,
                        commitH,
                        commitI,
                        commitL,
                        commitM,
                        commitN,
                        commitO,
                        commitU,
                        commitV,
                        commitW,
                        commitX);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(16)
                .containsAll(allCommits)
                .containsSubsequence(commitA, commitB, commitC, commitD)
                .containsSubsequence(commitA, commitB, commitF, commitG, commitH, commitI)
                .containsSubsequence(commitA, commitB, commitL, commitM, commitN, commitO)
                .containsSubsequence(commitA, commitB, commitL, commitM, commitU, commitV, commitW, commitX);
    }

    @Test
    void diag11() throws Exception {
        Diagram d = DiagTestUtil.diag11();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");

        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");
        Commit commitH = DiagTestUtil.findCommitByInternalName(d, "h");

        Commit commitU = DiagTestUtil.findCommitByInternalName(d, "u");
        Commit commitV = DiagTestUtil.findCommitByInternalName(d, "v");
        Commit commitW = DiagTestUtil.findCommitByInternalName(d, "w");

        Comparator<Commit> comparator = new CommitComparator(d);
        assertThat(comparator.compare(commitA, commitB)).isNegative();
        assertThat(comparator.compare(commitC, commitW)).isNegative();

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(9)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitF,
                        commitG,
                        commitH,
                        commitU,
                        commitV,
                        commitW);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(9)
                .containsAll(allCommits)
                .containsSubsequence(commitA, commitB, commitC)
                .containsSubsequence(commitA, commitF, commitG, commitH)
                .containsSubsequence(commitA, commitU, commitV, commitW)
                .containsSubsequence(commitF, commitB, commitV)
                .containsSubsequence(commitG, commitC)
                .containsSubsequence(commitV, commitC)
                .containsSubsequence(commitC, commitH)
                .containsSubsequence(commitC, commitW);

    }

    @Test
    void diag12() throws Exception {
        Diagram d = DiagTestUtil.diag12();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");

        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");
        Commit commitH = DiagTestUtil.findCommitByInternalName(d, "h");
        Commit commitI = DiagTestUtil.findCommitByInternalName(d, "i");
        Commit commitJ = DiagTestUtil.findCommitByInternalName(d, "j");

        Commit commitU = DiagTestUtil.findCommitByInternalName(d, "u");
        Commit commitV = DiagTestUtil.findCommitByInternalName(d, "v");
        Commit commitW = DiagTestUtil.findCommitByInternalName(d, "w");
        Commit commitX = DiagTestUtil.findCommitByInternalName(d, "x");
        Commit commitY = DiagTestUtil.findCommitByInternalName(d, "y");
        Commit commitZ = DiagTestUtil.findCommitByInternalName(d, "z");

        assertThat(commitA.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitB)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitC)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitF)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitG)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitH)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitI)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitJ)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitU)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitV)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitW)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitX)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitY)).containsExactlyInAnyOrder(commitA);
        assertThat(commitA.commonParents(commitZ)).containsExactlyInAnyOrder(commitA);

        assertThat(commitB.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitB.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitC)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitF)).containsExactlyInAnyOrder(commitA);
        assertThat(commitB.commonParents(commitG)).containsExactlyInAnyOrder(commitA);
        assertThat(commitB.commonParents(commitH)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitI)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitJ)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitU)).containsExactlyInAnyOrder(commitA);
        assertThat(commitB.commonParents(commitV)).containsExactlyInAnyOrder(commitA);
        assertThat(commitB.commonParents(commitW)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitX)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitY)).containsExactlyInAnyOrder(commitB);
        assertThat(commitB.commonParents(commitZ)).containsExactlyInAnyOrder(commitB);

        assertThat(commitC.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitC.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitC.commonParents(commitC)).containsExactlyInAnyOrder(commitC);
        assertThat(commitC.commonParents(commitF)).containsExactlyInAnyOrder(commitA);
        assertThat(commitC.commonParents(commitG)).containsExactlyInAnyOrder(commitA);
        assertThat(commitC.commonParents(commitH)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitC.commonParents(commitI)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitC.commonParents(commitJ)).containsExactlyInAnyOrder(commitC);
        assertThat(commitC.commonParents(commitU)).containsExactlyInAnyOrder(commitA);
        assertThat(commitC.commonParents(commitV)).containsExactlyInAnyOrder(commitA);
        assertThat(commitC.commonParents(commitW)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitC.commonParents(commitX)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitC.commonParents(commitY)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitC.commonParents(commitZ)).containsExactlyInAnyOrder(commitC);

        assertThat(commitF.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitF.commonParents(commitB)).containsExactlyInAnyOrder(commitA);
        assertThat(commitF.commonParents(commitC)).containsExactlyInAnyOrder(commitA);
        assertThat(commitF.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitG)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitH)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitI)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitJ)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitU)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitV)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitW)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitX)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitY)).containsExactlyInAnyOrder(commitF);
        assertThat(commitF.commonParents(commitZ)).containsExactlyInAnyOrder(commitF);

        assertThat(commitG.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitG.commonParents(commitB)).containsExactlyInAnyOrder(commitA);
        assertThat(commitG.commonParents(commitC)).containsExactlyInAnyOrder(commitA);
        assertThat(commitG.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitG.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitH)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitI)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitJ)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitU)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitV)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitW)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitX)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitY)).containsExactlyInAnyOrder(commitG);
        assertThat(commitG.commonParents(commitZ)).containsExactlyInAnyOrder(commitG);

        assertThat(commitH.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitH.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitH.commonParents(commitC)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitH.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitH.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitH.commonParents(commitH)).containsExactlyInAnyOrder(commitH);
        assertThat(commitH.commonParents(commitI)).containsExactlyInAnyOrder(commitH);
        assertThat(commitH.commonParents(commitJ)).containsExactlyInAnyOrder(commitH);
        assertThat(commitH.commonParents(commitU)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitH.commonParents(commitV)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitH.commonParents(commitW)).containsExactlyInAnyOrder(commitH);
        assertThat(commitH.commonParents(commitX)).containsExactlyInAnyOrder(commitH);
        assertThat(commitH.commonParents(commitY)).containsExactlyInAnyOrder(commitH);
        assertThat(commitH.commonParents(commitZ)).containsExactlyInAnyOrder(commitH);

        assertThat(commitI.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitI.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitI.commonParents(commitC)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitI.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitI.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitI.commonParents(commitH)).containsExactlyInAnyOrder(commitH);
        assertThat(commitI.commonParents(commitI)).containsExactlyInAnyOrder(commitI);
        assertThat(commitI.commonParents(commitJ)).containsExactlyInAnyOrder(commitI);
        assertThat(commitI.commonParents(commitU)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitI.commonParents(commitV)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitI.commonParents(commitW)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitI.commonParents(commitX)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitI.commonParents(commitY)).containsExactlyInAnyOrder(commitI);
        assertThat(commitI.commonParents(commitZ)).containsExactlyInAnyOrder(commitI);

        assertThat(commitJ.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitJ.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitJ.commonParents(commitC)).containsExactlyInAnyOrder(commitC);
        assertThat(commitJ.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitJ.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitJ.commonParents(commitH)).containsExactlyInAnyOrder(commitH);
        assertThat(commitJ.commonParents(commitI)).containsExactlyInAnyOrder(commitI);
        assertThat(commitJ.commonParents(commitJ)).containsExactlyInAnyOrder(commitJ);
        assertThat(commitJ.commonParents(commitU)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitJ.commonParents(commitV)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitJ.commonParents(commitW)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitJ.commonParents(commitX)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitJ.commonParents(commitY)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH, commitI);
        assertThat(commitJ.commonParents(commitZ)).containsExactlyInAnyOrder(commitJ);

        assertThat(commitU.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitU.commonParents(commitB)).containsExactlyInAnyOrder(commitA);
        assertThat(commitU.commonParents(commitC)).containsExactlyInAnyOrder(commitA);
        assertThat(commitU.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitU.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitU.commonParents(commitH)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitU.commonParents(commitI)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitU.commonParents(commitJ)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitU.commonParents(commitU)).containsExactlyInAnyOrder(commitU);
        assertThat(commitU.commonParents(commitV)).containsExactlyInAnyOrder(commitU);
        assertThat(commitU.commonParents(commitW)).containsExactlyInAnyOrder(commitU);
        assertThat(commitU.commonParents(commitX)).containsExactlyInAnyOrder(commitU);
        assertThat(commitU.commonParents(commitY)).containsExactlyInAnyOrder(commitU);
        assertThat(commitU.commonParents(commitZ)).containsExactlyInAnyOrder(commitU);

        assertThat(commitV.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitV.commonParents(commitB)).containsExactlyInAnyOrder(commitA);
        assertThat(commitV.commonParents(commitC)).containsExactlyInAnyOrder(commitA);
        assertThat(commitV.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitV.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitV.commonParents(commitH)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitV.commonParents(commitI)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitV.commonParents(commitJ)).containsExactlyInAnyOrder(commitA, commitF, commitG);
        assertThat(commitV.commonParents(commitU)).containsExactlyInAnyOrder(commitU);
        assertThat(commitV.commonParents(commitV)).containsExactlyInAnyOrder(commitV);
        assertThat(commitV.commonParents(commitW)).containsExactlyInAnyOrder(commitV);
        assertThat(commitV.commonParents(commitX)).containsExactlyInAnyOrder(commitV);
        assertThat(commitV.commonParents(commitY)).containsExactlyInAnyOrder(commitV);
        assertThat(commitV.commonParents(commitZ)).containsExactlyInAnyOrder(commitV);

        assertThat(commitW.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitW.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitW.commonParents(commitC)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitW.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitW.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitW.commonParents(commitH)).containsExactlyInAnyOrder(commitH);
        assertThat(commitW.commonParents(commitI)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitW.commonParents(commitJ)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitW.commonParents(commitU)).containsExactlyInAnyOrder(commitU);
        assertThat(commitW.commonParents(commitV)).containsExactlyInAnyOrder(commitV);
        assertThat(commitW.commonParents(commitW)).containsExactlyInAnyOrder(commitW);
        assertThat(commitW.commonParents(commitX)).containsExactlyInAnyOrder(commitW);
        assertThat(commitW.commonParents(commitY)).containsExactlyInAnyOrder(commitW);
        assertThat(commitW.commonParents(commitZ)).containsExactlyInAnyOrder(commitW);

        assertThat(commitX.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitX.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitX.commonParents(commitC)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitX.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitX.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitX.commonParents(commitH)).containsExactlyInAnyOrder(commitH);
        assertThat(commitX.commonParents(commitI)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitX.commonParents(commitJ)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH);
        assertThat(commitX.commonParents(commitU)).containsExactlyInAnyOrder(commitU);
        assertThat(commitX.commonParents(commitV)).containsExactlyInAnyOrder(commitV);
        assertThat(commitX.commonParents(commitW)).containsExactlyInAnyOrder(commitW);
        assertThat(commitX.commonParents(commitX)).containsExactlyInAnyOrder(commitX);
        assertThat(commitX.commonParents(commitY)).containsExactlyInAnyOrder(commitX);
        assertThat(commitX.commonParents(commitZ)).containsExactlyInAnyOrder(commitX);

        assertThat(commitY.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitY.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitY.commonParents(commitC)).containsExactlyInAnyOrder(commitA, commitB);
        assertThat(commitY.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitY.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitY.commonParents(commitH)).containsExactlyInAnyOrder(commitH);
        assertThat(commitY.commonParents(commitI)).containsExactlyInAnyOrder(commitI);
        assertThat(commitY.commonParents(commitJ)).containsExactlyInAnyOrder(commitA, commitB, commitF, commitG, commitH, commitI);
        assertThat(commitY.commonParents(commitU)).containsExactlyInAnyOrder(commitU);
        assertThat(commitY.commonParents(commitV)).containsExactlyInAnyOrder(commitV);
        assertThat(commitY.commonParents(commitW)).containsExactlyInAnyOrder(commitW);
        assertThat(commitY.commonParents(commitX)).containsExactlyInAnyOrder(commitX);
        assertThat(commitY.commonParents(commitY)).containsExactlyInAnyOrder(commitY);
        assertThat(commitY.commonParents(commitZ)).containsExactlyInAnyOrder(commitY);

        assertThat(commitZ.commonParents(commitA)).containsExactlyInAnyOrder(commitA);
        assertThat(commitZ.commonParents(commitB)).containsExactlyInAnyOrder(commitB);
        assertThat(commitZ.commonParents(commitC)).containsExactlyInAnyOrder(commitC);
        assertThat(commitZ.commonParents(commitF)).containsExactlyInAnyOrder(commitF);
        assertThat(commitZ.commonParents(commitG)).containsExactlyInAnyOrder(commitG);
        assertThat(commitZ.commonParents(commitH)).containsExactlyInAnyOrder(commitH);
        assertThat(commitZ.commonParents(commitI)).containsExactlyInAnyOrder(commitI);
        assertThat(commitZ.commonParents(commitJ)).containsExactlyInAnyOrder(commitJ);
        assertThat(commitZ.commonParents(commitU)).containsExactlyInAnyOrder(commitU);
        assertThat(commitZ.commonParents(commitV)).containsExactlyInAnyOrder(commitV);
        assertThat(commitZ.commonParents(commitW)).containsExactlyInAnyOrder(commitW);
        assertThat(commitZ.commonParents(commitX)).containsExactlyInAnyOrder(commitX);
        assertThat(commitZ.commonParents(commitY)).containsExactlyInAnyOrder(commitY);
        assertThat(commitZ.commonParents(commitZ)).containsExactlyInAnyOrder(commitZ);

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(14)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitF,
                        commitG,
                        commitH,
                        commitI,
                        commitJ,
                        commitU,
                        commitV,
                        commitW,
                        commitX,
                        commitY,
                        commitZ);

        Comparator<Commit> comparator = new CommitComparator(d);
        assertThat(comparator.compare(commitJ, commitX)).isEqualTo(1);
        assertThat(comparator.compare(commitX, commitJ)).isEqualTo(-1);

        assertThat(comparator.compare(commitY, commitJ)).isEqualTo(-1);
        assertThat(comparator.compare(commitJ, commitY)).isEqualTo(1);
        assertThat(comparator.compare(commitJ, commitI)).isEqualTo(2);
        assertThat(comparator.compare(commitI, commitJ)).isEqualTo(-2);
        assertThat(comparator.compare(commitY, commitI)).isEqualTo(1);
        assertThat(comparator.compare(commitI, commitY)).isEqualTo(-1);
        assertThat(comparator.compare(commitJ, commitY)).isEqualTo(1);
        assertThat(comparator.compare(commitY, commitJ)).isEqualTo(-1);
        assertThat(comparator.compare(commitH, commitC)).isEqualTo(-1);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(14)
                .containsAll(allCommits)
                .containsSubsequence(commitA, commitB, commitC)
                .containsSubsequence(commitF, commitG, commitH, commitI, commitJ)
                .containsSubsequence(commitU, commitV, commitW, commitX, commitY, commitZ)
                .containsSubsequence(commitB, commitH, commitW)
                .containsSubsequence(commitI, commitY)
                .containsSubsequence(commitC, commitJ, commitZ)
                .containsSubsequence(commitH, commitC)
                .containsSubsequence(commitW, commitI)
                .containsSubsequence(commitY, commitJ);
    }

    @Test
    void diag13() throws Exception {
        Diagram d = DiagTestUtil.diag13();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");
        Commit commitD = DiagTestUtil.findCommitByInternalName(d, "d");

        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");

        assertThat(commitB.commonParentsWithPath(commitG)).isEmpty();
        assertThat(commitG.commonParentsWithPath(commitB)).isEmpty();

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(6)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitD,
                        commitF,
                        commitG);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(6)
                .containsAll(allCommits)
                .containsSubsequence(commitA, commitB, commitC, commitD)
                .containsSubsequence(commitF, commitG, commitC, commitD);
    }

    @Test
    void diag14() throws Exception {
        Diagram d = DiagTestUtil.diag14();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");
        Commit commitD = DiagTestUtil.findCommitByInternalName(d, "d");
        Commit commitE = DiagTestUtil.findCommitByInternalName(d, "e");
        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");

        Commit commitI = DiagTestUtil.findCommitByInternalName(d, "i");
        Commit commitJ = DiagTestUtil.findCommitByInternalName(d, "j");

        Comparator<Commit> comparator = new CommitComparator(d);
        assertThat(comparator.compare(commitA, commitD)).isNegative();
        assertThat(comparator.compare(commitB, commitD)).isNegative();
        assertThat(comparator.compare(commitC, commitD)).isNegative();
        assertThat(comparator.compare(commitI, commitD)).isNegative();

        assertThat(comparator.compare(commitB, commitI)).isNegative();
        assertThat(comparator.compare(commitC, commitI)).isPositive();

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(8)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitD,
                        commitE,
                        commitF,
                        commitI,
                        commitJ);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(8)
                .containsAll(allCommits)
                .containsSubsequence(commitA, commitB, commitC, commitD, commitE, commitF)
                .containsSubsequence(commitA, commitB, commitI, commitJ)
                .containsSubsequence(commitJ, commitE);
    }

    @Test
    void diag15() throws Exception {
        Diagram d = DiagTestUtil.diag15();

        Commit commitA = DiagTestUtil.findCommitByInternalName(d, "a");
        Commit commitB = DiagTestUtil.findCommitByInternalName(d, "b");
        Commit commitC = DiagTestUtil.findCommitByInternalName(d, "c");

        Commit commitE = DiagTestUtil.findCommitByInternalName(d, "e");
        Commit commitF = DiagTestUtil.findCommitByInternalName(d, "f");
        Commit commitG = DiagTestUtil.findCommitByInternalName(d, "g");

        Commit commitO = DiagTestUtil.findCommitByInternalName(d, "o");
        Commit commitP = DiagTestUtil.findCommitByInternalName(d, "p");

        Commit commitX = DiagTestUtil.findCommitByInternalName(d, "x");
        Commit commitY = DiagTestUtil.findCommitByInternalName(d, "y");

        assertThat(commitB.pathToChild(commitG)).hasSize(2)
                .anySatisfy(l -> assertThat(l).containsExactly(commitB, commitC, commitG))
                .anySatisfy(l -> assertThat(l).containsExactly(commitB, commitE, commitF, commitG));

        checkComparator(d, List.of(
                commitA,
                commitB,
                commitC,
                commitE,
                commitF,
                commitG,
                commitO,
                commitP,
                commitX,
                commitY));

        List<Commit> allCommits = d.allCommits();
        checkCommonParent(allCommits);
        checkComparator(d, allCommits);

        assertThat(allCommits)
                .hasSize(10)
                .contains(
                        commitA,
                        commitB,
                        commitC,
                        commitE,
                        commitF,
                        commitG,
                        commitO,
                        commitP,
                        commitX,
                        commitY);

        List<Commit> allOrderedCommits = d.allOrderedCommits();
        assertThat(allOrderedCommits)
                .hasSize(10)
                .containsAll(allCommits)
                .containsSubsequence(commitX, commitY, commitO, commitP)
                .containsSubsequence(commitX, commitY, commitC, commitG)
                .containsSubsequence(commitX, commitA, commitB, commitC)
                .containsSubsequence(commitX, commitA, commitB, commitC)
                .containsSubsequence(commitX, commitA, commitB, commitE, commitF, commitG);
    }

    private void checkCommonParent(List<Commit> list) {
        for (Commit e1 : list) {
            for (Commit e2 : list) {
                Set<Commit> p1 = e1.commonParents(e2);
                Set<Commit> p2 = e2.commonParents(e1);
                if (!Objects.equals(p1, p2)) {
                    fail("Expecting '[" + e1.getInternalName() + "].commonParents([" + e2.getInternalName() + "]) == " + p1 + "' to be the same as '[" + e2.getInternalName() + "].commonParents([" + e1.getInternalName() + "]) == " + p2
                            + "'");
                }
            }
        }
    }

    private void checkComparator(Diagram d, List<Commit> list) {
        Comparator<Commit> comparator = new CommitComparator(d);
        for (Commit e1 : list) {
            for (Commit e2 : list) {
                int cA = comparator.compare(e1, e2);
                int cB = comparator.compare(e2, e1);
                if (Math.signum(cA) != -1 * Math.signum(cB)) {
                    fail("Expecting 'comparator.compare([" + e1.getInternalName() + "], [" + e2.getInternalName() + "]) == " + cA + "' to have an opposite sign than 'comparator.compare([" + e2.getInternalName() + "], [" + e1
                            .getInternalName() + "]) == " + cB + "'");
                }
            }
        }

        List<String> errors = new ArrayList<>();
        List<List<Commit>> all = ListPermutationsUtil.computePermutations(list, 3);
        for (List<Commit> l : all) {
            Commit a = l.get(0);
            Commit b = l.get(1);
            Commit c = l.get(2);

            int d1 = comparator.compare(a, b);
            int d2 = comparator.compare(b, c);
            if (d1 < 0 && d2 < 0) {
                int d3 = comparator.compare(a, c);
                if (d3 >= 0) {
                    errors.add("compare([" + a.getInternalName() + "], [" + b.getInternalName() + "]) is " + d1 + " and compare([" + b.getInternalName() + "], [" + c.getInternalName() + "]) is " + d2 + " but compare([" + a.getInternalName()
                            + "], [" + c.getInternalName() + "]) is " + d3
                            + ".");
                }
            }
            if (d1 > 0 && d2 > 0) {
                int d3 = comparator.compare(a, c);
                if (d3 <= 0) {
                    errors.add("compare([" + a.getInternalName() + "], [" + b.getInternalName() + "]) is " + d1 + " and compare([" + b.getInternalName() + "], [" + c.getInternalName() + "]) is " + d2 + " but compare([" + a.getInternalName()
                            + "], [ " + c.getInternalName() + "]) is " + d3
                            + ".");
                }
            }
        }
        if (!errors.isEmpty()) {
            fail(String.join("\n", errors));
        }
    }
}
