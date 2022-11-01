package fr.jmini.gitgraph4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateDiagram {
    public static final Counter COUNTER = new Counter();
    public static final int commitSize = 20;
    public static final int diagramMinWidth = 400;

    public static String convertToDrawIoXml(Diagram d) {
        calculatePosition(d);

        StringBuilder sb = new StringBuilder();
        sb.append("""
                <mxfile compressed="false" version="17.4.2" type="device">
                <diagram id="a" name="Page-1">
                  <mxGraphModel dx="532" dy="766" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="413" pageHeight="583" math="0" shadow="0">
                    <root>
                      <mxCell id="0" />
                      <mxCell id="1" parent="0" />
                """);

        List<Branch> branches = d.branches;
        for (int i = 0; i < branches.size(); i++) {
            Branch b = branches.get(i);
            appendBranch(sb, d, b);
        }
        sb.append("""
                      </root>
                    </mxGraphModel>
                  </diagram>
                </mxfile>""");

        return sb.toString();
    }

    private static void calculatePosition(Diagram d) {
        COUNTER.reset();

        List<Commit> allOrderedCommits = d.allOrderedCommits();

        int initialStartPosition = 20;

        for (int i = 0; i < d.branches.size(); i++) {
            Branch branch = d.branches.get(i);
            branch.branchY = 30 * (i + 1);

            int branchBoxHeidth = 20;
            int branchBoxX = Math.max(diagramMinWidth - d.branchBoxWidth, initialStartPosition + allOrderedCommits.size() * 40 + d.branchBoxMargin);

            branch.box = new Shape();
            branch.box.graphId = "box" + COUNTER.get();
            branch.box.x = branchBoxX;
            branch.box.y = (branch.branchY - (branchBoxHeidth / 2));
            branch.box.width = d.branchBoxWidth;
            branch.box.height = branchBoxHeidth;
        }

        for (int i = 0; i < allOrderedCommits.size(); i++) {
            Commit c = allOrderedCommits.get(i);
            Shape shape = new Shape();
            shape.graphId = "b" + COUNTER.get();
            shape.x = initialStartPosition + i * 40;
            shape.y = c.getBranch().branchY - (commitSize / 2);
            shape.height = commitSize;
            shape.width = commitSize;
            c.setShape(shape);
        }
    }

    private static void appendBranch(StringBuilder sb, Diagram d, Branch b) {
        String style = style(b.color);

        sb.append("      <mxCell id=\"" + b.box.graphId + "\" value=\"" + b.name + "\" style=\"rounded=0;whiteSpace=wrap;html=1;" + style + "\" parent=\"1\" vertex=\"1\">\n");
        sb.append("        <mxGeometry x=\"" + b.box.x + "\" y=\"" + b.box.y + "\" width=\"" + b.box.width + "\" height=\"" + b.box.height + "\" as=\"geometry\" />\n");
        sb.append("      </mxCell>\n");

        List<Commit> commits = b.commits;
        for (int i = 0; i < commits.size(); i++) {
            Commit c = commits.get(i);
            appendCommit(sb, d, c);
        }
    }

    private static String style(Color color) {
        if (color == null) {
            return "";
        }
        switch (color) {
        case BLUE, GREEN, ORANGE, YELLOW, RED, PURPLE:
            return "fillColor=" + fillColor(color) + ";strokeColor=" + strokeColor(color) + ";";
        case GRAY:
            return "fillColor=" + fillColor(color) + ";strokeColor=" + strokeColor(color) + ";fontColor=#333333;";
        default:
            throw new IllegalArgumentException("Unexpected value: " + color);
        }
    }

    private static String fillColor(Color color) {
        if (color == null) {
            return "";
        }
        switch (color) {
        case BLUE:
            return "#dae8fc";
        case GREEN:
            return "#d5e8d4";
        case ORANGE:
            return "#ffe6cc";
        case YELLOW:
            return "#fff2cc";
        case RED:
            return "#f8cecc";
        case PURPLE:
            return "#e1d5e7";
        case GRAY:
            return "#f5f5f5";
        default:
            throw new IllegalArgumentException("Unexpected value: " + color);
        }
    }

    private static String strokeColor(Color color) {
        if (color == null) {
            return "";
        }
        switch (color) {
        case BLUE:
            return "#6c8ebf";
        case GREEN:
            return "#82b366";
        case ORANGE:
            return "#d79b00";
        case YELLOW:
            return "#d6b656";
        case RED:
            return "#b85450";
        case PURPLE:
            return "#9673a6";
        case GRAY:
            return "#666666";
        default:
            throw new IllegalArgumentException("Unexpected value: " + color);
        }
    }

    private static void appendCommit(StringBuilder sb, Diagram d, Commit c) {
        Color color = (c.getColor() != null) ? c.getColor() : c.getBranch().color;
        String style = style(color);

        if (c.getParents()
                .isEmpty()) {
            appendLine(sb, d, null, null, c.getShape(), new LineStyle());
        } else {
            for (Map.Entry<Commit, LineStyle> e : c.getParentsWithStyle()) {
                Commit parent = e.getKey();
                LineStyle lineStyle = e.getValue();
                AnchorLocation fromLocation;
                if (Objects.equals(c.getBranch().name, parent.getBranch().name)) {
                    fromLocation = AnchorLocation.RIGHT;
                } else if (c.getBranch().commits.get(0) == c) {
                    fromLocation = AnchorLocation.TOP_OR_BOTTOM;
                } else {
                    fromLocation = AnchorLocation.RIGHT;
                }
                appendLine(sb, d, parent.getShape(), fromLocation, c.getShape(), lineStyle);
            }
        }

        String commitValue = c.getLabel() == null ? "" : c.getLabel();
        sb.append("      <mxCell id=\"" + c.getShape().graphId + "\" value=\"" + commitValue + "\" style=\"ellipse;whiteSpace=wrap;html=1;" + style + "\" vertex=\"1\" parent=\"1\">\n");
        sb.append("        <mxGeometry x=\"" + c.getShape().x + "\" y=\"" + c.getShape().y + "\" width=\"" + c.getShape().width + "\" height=\"" + c.getShape().height + "\" as=\"geometry\" />\n");
        sb.append("      </mxCell>\n");

        if (c.getBranch()
                .isOpenToEnd() && c.getBranch().commits.indexOf(c) == c.getBranch().commits.size() - 1) {
            appendLine(sb, d, c.getShape(), AnchorLocation.RIGHT, c.getBranch().box, new LineStyle());
        }
    }

    private static void appendLine(StringBuilder sb, Diagram d, Shape from, AnchorLocation fromLocation, Shape to, LineStyle lineStyle) {
        String sourceAttribute;
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;
        String exitStyle;
        String entryStyle;
        boolean differentBranches;
        if (from != null) {
            sourceAttribute = " source=\"" + from.graphId + "\"";
            if (fromLocation == AnchorLocation.RIGHT || lineStyle.type == Type.DIRECT || from.y == to.y) {
                xStart = from.xRight();
                yStart = from.yCenter();
                if (lineStyle.type == Type.DIRECT && from.y < to.y) {
                    exitStyle = "exitX=1;exitY=1;exitDx=0;exitDy=0;";
                    entryStyle = "entryX=0;entryY=0;entryDx=0;entryDy=0;";
                } else if (lineStyle.type == Type.DIRECT && from.y > to.y) {
                    exitStyle = "exitX=1;exitY=0;exitDx=0;exitDy=0;";
                    entryStyle = "entryX=0;entryY=1;entryDx=0;entryDy=0;";
                } else {
                    exitStyle = "exitX=1;exitY=0.5;exitDx=0;exitDy=0;";
                    entryStyle = "entryX=0;entryY=0.5;entryDx=0;entryDy=0;";
                }
                differentBranches = (from.y != to.y);
            } else if (from.y < to.y) {
                xStart = from.xCenter();
                yStart = from.yTop();
                exitStyle = "exitX=0.5;exitY=1;exitDx=0;exitDy=0;";
                entryStyle = "entryX=0;entryY=0.5;entryDx=0;entryDy=0;";
                differentBranches = true;
            } else {
                xStart = from.xCenter();
                yStart = from.yBottom();
                exitStyle = "exitX=0.5;exitY=0;exitDx=0;exitDy=0;";
                entryStyle = "entryX=0;entryY=0.5;entryDx=0;entryDy=0;";
                differentBranches = true;
            }
        } else {
            sourceAttribute = "";
            xStart = 0;
            yStart = to.yCenter();
            exitStyle = "";
            entryStyle = "entryX=0;entryY=0.5;entryDx=0;entryDy=0;";
            differentBranches = false;
        }
        xEnd = to.xLeft();
        yEnd = to.yCenter();

        String targetAttribute = " target=\"" + to.graphId + "\"";

        String strokeStyle;
        if (lineStyle.color == null) {
            strokeStyle = "";
        } else {
            strokeStyle = "strokeColor=" + lineStyle.color + ";";
        }
        String endArrow;
        String endAttributes;
        if (lineStyle.arrow) {
            endArrow = "endArrow=open;";
            endAttributes = "endSize=3;endFill=1;";
        } else {
            endArrow = "endArrow=none;";
            endAttributes = "";
        }
        String strokeWidth;
        if (lineStyle.width == null) {
            strokeWidth = "";
        } else {
            strokeWidth = "strokeWidth=" + lineStyle.width + ";";
        }
        String dashed;
        if (lineStyle.style == null) {
            dashed = "";
        } else {
            switch (lineStyle.style) {
            case DASHED:
                dashed = "dashed=1;";
                break;
            case DOTTED:
                dashed = "dashed=1;dashPattern=1 1;";
                break;
            case SOLID:
                dashed = "";
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + lineStyle.style);
            }
        }
        String lineId = "l" + COUNTER.get();
        sb.append("    <mxCell id=\"" + lineId + "\" value=\"\" style=\"" + endArrow + "html=1;rounded=1;" + exitStyle + entryStyle + endAttributes + strokeWidth + strokeStyle + dashed
                + "\" edge=\"1\" parent=\"1\""
                + sourceAttribute
                + targetAttribute
                + ">\n");
        sb.append("      <mxGeometry width=\"50\" height=\"50\" relative=\"1\" as=\"geometry\">\n");
        sb.append("        <mxPoint x=\"" + xStart + "\" y=\"" + yStart + "\" as=\"sourcePoint\" />\n");
        sb.append("        <mxPoint x=\"" + xEnd + "\" y=\"" + yEnd + "\" as=\"targetPoint\" />\n");
        if (lineStyle.type == Type.NORMAL && differentBranches) {
            sb.append("        <Array as=\"points\">\n");
            if (fromLocation == AnchorLocation.RIGHT) {
                int xPoint = to.xLeft() - 10;
                int y;
                if (lineStyle.arrow && from.y != to.y) {
                    if (from.y < to.y) {
                        y = yEnd - 10;
                    } else {
                        y = yEnd + 10;
                    }
                } else {
                    y = yEnd;
                }
                sb.append("          <mxPoint x=\"" + xPoint + "\" y=\"" + yStart + "\" />\n");
                sb.append("          <mxPoint x=\"" + xPoint + "\" y=\"" + y + "\" />\n");
            } else {
                sb.append("          <mxPoint x=\"" + xStart + "\" y=\"" + yEnd + "\" />\n");
            }
            sb.append("        </Array>\n");
        }
        sb.append("      </mxGeometry>\n");
        sb.append("    </mxCell>\n");
    }

    public static class Counter {
        private int i = 0;

        public int get() {
            i = i + 1;
            return i;
        }

        public void reset() {
            i = 0;
        }

    }

    public static String convertToMermaid(Diagram d) {
        List<Commit> allOrderedCommits = d.allOrderedCommits();

        Commit firstCommit = allOrderedCommits.get(0);
        String mainBranchName;
        Branch currentBranch = firstCommit.getBranch();
        if (Objects.equals(toBranchName(currentBranch), "main")) {
            mainBranchName = "";
        } else {
            mainBranchName = ", 'mainBranchName': '" + toBranchName(currentBranch) + "'";
        }

        String mainBranchOrder;
        if (d.branchIndex(firstCommit) == 0) {
            mainBranchOrder = "";
        } else {
            mainBranchOrder = ", 'mainBranchOrder': " + d.branchIndex(firstCommit);
        }

        firstCommit.getBranch();
        StringBuilder sb = new StringBuilder();
        sb.append("%%{init: { 'logLevel': 'debug', 'theme': 'base', 'gitGraph': {'showBranches': true, 'showCommitLabel':true" + mainBranchName + mainBranchOrder + "}} }%%\n");
        sb.append("gitGraph\n");
        appendCommit(sb, firstCommit);
        List<Branch> remainingBranches = d.branches.stream()
                .filter(b -> b != firstCommit.getBranch())
                .toList();
        for (Branch b : remainingBranches) {
            sb.append("     branch " + toBranchName(b) + " order: " + d.branches.indexOf(b) + "\n");
            currentBranch = b;
        }

        for (int i = 1; i < allOrderedCommits.size(); i++) {
            Commit c = allOrderedCommits.get(i);
            if (c.getBranch() != currentBranch) {
                sb.append("     checkout " + toBranchName(c.getBranch()) + "\n");
                currentBranch = c.getBranch();
            }
            if (c.getParents()
                    .size() == 1) {
                appendCommit(sb, c);
            } else {
                if (!c.getParents()
                        .stream()
                        .anyMatch(p -> p.getBranch() == c.getBranch())) {
                    System.err.println("Expecting one of the parents of " + c + " to be on branch " + c.getBranch().name);
                }
                List<Commit> toMerge = c.getParents()
                        .stream()
                        .filter(p -> p.getBranch() != c.getBranch())
                        .sorted(Comparator.comparingInt(d::branchIndex)
                                .reversed())
                        .toList();
                if (toMerge.size() > 1) {
                    System.err.println("Mermaid does not support merging more than one branch (parent of " + c + "), creating " + toMerge.size() + " separated merge commit");
                }
                toMerge.forEach(p -> {
                    sb.append("     merge " + toBranchName(p.getBranch()) + "\n");
                });
            }
        }
        return sb.toString();
    }

    private static String toBranchName(Branch currentBranch) {
        String result = currentBranch.name
                .replace(" ", "")
                .replace("branch-", "branch_");
        if (result.length() < 2) {
            result = "branch_" + result;
        }
        return result;
    }

    private static void appendCommit(StringBuilder sb, Commit c) {
        sb.append("     commit");
        String label = c.getLabel();
        if (label != null && !label.isBlank()) {
            sb.append(" id:\"" + label + "\"");
        }
        sb.append("\n");
    }

    public static String convertToDot(Diagram d) {
        Counter counter = new Counter();
        List<Commit> allOrderedCommits = d.allOrderedCommits();

        Map<Branch, String> branchMap = new HashMap<>();
        Map<Commit, String> commitMap = new HashMap<>();

        for (Branch b : d.branches) {
            for (Commit c : b.commits) {
                if (c.getInternalName()
                        .matches("[a-z0-9]{0,5}")) {
                    commitMap.put(c, c.getInternalName());
                } else {
                    commitMap.put(c, "c" + counter.get());
                }
            }
            branchMap.put(b, "box" + counter.get());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("    rankdir=\"LR\";\n");
        sb.append("    bgcolor=\"white\"\n");
        sb.append("    node[shape=circle; style=filled; fixedsize=true; width=0.25, fontcolor=black, label=\"\"];\n");
        sb.append("    edge[weight=2, arrowhead=none, color=black];\n");
        sb.append("\n");
        for (Branch b : d.branches) {
            sb.append("    node[group=\"g" + branchMap.get(b) + "\", color=\"" + strokeColor(b.color) + "\"; fillcolor=\"" + fillColor(b.color) + "\";];\n");
            for (Commit c : b.commits) {
                List<String> attrList = new ArrayList<>();
                if (c.getLabel() != null) {
                    attrList.add("label=\"" + c.getLabel() + "\"");
                }
                if (c.getColor() != null) {
                    attrList.add("color=\"" + strokeColor(c.getColor()) + "\"");
                    attrList.add("fillcolor=\"" + fillColor(c.getColor()) + "\"");
                }
                String attrValue = toAttributeValue(attrList);
                sb.append("    " + commitMap.get(c) + attrValue + ";\n");
            }
            sb.append("    " + branchMap.get(b) + "[shape=box; style=\"\"; fixedsize=false; label=\"" + b.name + "\"];\n");
            sb.append("\n");
        }
        for (Commit c : allOrderedCommits) {
            c.getParentsWithStyle()
                    .stream()
                    .sorted(Comparator.comparingInt(e -> d.branchIndex(e.getKey())))
                    .forEach(e -> {
                        LineStyle style = e.getValue();
                        List<String> attrList = new ArrayList<>();
                        if (style.color != null) {
                            attrList.add("color=\"" + style.color + "\"");
                        }
                        if (style.arrow) {
                            attrList.add("arrowhead=open");
                        }
                        if (style.width != null) {
                            attrList.add("penwidth=" + style.width);
                        }
                        if (style.style != null) {
                            attrList.add("style=" + style.style.name()
                                    .toLowerCase());
                        }
                        String attrValue = toAttributeValue(attrList);
                        sb.append("    " + commitMap.get(e.getKey()) + " -> " + commitMap.get(c) + attrValue + "\n");
                    });
        }
        for (Branch b : d.branches) {
            String style = b.isOpenToEnd() ? "" : " [style=invis]";
            Commit lastCommit = b.commits.get(b.commits.size() - 1);
            sb.append("    " + commitMap.get(lastCommit) + " -> " + branchMap.get(b) + style + "\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String toAttributeValue(List<String> list) {
        return list.isEmpty() ? ""
                : list.stream()
                        .collect(Collectors.joining(", ", " [", "]"));
    }

    private CreateDiagram() {
    }

}
