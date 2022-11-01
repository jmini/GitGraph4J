package fr.jmini.gitgraph4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;

abstract class AbstractTestCreateDiagram {
    private static final String GRAPH_DRAWIO_FIlE_NAME = "graph.drawio";
    private static final String GRAPH_DRAWIO_PNG_FILE_NAME = "graph.drawio.png";
    private static final String GRAPH_MERMAID_FIlE_NAME = "graph.mmd";
    private static final String GRAPH_MERMAID_PNG_FILE_NAME = "graph.mmd.png";
    private static final String GRAPH_DOT_FIlE_NAME = "graph.gv";
    private static final String GRAPH_DOT_PNG_FILE_NAME = "graph.gv.png";

    protected void runConvert(Diagram d) throws Exception {
        Path folder = Paths.get("src/docs/asciidoc/examples")
                .resolve(d.getFileName());
        Files.createDirectories(folder);

        Path drawIoFile = folder.resolve(GRAPH_DRAWIO_FIlE_NAME);
        // tag::storeFile[]
        // convert to file
        String drawIoXmlContent = CreateDiagram.convertToDrawIoXml(d);
        Files.write(drawIoFile, drawIoXmlContent.getBytes(StandardCharsets.UTF_8));
        // end::storeFile[]
        Assertions.assertThat(drawIoXmlContent)
                .isNotNull();

        String mermaid = CreateDiagram.convertToMermaid(d);
        Assertions.assertThat(mermaid)
                .isNotNull();
        Path mermaidFile = folder.resolve(GRAPH_MERMAID_FIlE_NAME);
        Files.write(mermaidFile, mermaid.getBytes(StandardCharsets.UTF_8));

        String plantUml = CreateDiagram.convertToDot(d);
        Assertions.assertThat(plantUml)
                .isNotNull();
        Path plantUmlFile = folder.resolve(GRAPH_DOT_FIlE_NAME);
        Files.write(plantUmlFile, plantUml.getBytes(StandardCharsets.UTF_8));

        ConvertUtil.runConfiguredDrawIoConvertToPngCommand(folder, GRAPH_DRAWIO_PNG_FILE_NAME, GRAPH_DRAWIO_FIlE_NAME);
        ConvertUtil.runConfiguredMermaidConvertToPngCommand(folder, GRAPH_MERMAID_PNG_FILE_NAME, GRAPH_MERMAID_FIlE_NAME);
        ConvertUtil.runConfiguredDotConvertToPngCommand(folder, GRAPH_DOT_PNG_FILE_NAME, GRAPH_DOT_FIlE_NAME);
    }
}
