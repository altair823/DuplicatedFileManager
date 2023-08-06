package view;

import dto.DBSetup;
import dto.config.ConfigManager;
import dto.config.DatabaseConfig;
import dto.metadata.dir.DirMetadataDto;
import dto.metadata.file.FileMetadata;
import dto.metadata.file.FileMetadataDto;
import model.FileManager;
import model.hasher.Md5Hasher;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Cli {
    public static final String DEFAULT_DB_CONFIG_FILE_NAME = "dbConfig.json";

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", "help", false, "print help");
        options.addOption("v", "version", false, "print version");
        options.addOption(Option.builder("d")
                .longOpt("directory")
                .hasArg()
                .argName("directory")
                .desc("directory to scan")
                .build());
        options.addOption(Option.builder("c")
                .longOpt("config")
                .hasArg()
                .argName("config")
                .desc("database config file")
                .build());
        options.addOption("a", "scan all", false, "scan all files");
        options.addOption("u", "scan updated", false, "scan updated files");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Print help or version
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar <jar file>", options);
            endProgram();
        } else if (cmd.hasOption("v")) {
            System.out.println("Version 1.2.0");
            endProgram();
        }
        ConfigManager configManager = new ConfigManager();

        // Load database config
        if (cmd.hasOption("c")) {
            loadDBConfig(configManager, cmd.getOptionValue("c"));
        } else {
            loadDBConfig(configManager, DEFAULT_DB_CONFIG_FILE_NAME);
        }

        // Load last run timestamp
        loadLastRunTimestamp(configManager);

        // set directory to scan
        String rootDir = null;
        if (cmd.hasOption("d") && cmd.getOptionValue("d") != null) {
            rootDir = cmd.getOptionValue("d");
        } else {
            System.err.println("Please specify directory to scan.");
            endProgram();
        }

        // Connect to database
        DBSetup dbSetup = new DBSetup(configManager.getDatabaseConfig());
        Connection connection;
        try {
            connection = dbSetup.getConnection();
        } catch (SQLException e) {
            System.err.println("Cannot connect to database.");
            throw new RuntimeException(e);
        }
        FileManager fileManager = new FileManager(configManager, new Md5Hasher());

        // Scan files
        if (cmd.hasOption("a")) {
            fileManager.updateAll(rootDir, new DirMetadataDto(connection), new FileMetadataDto(connection));
        } else if (cmd.hasOption("u")) {
            fileManager.updateModifiedContent(rootDir, new DirMetadataDto(connection), new FileMetadataDto(connection));
            List<FileMetadata> result = fileManager.getDuplicateFiles();
            System.out.println("Do you want to list all duplicated files? [Y/n]:");
            String answer = System.console().readLine();
            if (answer.equals("Y") || answer.equals("y") || answer.isEmpty()) {
                System.out.println("Duplicated files:");
                for (FileMetadata fileMetadata : result) {
                    System.out.println(fileMetadata);
                }
            }
            System.out.println("Duplicated files count: " + result.size());

        } else {
            System.err.println("Please specify scan mode.");
            endProgram();
        }
    }

    private static void loadLastRunTimestamp(ConfigManager configManager) {
        try {
            configManager.loadLastRunTimestamp();
        } catch (IOException e) {
            System.err.println("Cannot load timestamp file.\n" +
                    "Create new timestamp file in default location.");
            try {
                ConfigManager.saveLastRunTimestamp(ConfigManager.createCurrentTimestamp());
            } catch (IOException ex) {
                System.err.println("Cannot create new timestamp file in default location.");
                throw new RuntimeException(ex);
            }
        }
    }

    private static void loadDBConfig(ConfigManager configManager, String defaultDbConfigFileName) {
        try {
            configManager.loadDatabaseConfig(defaultDbConfigFileName);
        } catch (IOException e) {
            try {
                System.err.println("Cannot load database config file.\n" +
                        "Create new database config file in default location.");
                DatabaseConfig defaultConfig = new DatabaseConfig();
                configManager.setDatabaseConfig(defaultConfig);
                configManager.saveDatabaseConfig(DEFAULT_DB_CONFIG_FILE_NAME);
                System.out.println("Example database config file created in default location.\n"
                        + "Please edit the file and run the program again.");
                endProgram();
            } catch (IOException ex) {
                System.err.println("Cannot create new database config file in default location.");
                throw new RuntimeException(ex);
            }
        }
    }

    private static void endProgram() {
        System.exit(0);
    }
}
