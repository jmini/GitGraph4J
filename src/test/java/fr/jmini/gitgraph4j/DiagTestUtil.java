package fr.jmini.gitgraph4j;

import java.util.List;
import java.util.Objects;

public class DiagTestUtil {

    private DiagTestUtil() {
    }

    public static List<Diagram> getAllDiagrams() {
        return List.of(
                diag01(),
                diag02(),
                diag03(),
                diag04(),
                diag05(),
                diag06(),
                diag07(),
                diag08(),
                diag09(),
                diag10(),
                diag11(),
                diag12(),
                diag13(),
                diag14(),
                diag15(),
                diag16());
    }

    public static Diagram diag01() {
        LineStyle redColor = new LineStyle();
        redColor.color = "#FF0000";
        LineStyle greenColor = new LineStyle();
        greenColor.color = "#00CC00";

        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA, redColor);

        Commit commitF = new Commit("f").parent(commitA, greenColor);

        Commit commitC = new Commit("c")
                .parent(commitB)
                .parent(commitF);

        Branch b = new Branch("main", Color.BLUE);
        b.addCommit(commitA);
        b.addCommit(commitB);
        b.addCommit(commitC);

        Branch b1 = new Branch("feature", Color.ORANGE);
        b1.addCommit(commitF);

        Diagram d = new Diagram("diag01");
        d.addBranch(b1);
        d.addBranch(b);
        return d;
    }

    public static Diagram diag02() {
        LineStyle strong = new LineStyle();
        strong.width = 3;

        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c").parent(commitB, strong);
        Commit commitD = new Commit("d").parent(commitC);

        Commit commitF = new Commit("f").parent(commitB, strong);
        Commit commitG = new Commit("g").parent(commitF);

        Branch b = new Branch("main", Color.BLUE);
        b.addCommit(commitA);
        b.addCommit(commitB);
        b.addCommit(commitC);
        b.addCommit(commitD);

        Branch b1 = new Branch("feature", Color.GREEN);
        b1.addCommit(commitF);
        b1.addCommit(commitG);

        Diagram d = new Diagram("diag02");
        d.addBranch(b);
        d.addBranch(b1);

        return d;
    }

    public static Diagram diag03() {
        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);

        Commit commitF = new Commit("f").parent(commitA);
        Commit commitG = new Commit("g").parent(commitF);

        Commit commitC = new Commit("m", Color.YELLOW)
                .parent(commitB)
                .parent(commitG);
        Commit commitX = new Commit("x").parent(commitA);
        ;

        Branch b = new Branch("main", Color.BLUE);
        b.addCommit(commitA);
        b.addCommit(commitB);
        b.addCommit(commitC);

        Branch b2 = new Branch("feature-2", Color.ORANGE);
        b2.addCommit(commitF);
        b2.addCommit(commitG);

        Branch b1 = new Branch("feature-1", Color.GRAY).withOpenToEnd(false);
        b1.addCommit(commitX);

        Diagram d = new Diagram("diag03");
        d.addBranch(b1);
        d.addBranch(b2);
        d.addBranch(b);

        return d;
    }

    public static Diagram diag04() {
        Commit commitA = new Commit("a", "V1.0");
        Commit commitB = new Commit("b", "V2.0").parent(commitA);

        Commit commitF = new Commit("f", "").parent(commitA);
        Commit commitG = new Commit("g", "").parent(commitF);
        Commit commitH = new Commit("h", "").parent(commitG);

        Commit commitC = new Commit("c", "V2.1")
                .parent(commitB)
                .parent(commitG);

        Branch b = new Branch("master", Color.BLUE);
        Branch b1 = new Branch("feature", Color.PURPLE);

        Diagram d = new Diagram("diag04");
        d.addCommit(commitA, b);
        d.addCommit(commitF, b1);
        d.addCommit(commitB, b);
        d.addCommit(commitG, b1);
        d.addCommit(commitC, b);
        d.addCommit(commitH, b1);

        return d;
    }

    public static Diagram diag05() {
        Commit commitZ = new Commit("z");
        Commit commitA = new Commit("a").parent(commitZ);
        Commit commitB = new Commit("b").parent(commitA);

        Commit commitF = new Commit("f").parent(commitA);
        Commit commitG = new Commit("g").parent(commitF);
        Commit commitH = new Commit("h").parent(commitG);

        Commit commitC = new Commit("c")
                .parent(commitH)
                .parent(commitB);

        Branch b = new Branch("main", Color.BLUE);
        b.addCommit(commitZ);
        b.addCommit(commitA);
        b.addCommit(commitB);
        b.addCommit(commitC);

        Branch b1 = new Branch("very-long-feature-branch", Color.RED);
        b1.addCommit(commitF);
        b1.addCommit(commitG);
        b1.addCommit(commitH);

        Diagram d = new Diagram("diag05");
        d.addBranch(b);
        d.addBranch(b1);
        d.branchBoxWidth = 160;

        return d;
    }

    public static Diagram diag06() {
        // tag::createDiagram[]
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

        // add commits to branches:
        Branch branchMain = new Branch("main", Color.BLUE);
        branchMain.addCommit(commitA);
        branchMain.addCommit(commitB);
        branchMain.addCommit(commitC);
        branchMain.addCommit(commitD);

        Branch branch1 = new Branch("feature_1", Color.GRAY);
        branch1.addCommit(commitF);
        branch1.addCommit(commitG);
        branch1.addCommit(commitH);

        Branch branch2 = new Branch("feature_2", Color.YELLOW);
        branch2.addCommit(commitU);
        branch2.addCommit(commitV);

        // add branches to diagram:
        Diagram d = new Diagram("diag06");
        d.addBranch(branch1);
        d.addBranch(branchMain);
        d.addBranch(branch2);
        // end::createDiagram[]

        return d;
    }

    public static Diagram diag07() {
        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);

        Commit commitF = new Commit("f").parent(commitA);
        Commit commitG = new Commit("g").parent(commitF);
        Commit commitH = new Commit("h")
                .parent(commitG)
                .parent(commitB);

        Branch b1 = new Branch("branch 1", Color.BLUE)
                .withOpenToEnd(true);
        b1.addCommit(commitA);
        b1.addCommit(commitB);

        Branch b2 = new Branch("branch 2", Color.PURPLE);
        b2.addCommit(commitF);
        b2.addCommit(commitG);
        b2.addCommit(commitH);

        Diagram d = new Diagram("diag07");
        d.addBranch(b1);
        d.addBranch(b2);

        return d;
    }

    public static Diagram diag08() {
        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c").parent(commitB);

        Branch b1 = new Branch("branch", Color.BLUE)
                .withOpenToEnd(false);
        b1.addCommit(commitA);
        b1.addCommit(commitB);
        b1.addCommit(commitC);

        Diagram d = new Diagram("diag08");
        d.addBranch(b1);

        return d;
    }

    public static Diagram diag09() {
        Commit commitA = new Commit(null, "a");
        Commit commitB = new Commit(null, "b").parent(commitA);
        Commit commitC = new Commit(null, "c").parent(commitB);

        Commit commitF = new Commit(null, "f").parent(commitA);
        Commit commitG = new Commit(null, "g").parent(commitF);
        Commit commitH = new Commit(null, "h")
                .parent(commitG)
                .parent(commitB);
        Commit commitI = new Commit(null, "i")
                .parent(commitH)
                .parent(commitC);

        Branch b1 = new Branch("Branch 1", Color.BLUE);
        b1.addCommit(commitA);
        b1.addCommit(commitB);
        b1.addCommit(commitC);

        Branch b2 = new Branch("Branch 2", Color.PURPLE);
        b2.addCommit(commitF);
        b2.addCommit(commitG);
        b2.addCommit(commitH);
        b2.addCommit(commitI);

        Diagram d = new Diagram("diag09");
        d.addBranch(b1);
        d.addBranch(b2);

        return d;
    }

    public static Diagram diag10() {
        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c").parent(commitB);
        Commit commitD = new Commit("d").parent(commitC);

        Commit commitF = new Commit("f").parent(commitB);
        Commit commitG = new Commit("g").parent(commitF);
        Commit commitH = new Commit("h")
                .parent(commitG)
                .parent(commitC);
        Commit commitI = new Commit("i")
                .parent(commitH)
                .parent(commitD);

        Commit commitL = new Commit("l").parent(commitB);
        Commit commitM = new Commit("m").parent(commitL);
        Commit commitN = new Commit("n")
                .parent(commitM)
                .parent(commitD);
        Commit commitO = new Commit("o").parent(commitN);

        Commit commitU = new Commit("u").parent(commitM);
        Commit commitV = new Commit("v").parent(commitU);
        Commit commitW = new Commit("w")
                .parent(commitV)
                .parent(commitN);
        Commit commitX = new Commit("x").parent(commitW);

        Branch b1 = new Branch("main", Color.BLUE);
        b1.addCommit(commitA);
        b1.addCommit(commitB);
        b1.addCommit(commitC);
        b1.addCommit(commitD);

        Branch b2 = new Branch("feature", Color.PURPLE);
        b2.addCommit(commitF);
        b2.addCommit(commitG);
        b2.addCommit(commitH);
        b2.addCommit(commitI);

        Branch b3 = new Branch("b3", Color.GREEN);
        b3.addCommit(commitL);
        b3.addCommit(commitM);
        b3.addCommit(commitN);
        b3.addCommit(commitO);

        Branch b4 = new Branch("b4", Color.GRAY);
        b4.addCommit(commitU);
        b4.addCommit(commitV);
        b4.addCommit(commitW);
        b4.addCommit(commitX);

        Diagram d = new Diagram("diag10");
        d.addBranch(b2);
        d.addBranch(b1);
        d.addBranch(b3);
        d.addBranch(b4);

        return d;
    }

    public static Diagram diag11() {
        LineStyle style = new LineStyle();
        style.arrow = true;
        style.width = 3;

        Commit commitA = new Commit("a");
        Commit commitF = new Commit("f").parent(commitA, style);
        Commit commitU = new Commit("u").parent(commitA);

        Commit commitB = new Commit("b")
                .parent(commitA)
                .parent(commitF, style)
                .parent(commitU);
        Commit commitG = new Commit("g")
                .parent(commitF)
                .parent(commitB, style);
        Commit commitV = new Commit("v")
                .parent(commitU)
                .parent(commitB);

        Commit commitC = new Commit("c")
                .parent(commitB)
                .parent(commitG)
                .parent(commitV);

        Commit commitH = new Commit("h")
                .parent(commitG, style);

        Commit commitW = new Commit("w")
                .parent(commitV);

        Branch b1 = new Branch("l", Color.PURPLE);
        b1.addCommit(commitF);
        b1.addCommit(commitG);
        b1.addCommit(commitH);

        Branch b2 = new Branch("m", Color.BLUE);
        b2.addCommit(commitA);
        b2.addCommit(commitB);
        b2.addCommit(commitC);

        Branch b3 = new Branch("n", Color.GREEN);
        b3.addCommit(commitU);
        b3.addCommit(commitV);
        b3.addCommit(commitW);

        Diagram d = new Diagram("diag11");
        d.addBranch(b1);
        d.addBranch(b2);
        d.addBranch(b3);
        d.branchBoxMargin = 100;

        return d;
    }

    public static Diagram diag12() {
        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c").parent(commitB);

        Commit commitF = new Commit("f").parent(commitA);
        Commit commitG = new Commit("g").parent(commitF);
        Commit commitH = new Commit("h")
                .parent(commitG)
                .parent(commitB);
        Commit commitI = new Commit("i").parent(commitH);
        Commit commitJ = new Commit("j")
                .parent(commitI)
                .parent(commitC);

        Commit commitU = new Commit("u").parent(commitG);
        Commit commitV = new Commit("v").parent(commitU);
        Commit commitW = new Commit("w")
                .parent(commitV)
                .parent(commitH);
        Commit commitX = new Commit("x").parent(commitW);
        Commit commitY = new Commit("y")
                .parent(commitX)
                .parent(commitI);
        Commit commitZ = new Commit("z")
                .parent(commitY)
                .parent(commitJ);

        Branch b1 = new Branch("b1", Color.BLUE);
        b1.addCommit(commitA);
        b1.addCommit(commitB);
        b1.addCommit(commitC);

        Branch b2 = new Branch("b2", Color.PURPLE);
        b2.addCommit(commitF);
        b2.addCommit(commitG);
        b2.addCommit(commitH);
        b2.addCommit(commitI);
        b2.addCommit(commitJ);

        Branch b3 = new Branch("b3", Color.GREEN);
        b3.addCommit(commitU);
        b3.addCommit(commitV);
        b3.addCommit(commitW);
        b3.addCommit(commitX);
        b3.addCommit(commitY);
        b3.addCommit(commitZ);

        Diagram d = new Diagram("diag12");
        d.addBranch(b1);
        d.addBranch(b2);
        d.addBranch(b3);
        return d;
    }

    public static Diagram diag13() {
        Commit commitF = new Commit("f");
        Commit commitG = new Commit("g").parent(commitF);

        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c")
                .parent(commitB)
                .parent(commitG);
        Commit commitD = new Commit("d").parent(commitC);

        Branch b = new Branch("main", Color.BLUE);
        b.addCommit(commitA);
        b.addCommit(commitB);
        b.addCommit(commitC);
        b.addCommit(commitD);

        Branch b1 = new Branch("feature", Color.RED);
        b1.addCommit(commitF);
        b1.addCommit(commitG);

        Diagram d = new Diagram("diag13");
        d.addBranch(b);
        d.addBranch(b1);

        return d;
    }

    public static Diagram diag14() {
        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c").parent(commitB);
        Commit commitD = new Commit("d").parent(commitC);
        Commit commitE = new Commit("e").parent(commitD);
        Commit commitF = new Commit("f").parent(commitE);

        Commit commitI = new Commit("i").parent(commitB);
        Commit commitJ = new Commit("j").parent(commitI)
                .parent(commitD);

        Branch bFeature = new Branch("feature", Color.BLUE).withOpenToEnd(true);
        bFeature.addCommit(commitI);
        bFeature.addCommit(commitJ);

        Branch bMain = new Branch("main", Color.GREEN).withOpenToEnd(true);
        bMain.addCommit(commitA);
        bMain.addCommit(commitB);
        bMain.addCommit(commitC);
        bMain.addCommit(commitD);
        bMain.addCommit(commitE);
        bMain.addCommit(commitF);

        Diagram d = new Diagram("diag14");
        d.addBranch(bFeature);
        d.addBranch(bMain);

        return d;
    }

    public static Diagram diag15() {
        Commit commitX = new Commit("x");
        Commit commitY = new Commit("y")
                .parent(commitX);

        Commit commitA = new Commit("a").parent(commitX);

        Commit commitO = new Commit("o").parent(commitY);
        Commit commitP = new Commit("p").parent(commitO);

        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c").parent(commitB)
                .parent(commitY);

        Commit commitE = new Commit("e").parent(commitB);
        Commit commitF = new Commit("f").parent(commitE);
        Commit commitG = new Commit("g")
                .parent(commitF)
                .parent(commitC);

        Branch b0 = new Branch("branch-0", Color.RED).withOpenToEnd(false);
        b0.addCommit(commitO);
        b0.addCommit(commitP);

        Branch b1 = new Branch("branch-1", Color.BLUE).withOpenToEnd(true);
        b1.addCommit(commitX);
        b1.addCommit(commitY);

        Branch b2 = new Branch("branch-2", Color.PURPLE).withOpenToEnd(true);
        b2.addCommit(commitA);
        b2.addCommit(commitB);
        b2.addCommit(commitC);

        Branch b3 = new Branch("branch-3", Color.GREEN).withOpenToEnd(true);
        b3.addCommit(commitE);
        b3.addCommit(commitF);
        b3.addCommit(commitG);

        Diagram d = new Diagram("diag15");
        d.addBranch(b0);
        d.addBranch(b1);
        d.addBranch(b2);
        d.addBranch(b3);

        return d;
    }

    public static Diagram diag16() {
        LineStyle style = new LineStyle();
        style.arrow = true;
        style.width = 3;
        style.color = "#CC0000";
        style.style = Style.DOTTED;
        style.type = Type.DIRECT;

        Commit commitA = new Commit("a");
        Commit commitB = new Commit("b").parent(commitA);
        Commit commitC = new Commit("c").parent(commitB);
        Commit commitD = new Commit("d").parent(commitC);

        Commit commitX = new Commit("x").parent(commitB);
        Commit commitY = new Commit("y")
                .parent(commitX)
                .parent(commitD, style);

        Commit commitU = new Commit("u").parent(commitB);
        Commit commitV = new Commit("v").parent(commitU)
                .parent(commitC, style);

        Commit commitJ = new Commit("j").parent(commitA);
        Commit commitK = new Commit("k")
                .parent(commitJ)
                .parent(commitD, style);

        Branch b1 = new Branch("branch1", Color.BLUE).withOpenToEnd(true);
        Branch b2 = new Branch("branch2", Color.PURPLE).withOpenToEnd(true);
        Branch bMain = new Branch("main", Color.GREEN).withOpenToEnd(true);
        Branch b3 = new Branch("branch3", Color.GRAY).withOpenToEnd(true);

        Diagram d = new Diagram("diag16");
        d.addBranch(b1);
        d.addBranch(b2);
        d.addBranch(bMain);
        d.addBranch(b3);

        d.addCommit(commitA, bMain);
        d.addCommit(commitJ, b1);
        d.addCommit(commitB, bMain);
        d.addCommit(commitX, b2);
        d.addCommit(commitU, b3);
        d.addCommit(commitC, bMain);
        d.addCommit(commitV, b3);
        d.addCommit(commitD, bMain);
        d.addCommit(commitK, b1);
        d.addCommit(commitY, b2);

        d.branchBoxWidth = 180;
        d.branchBoxMargin = 20;

        return d;
    }

    public static Commit findCommitByInternalName(Diagram d, String internalName) {
        return findCommitByName(d.allCommits(), internalName);
    }

    public static Commit findCommitByName(List<Commit> list, String internalName) {
        return list.stream()
                .filter(c -> Objects.equals(c.getInternalName(), internalName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not find commit with internal name '" + internalName + "'"));
    }
}
