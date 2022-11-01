package fr.jmini.gitgraph4j;

import java.util.ArrayList;
import java.util.List;

public class Branch {
    public int branchY;
    public String name;
    public List<Commit> commits = new ArrayList<>();
    public Color color;
    private Boolean openToEnd;

    public Branch(String name, Color color) {
        super();
        this.name = name;
        this.color = color;
    }

    public Branch withOpenToEnd(boolean openToEndValue) {
        this.openToEnd = openToEndValue;
        return this;
    }

    public void addCommit(Commit c) {
        commits.add(c);
        c.setBranch(this);
    }

    public boolean isOpenToEnd() {
        if (openToEnd == null) {
            return commits.get(commits.size() - 1)
                    .getChildren()
                    .isEmpty();
        }
        return openToEnd.booleanValue();
    }

    public Shape box = new Shape();

}