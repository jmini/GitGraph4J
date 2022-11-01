package fr.jmini.gitgraph4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.jmini.gitgraph4j.CreateDiagram.Counter;

public class Diagram {
    private Counter counter = new Counter();

    private String fileName;

    public List<Branch> branches = new ArrayList<>();
    public List<Commit> commits = new ArrayList<>();

    public int branchBoxWidth = 80;
    public int branchBoxMargin = 0;

    public Diagram(String fileName) {
        this.fileName = fileName;
    }

    public List<Commit> allCommits() {
        List<Commit> result = new ArrayList<>();
        result.addAll(commits);

        branches.stream()
                .flatMap(b -> b.commits.stream())
                .filter(c -> !result.contains(c))
                .forEach(result::add);

        Set<String> existingNames = new HashSet<>();
        for (Commit c : result) {
            if (c.getInternalName() != null && !c.getInternalName()
                    .isBlank()) {
                if (existingNames.contains(c.getInternalName())) {
                    throw new IllegalStateException("Same internal name '" + c.getInternalName() + "' for multiple commits.");
                }
                existingNames.add(c.getInternalName());
            }
        }
        for (Commit c : result) {
            if (c.getInternalName() == null || c.getInternalName()
                    .isBlank()) {
                c.setInternalName("c" + counter.get());
                existingNames.add(c.getInternalName());
            }
        }

        for (Commit c : result) {
            c.getParents()
                    .stream()
                    .filter(e -> e.getBranch() == null)
                    .findAny()
                    .ifPresent(e -> {
                        throw new IllegalStateException("Branch is missing in (parent) commit: " + e);
                    });
            c.getChildren()
                    .stream()
                    .filter(e -> e.getBranch() == null)
                    .findAny()
                    .ifPresent(e -> {
                        throw new IllegalStateException("Branch is missing in (child) commit: " + e);
                    });

            c.getParents()
                    .stream()
                    .filter(e -> !result.contains(e))
                    .findAny()
                    .ifPresent(e -> {
                        throw new IllegalStateException("Result does not contain (parent) commit: " + e);
                    });
            c.getChildren()
                    .stream()
                    .filter(e -> !result.contains(e))
                    .findAny()
                    .ifPresent(e -> {
                        throw new IllegalStateException("Result does not contain (child) commit: " + e);
                    });
        }

        return Collections.unmodifiableList(result);
    }

    public List<Commit> allOrderedCommits() {
        List<Commit> allCommits = allCommits();
        if (commits.size() == allCommits.size()) {
            return List.copyOf(commits);
        } else if (!commits.isEmpty()) {
            System.err.println("There are more commits in the diagram than what was manually added, using the sort algorism to dertermine the position");
        }
        return allCommits().stream()
                .sorted(new CommitComparator(this))
                .toList();
    }

    public int branchIndex(Commit c) {
        return branches.indexOf(c.getBranch());
    }

    public void addCommit(Commit commit) {
        commits.add(commit);
    }

    public void addCommit(Commit commit, Branch branch) {
        addCommit(commit);
        if (!branch.commits.contains(commit)) {
            branch.addCommit(commit);
        }
        if (!branches.contains(branch)) {
            branches.add(branch);
        }
    }

    public void addBranch(Branch b) {
        branches.add(b);
    }

    public String getFileName() {
        return fileName;
    }
}