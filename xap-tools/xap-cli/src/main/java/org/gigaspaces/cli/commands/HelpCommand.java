package org.gigaspaces.cli.commands;

import org.gigaspaces.cli.CliCommand;
import org.gigaspaces.cli.CliExecutor;
import picocli.CommandLine.*;

@Command(name="help", header = "display this help message")
public class HelpCommand extends CliCommand {
    @ParentCommand
    private Object parentCommand;

    @Override
    protected void execute() throws Exception {
        CliExecutor.toCommandLine(parentCommand).usage(System.out);
    }
}