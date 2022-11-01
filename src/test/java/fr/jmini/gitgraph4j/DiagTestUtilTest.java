package fr.jmini.gitgraph4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

class DiagTestUtilTest {

    @Test
    void testGetAllDiagrams() throws Exception {
        List<String> expected = Arrays.stream(DiagTestUtil.class.getDeclaredMethods())
                .map(Method::getName)
                .filter(name -> name.matches("diag[0-9]+"))
                .sorted()
                .toList();

        List<Diagram> allDiagrams = DiagTestUtil.getAllDiagrams();
        assertThat(allDiagrams)
                .hasSize(expected.size());

        assertThat(allDiagrams)
                .extracting(Diagram::getFileName)
                .containsExactlyElementsOf(expected);
    }

    @Test
    void testCreateDiagramTestContent() throws Exception {
        Path file = Paths.get("src/test/java/fr/jmini/gitgraph4j/CreateDiagramTest.java");

        StringBuilder sb = new StringBuilder();
        sb.append("""
                package fr.jmini.gitgraph4j;

                import static org.assertj.core.api.Assertions.assertThat;

                import org.junit.jupiter.api.Test;

                class CreateDiagramTest extends AbstractTestCreateDiagram {
                """);
        for (Diagram d : DiagTestUtil.getAllDiagrams()) {
            String name = d.getFileName();
            sb.append("\n");
            sb.append("    @Test\n");
            sb.append("    void " + name + "() throws Exception {\n");
            sb.append("        Diagram d = DiagTestUtil." + name + "();\n");
            sb.append("        assertThat(d).isNotNull();\n");
            sb.append("        runConvert(d);\n");
            sb.append("    }\n");
        }
        sb.append("}\n");
        String expectedContent = sb.toString();
        String actualContent = Files.readString(file);
        if (!Objects.equals(actualContent, expectedContent)) {
            Files.writeString(file, expectedContent);
            fail("The content of the file `CreateDiagramTest.java` not not up-to-date with the diagrams in `DiagTestUtil`");
        }
    }

    @Test
    void testExamplesContent() throws Exception {
        Path file = Paths.get("src/docs/asciidoc/examples.adoc");

        StringBuilder sb = new StringBuilder();
        sb.append("""
                :rootdir: ../../..
                :github-readme: {rootdir}/README.adoc
                :revdate: {project-builddate}
                :revnumber: {project-version}

                include::{github-readme}[tags=vardef]

                == Examples
                Here some diagrams used to develop of the code:
                """);
        for (Diagram d : DiagTestUtil.getAllDiagrams()) {
            String name = d.getFileName();
            sb.append("\n");
            sb.append("=== " + name + "\n");
            sb.append("\n");
            sb.append("Diagrams.net:\n");
            sb.append("\n");
            sb.append("image:examples/" + name + "/graph.drawio.png[]\n");
            sb.append("\n");
            sb.append("git-link:src/docs/asciidoc/examples/" + name
                    + "/graph.drawio[view source file, view] - link:https://app.diagrams.net/?src=https://app.diagrams.net/?src=https://raw.githubusercontent.com/jmini/GitGraph4J/main/src/docs/asciidoc/examples/" + name
                    + "/graph.drawio#Uhttps%3A%2F%2Fraw.githubusercontent.com%2Fjmini%2FGitGraph4J%2Fmain%2Fsrc%2Fdocs%2Fasciidoc%2Fexamples%2F" + name + "%2Fgraph.drawio[edit file on diagrams.net]\n");
            sb.append("\n");
            sb.append("Mermaid:\n");
            sb.append("\n");
            sb.append("image:examples/" + name + "/graph.mmd.png[]\n");
            sb.append("\n");
            sb.append("git-link:src/docs/asciidoc/examples/" + name + "/graph.mmd[view source file, view]\n");
            sb.append("\n");
            sb.append("Dot (Graphviz):\n");
            sb.append("\n");
            sb.append("image:examples/" + name + "/graph.gv.png[]\n");
            sb.append("\n");
            sb.append("git-link:src/docs/asciidoc/examples/" + name + "/graph.gv[view source file, view]\n");
            sb.append("\n");
        }
        String expectedContent = sb.toString();
        String actualContent = Files.readString(file);
        if (!Objects.equals(actualContent, expectedContent)) {
            Files.writeString(file, expectedContent);
            fail("The content of the file `examples.adoc` not not up-to-date with the diagrams in `DiagTestUtil`");
        }
    }

}
