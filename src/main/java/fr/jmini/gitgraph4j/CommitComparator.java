package fr.jmini.gitgraph4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommitComparator implements Comparator<Commit> {

    private Diagram diagram;

    public CommitComparator(Diagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public int compare(Commit o1, Commit o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1.isDirectParentOf(o2)) {
            return -1 * distParentAndChild(o1, o2);
        }
        if (o1.isDirectChildOf(o2)) {
            return distParentAndChild(o2, o1);
        }
        Map<Commit, List<List<Commit>>> commonParents = o1.commonParentsWithPath(o2);
        if (!commonParents.isEmpty()) {
            List<Holder> commonParentHolders = commonParents.entrySet()
                    .stream()
                    .map(e -> {
                        Holder h = new Holder();
                        Commit p = e.getKey();
                        h.parent = p;
                        h.pathO1 = longest(p.pathToChild(o1)).orElse(List.of());
                        h.pathO2 = longest(e.getValue()).orElse(List.of());
                        return h;
                    })
                    .sorted(Comparator.comparingInt((Holder h) -> dist(h.pathO1) + dist(h.pathO2))
                            .reversed())
                    .toList();

            for (Holder parent : commonParentHolders) {
                int distance1 = dist(parent.pathO1);
                int distance2 = dist(parent.pathO2);
                int compare = Integer.compare(distance1, distance2);
                if (compare != 0) {
                    return compare;
                }
            }
        }
        return -1 * Integer.compare(diagram.branchIndex(o1), diagram.branchIndex(o2));
    }

    private Optional<List<Commit>> longest(List<List<Commit>> p) {
        return p.stream()
                .sorted(Comparator.comparingInt(this::dist)
                        .reversed())
                .findFirst();
    }

    private int dist(List<Commit> path) {
        if (path.size() < 2) {
            return 0;
        }
        int result = 0;
        for (int i = 1; i < path.size(); i++) {
            Commit parent = path.get(i - 1);
            Commit child = path.get(i);
            result = result + distParentAndChild(parent, child);
        }
        return result;
    }

    private int distParentAndChild(Commit parent, Commit child) {
        List<Commit> children = parent.getChildren()
                .stream()
                .filter(c -> c.getBranch() != parent.getBranch())
                .sorted(Comparator.comparingInt(diagram::branchIndex)
                        .reversed())
                .toList();
        if (parent.getBranch() == child.getBranch()) {
            return 1 + children.size();
        }
        return 1 + children.indexOf(child);
    }

    private static class Holder {
        private Commit parent;
        private List<Commit> pathO1;
        private List<Commit> pathO2;
    }
}
