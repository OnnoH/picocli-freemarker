package nl.onnoh.picocli.freemarker;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "baton"
        , description = "CLI for Camunda Conductors"
        , version = "1.0"
        , subcommands = {
        ScaffoldCommand.class
}
)
class Main {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays a help message.")
    private boolean helpRequested = false;

    @Option(names = {"-v", "--verbose"}, description = "Verbose output.")
    private boolean verbose = false;

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

}
