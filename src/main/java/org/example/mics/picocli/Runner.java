package org.example.mics.picocli;

import picocli.CommandLine;

public class Runner {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        CommandLine commandLine = new CommandLine(configuration);
        commandLine.parseArgs(args);

        if (configuration.isHelpRequested()) {
            commandLine.usage(System.out);
            return;
        }

        System.out.println("Database host:" + configuration.getDatabaseHost());
        System.out.println("Database port:" + configuration.getDatabasePort());
        System.out.println("Elasticsearch url: " + configuration.getElasticsearchUrl());
        System.out.println("Elasticsearch bulk size: " + configuration.getElasticsearchBulkSize());
        System.out.println("Run bulk inserts asynchronously: " + configuration.isAsync());
    }

}
