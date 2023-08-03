package view;

import org.apache.commons.cli.*;

public class CLI {
    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", "help", false, "print help");
        options.addOption("v", "version", false, "print version");
        options.addOption(Option.builder("d")
                .longOpt("directory")
                .hasArg()
                .argName("directory")
                .desc("directory to scan")
                .required()
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar <jar file>", options);
        } else if (cmd.hasOption("v")) {
            System.out.println("Version 1.2.0");
        }
    }
}
