package fr.jmini.gitgraph4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Commit {
    private Shape shape = new Shape();

    private String label;
    private Map<Commit, LineStyle> parents = new LinkedHashMap<>();
    private Set<Commit> children = new LinkedHashSet<>();
    private Branch branch;

    private Color color;

    private String internalName;

    public Commit parent(Commit commit) {
        addParent(commit);
        return this;
    }

    public Commit parent(Commit commit, LineStyle style) {
        addParent(commit, style);
        return this;
    }

    public void addParent(Commit commit) {
        addParent(commit, new LineStyle());
    }

    private void addParent(Commit commit, LineStyle style) {
        this.parents.put(commit, style);
        commit.children.add(this);
    }

    public Commit(String name) {
        this(name, name, null);
    }

    public Commit(String name, Color color) {
        this(name, name, color);
    }

    public Commit(String internalName, String name) {
        this(internalName, name, null);
    }

    public Commit(String internalName, String label, Color color) {
        this.internalName = internalName;
        this.label = label;
        this.color = color;
    }

    public boolean isParentOf(Commit other) {
        if (isDirectParentOf(other)) {
            return true;
        }
        for (Commit c : children) {
            if (c.isParentOf(other)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDirectParentOf(Commit other) {
        return children.contains(other);
    }

    public List<List<Commit>> pathToChild(Commit child) {
        if (children.contains(child)) {
            return List.of(List.of(this, child));
        }
        List<List<Commit>> result = new ArrayList<>();
        for (Commit c : children) {
            if (c.isParentOf(child)) {
                List<List<Commit>> r = c.pathToChild(child);
                for (List<Commit> e : r) {
                    ArrayList<Commit> newPath = new ArrayList<>();
                    newPath.add(this);
                    newPath.addAll(e);
                    result.add(Collections.unmodifiableList(newPath));
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    public Optional<List<Commit>> shortestPathToChild(Commit child) {
        return shortest(pathToChild(child));
    }

    private Optional<List<Commit>> shortest(List<List<Commit>> p) {
        return p.stream()
                .sorted(Comparator.comparingInt(List<Commit>::size))
                .findFirst();
    }

    public boolean isChildOf(Commit other) {
        if (isDirectChildOf(other)) {
            return true;
        }
        for (Commit c : parents.keySet()) {
            if (c.isChildOf(other)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDirectChildOf(Commit other) {
        return parents.containsKey(other);
    }

    public Optional<Commit> commonDirectParent(Commit other) {
        return parents.keySet()
                .stream()
                .filter(c -> other.parents.containsKey(c))
                .findAny();
    }

    public Set<Commit> commonParents(Commit other) {
        return commonParentsWithPath(other).keySet();
    }

    public Map<Commit, List<List<Commit>>> commonParentsWithPath(Commit other) {
        if (equals(other)) {
            return Map.of(this, List.of());
        }
        if (isParentOf((other))) {
            return Map.of(this, pathToChild(other));
        }
        if (isChildOf(other)) {
            return Map.of(other, List.of());
        }
        return getAllParents().stream()
                .flatMap(c -> c.pathToChild(other)
                        .stream())
                .collect(Collectors.groupingBy((List<Commit> l) -> l.get(0)));
    }

    public Set<Commit> getParents() {
        return parents.keySet();
    }

    public Set<Commit> getAllParents() {
        Set<Commit> result = new LinkedHashSet<>();
        result.addAll(getParents());
        for (Commit p : getParents()) {
            result.addAll(p.getAllParents());
        }
        return result;
    }

    public Set<Map.Entry<Commit, LineStyle>> getParentsWithStyle() {
        return parents.entrySet();
    }

    public Set<Commit> getChildren() {
        return children;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getLabel() {
        return label;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        if (branch == null) {
            throw new IllegalArgumentException("Branch can't be null");
        }
        if (this.branch != null) {
            throw new IllegalStateException("Branch is already set");
        }
        this.branch = branch;
        if (!branch.commits.contains(this)) {
            branch.commits.add(this);
        }
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Commit [" + internalName + "]";
    }
}
