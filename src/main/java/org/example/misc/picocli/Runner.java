package org.example.misc.picocli;

import picocli.CommandLine;

public class Runner {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        CommandLine commandLine = new CommandLine(configuration);
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
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

        switch (configuration.getDataType()) {
            case VENTE:
                System.out.println("Lancer l'import des VENTES");
                break;
            case CONTACT:
                System.out.println("Lancer l'import des CONTACT");
                break;
            case INFORMATION:
                System.out.println("Lancer l'import des INFORMATION");
                break;
            case LINEAIRE:
                System.out.println("Lancer l'import des LINEAIRE");
                break;
            case MOBILIER:
                System.out.println("Lancer l'import des MOBILIER");
                break;
        }
    }

}
