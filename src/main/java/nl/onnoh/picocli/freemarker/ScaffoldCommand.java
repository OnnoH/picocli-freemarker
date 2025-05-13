package nl.onnoh.picocli.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "scaffold")
public class ScaffoldCommand implements Callable<Integer> {

    Logger logger = LoggerFactory.getLogger(ScaffoldCommand.class);

    private static final String RAT = "resource-allocation-table";
    private static final String TEMPLATE_FOLDER = "templates";
    private static final String TEMPLATE_SUFFIX = ".ftl";

    @CommandLine.Parameters(index = "0", arity = "1", description = "The name of the project to be scaffolded.")
    private String projectName;

    @Override
    public Integer call() throws Exception {
        logger.info("Scaffolding project {}", projectName);

        Code code = new Code();
        code.setVersion("0.0.1-SNAPSHOT");
        code.setGroupId("nl.onnoh");
        code.setArtifactId(projectName);

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_35);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File("/"));
        cfg.setClassForTemplateLoading(this.getClass(), "/");

        List<File> resourceFolderFiles = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream(File.separator + RAT))));
        while(bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            resourceFolderFiles.add(new File(line));
        }

        createFolder(projectName);
        resourceFolderFiles.stream()
                .filter(f -> f.getName().endsWith(TEMPLATE_SUFFIX))
                .forEach(file -> {
                    String outputFile = projectName + File.separator + file.getPath()
                            .replace(TEMPLATE_FOLDER + File.separator, "")
                            .replace(TEMPLATE_SUFFIX, "");
                    Template template = null;
                    Writer writer = null;
                    try {
                        template = cfg.getTemplate(file.getPath());
                        writer = new BufferedWriter(new FileWriter(outputFile));
                        template.process(code, writer);
                    } catch (TemplateException | IOException ex) {
                        logger.error("Failed to process template {} : {}", file.getPath(), ex.getMessage());
                    }
                });

        try (Git git = Git.init().setDirectory(new File(projectName)).call()) {
            logger.info("Initialized git repository at {}", projectName);
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Initial commit").call();
        } catch (GitAPIException e) {
            logger.error("Failed to create a new repository at {} : {}", projectName, e.getMessage());
        }

        return 0;
    }

    private void createFolder(String folderPath) {
        try {
            Path path = Paths.get(folderPath);
            Files.createDirectories(path);
            logger.info("Created directory {}", path);
        } catch (IOException e) {
            logger.error("Failed to create directory {}", folderPath);
        }
    }
}
