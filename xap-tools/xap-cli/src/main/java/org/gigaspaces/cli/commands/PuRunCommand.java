package org.gigaspaces.cli.commands;

import com.gigaspaces.CommonSystemProperties;
import com.gigaspaces.start.SystemInfo;
import org.gigaspaces.cli.JavaCommandBuilder;
import org.gigaspaces.cli.commands.utils.XapCliUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.*;

/**
 * @since 12.3
 * @author Rotem Herzberg
 */
@Command(name="run", header = "Run a standalone Processing Unit")
public class PuRunCommand extends AbstractRunCommand {

    @Parameters(index = "0", description = "Relative/absolute path of a Processing Unit directory or archive file")
    File path;
    @Option(names = {"--partitions" }, description = "Specify the number of partitions for the Processing Unit")
    int partitions;
    @Option(names = {"--ha" }, description = "High availability (add one backup per partition)")
    boolean ha;
    @Option(names = {"--instances" }, split = ",", description = "Specify one or more instances to run (for example: --instances=1_1,1_2). "
                                                                    + "If no instances are specified, runs all instances.")
    String[] instances;
    @Option(names = {"--lus"}, description = "Start a lookup service")
    boolean lus;

    @Override
    protected void execute() throws Exception {

        validateOptions(partitions, ha, instances);

        final List<ProcessBuilder> processBuilders = new ArrayList<ProcessBuilder>();
        if (lus) {
            processBuilders.add(buildStartLookupServiceCommand());
        }
        if (partitions == 0) {
            processBuilders.add(buildPuCommand(0, false));
        } else {
            for (int id = 1; id < partitions+1; id++) {
                if (instances == null) {
                    processBuilders.add(buildPuCommand(id, false));
                    if (ha) {
                        processBuilders.add(buildPuCommand(id, true));
                    }
                } else {
                    if (containsInstance(instances, id + "_" + 1)) {
                        processBuilders.add(buildPuCommand(id, false));
                    }
                    if (containsInstance(instances, id + "_" + 2)) {
                        processBuilders.add(buildPuCommand(id, true));
                    }
                }
            }
        }
        XapCliUtils.executeProcesses(processBuilders);
    }

    private ProcessBuilder buildPuCommand(int id, boolean backup) {
        final JavaCommandBuilder command = new JavaCommandBuilder()
                .systemProperty(CommonSystemProperties.START_EMBEDDED_LOOKUP, "false")
                .optionsFromEnv("XAP_OPTIONS");
        command.classpathFromEnv("PRE_CLASSPATH");
        appendGsClasspath(command);
        command.classpathFromEnv("SPRING_JARS", () -> {
            //SPRING_JARS="%XAP_HOME%\lib\optional\spring\*:%XAP_HOME%\lib\optional\security\*"
            SystemInfo.XapLocations locations = SystemInfo.singleton().locations();
            command.classpath(locations.getLibOptional() + File.separator + "spring" + File.separator + "*");
            command.classpath(locations.getLibOptional() + File.separator + "security" + File.separator + "*");
        });
        command.classpathFromEnv("POST_CLASSPATH");
        command.mainClass("org.openspaces.pu.container.standalone.StandaloneProcessingUnitContainer");
        command.arg("-path").arg(path.getPath());
        if (id != 0) {
            command.arg("-cluster")
                    .arg("schema=partitioned")
                    .arg("total_members=" + partitions + "," + (ha ? "1" : "0"))
                    .arg("id=" + id)
                    .arg(backup ? "backup_id=1" : "");
        }

        return toProcessBuilder(command, "processing unit");
    }
}
