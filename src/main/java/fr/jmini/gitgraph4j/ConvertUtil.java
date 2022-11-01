package fr.jmini.gitgraph4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.Deflater;

public class ConvertUtil {

    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String DRAW_IO_CMD_KEY = "draw.io.cmd";
    private static final String MERMAID_CMD_KEY = "mermaid.cmd";
    private static final String DOT_CMD_KEY = "dot.cmd";

    private ConvertUtil() {
    }

    public static void runConfiguredDrawIoConvertToPngCommand(Path workingDirectory, String outputPngFileName, String inputDrawIoFileName) throws IOException, InterruptedException {
        Optional<String> drawIoCommand = findCommand(DRAW_IO_CMD_KEY);
        if (drawIoCommand.isPresent()) {
            runDrawIoConvertToPngCommand(drawIoCommand.get(), workingDirectory, outputPngFileName, inputDrawIoFileName);
        } else {
            System.err.println("File '" + CONFIG_FILE_NAME + "' is missing, conversion of drawio file to png is not possible");
            Files.deleteIfExists(workingDirectory.resolve(outputPngFileName));
        }
    }

    public static void runDrawIoConvertToPngCommand(String drawIoCommand, Path workingDirectory, String outputPngFileName, String inputDrawIoFileName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(drawIoCommand, "-x", "-b", "10", "-f", "png", "-o", outputPngFileName, inputDrawIoFileName);
        runCommand(workingDirectory, pb);
    }

    public static void runConfiguredMermaidConvertToPngCommand(Path workingDirectory, String outputPngFileName, String inputMermaidFileName) throws IOException, InterruptedException {
        Optional<String> findCommand = findCommand(MERMAID_CMD_KEY);
        if (findCommand.isPresent()) {
            String command = findCommand.get();
            if (command.startsWith("kroki")) {
                runKroki(workingDirectory, KrokiFormat.MERMAID, outputPngFileName, inputMermaidFileName);
            } else {
                runMermaidConvertToPngCommand(command, workingDirectory, outputPngFileName, inputMermaidFileName);
            }
        } else {
            System.err.println("File '" + CONFIG_FILE_NAME + "' is missing, conversion of mermaid file to png is not possible");
            Files.deleteIfExists(workingDirectory.resolve(outputPngFileName));
        }
    }

    public static void runMermaidConvertToPngCommand(String mermaidCommand, Path workingDirectory, String outputPngFileName, String inputMermaidFileName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(mermaidCommand, "-i", inputMermaidFileName, "-o", outputPngFileName);
        runCommand(workingDirectory, pb);
        Path krokiCachefile = getKrokiCacheFile(workingDirectory, outputPngFileName);
        Files.deleteIfExists(krokiCachefile);
    }

    public static void runConfiguredDotConvertToPngCommand(Path workingDirectory, String outputPngFileName, String inputDotFileName) throws IOException, InterruptedException {
        Optional<String> findCommand = findCommand(DOT_CMD_KEY);
        if (findCommand.isPresent()) {
            String command = findCommand.get();
            if (command.startsWith("kroki")) {
                runKroki(workingDirectory, KrokiFormat.PLANTUML, outputPngFileName, inputDotFileName);
            } else {
                runDotConvertToPngCommand(command, workingDirectory, outputPngFileName, inputDotFileName);
            }
        } else {
            System.err.println("File '" + CONFIG_FILE_NAME + "' is missing, conversion of dot file to png is not possible");
            Files.deleteIfExists(workingDirectory.resolve(outputPngFileName));
        }
    }

    public static void runDotConvertToPngCommand(String dotCommand, Path workingDirectory, String outputPngFileName, String inputDotFileName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(dotCommand, "-T", "png", "-o", outputPngFileName, inputDotFileName);
        runCommand(workingDirectory, pb);
        Path krokiCachefile = getKrokiCacheFile(workingDirectory, outputPngFileName);
        Files.deleteIfExists(krokiCachefile);
    }

    private static void runKroki(Path workingDirectory, KrokiFormat format, String outputPngFileName, String inputFileName) throws IOException {
        String content = Files.readString(workingDirectory.resolve(inputFileName));
        String formatName = format.name()
                .toLowerCase();
        String u = "https://kroki.io/" + formatName + "/png/" + encode(content);
        Path krokiCachefile = getKrokiCacheFile(workingDirectory, outputPngFileName);
        boolean fetchFromKroki;
        if (Files.exists(krokiCachefile)) {
            String krokiCacheContent = Files.readString(krokiCachefile);
            fetchFromKroki = !Objects.equals(u, krokiCacheContent);
        } else {
            fetchFromKroki = true;
        }
        if (fetchFromKroki) {
            URL url = new URL(u);
            try (InputStream in = new BufferedInputStream(url.openStream());
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(workingDirectory.resolve(outputPngFileName)
                            .toFile()))) {
                for (int i; (i = in.read()) != -1;) {
                    out.write(i);
                }
            }
        }
        Files.writeString(krokiCachefile, u);
    }

    private static Path getKrokiCacheFile(Path workingDirectory, String outputPngFileName) {
        return workingDirectory.resolve(outputPngFileName + ".kroki");
    }

    public static String encode(String decoded) throws IOException {
        return Base64.getUrlEncoder()
                .encodeToString(compress(decoded.getBytes()));
    }

    private static byte[] compress(byte[] source) throws IOException {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(source);
        deflater.finish();

        byte[] buffer = new byte[2048];
        int compressedLength = deflater.deflate(buffer);
        byte[] result = new byte[compressedLength];
        System.arraycopy(buffer, 0, result, 0, compressedLength);
        return result;
    }

    private static Optional<String> findCommand(String commandKey) throws IOException {
        Path configFile = Paths.get(CONFIG_FILE_NAME);
        if (Files.isRegularFile(configFile)) {
            Properties properties = configProperties(configFile);
            String command = properties.getProperty(commandKey);
            if (command == null) {
                throw new IllegalStateException("File '" + CONFIG_FILE_NAME + "' should contains a '" + commandKey + "' entry");
            }
            return Optional.of(command);
        }
        return Optional.empty();
    }

    private static void runCommand(Path workingDirectory, ProcessBuilder pb) throws IOException, InterruptedException {
        pb.directory(workingDirectory.toAbsolutePath()
                .toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int code = p.waitFor();
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = bri.readLine()) != null) {
            System.out.println(line);
        }
        if (code != 0) {
            throw new IllegalStateException("Convert to png exit with a non 0 status code");
        }
    }

    public static Properties configProperties(Path configFile) throws IOException {
        try (InputStream is = new FileInputStream(configFile.toFile())) {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        }
    }
}
