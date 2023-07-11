package mash.pies.syncthing.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import mash.pies.syncthing.engine.processors.LogBase;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;
import mash.pies.syncthing.engine.processors.task.TaskProcessor;
import mash.pies.syncthing.engine.processors.util.YamlFileReader;

/**
 * Sample application that reads YAML configs from passed parameters and runs them
 */
public class SyncRunner extends LogBase {

    public static void main(String[] args) throws Exception {
        new SyncRunner(args);
    }

    private boolean confirm = true;
    private boolean dryRun = true;

    SyncRunner(String[] args) throws Exception {   
        
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));

        if (argList.contains("--loglevel")) {
            String logLevel = argList.get(argList.indexOf("--loglevel")+1);
            argList.remove(logLevel);
            argList.remove("--loglevel");
            setDefaultLogLevel(logLevel);
        }

        if (argList.contains("--commit")) {
            dryRun = false;
            argList.remove("--commit");
            debug("--dryrun = " + dryRun + "; changes will "+(dryRun ? "not ":"")+"be committed");
        }

        if (!dryRun) {
            if (argList.contains("--no-confirm")) {
                confirm = false;
                argList.remove("--no-confirm");
            }
            debug("--no-confirm = " + !confirm + "; changes will "+(!confirm ? "not ":"")+"require user interaction");
        }

        debug("Config files to process: "+argList);
        List <FileInputStream> cFiles = new ArrayList<FileInputStream>();

        for (String fileName : argList) 
            cFiles.add(new FileInputStream(fileName));

        YamlFileReader configs = new YamlFileReader();
        configs.read(new SequenceInputStream(Collections.enumeration(cFiles)));

        debug("Tasks to run: "+configs.getRegistry().getTasks().keySet());
        for (String taskName : configs.getRegistry().getTasks().keySet()) {
            debug("Running task:" + taskName);
            TaskProcessor processor = configs.getRegistry().getTasks().get(taskName);
            Collection<ChangeCommand> changes = new HashSet<ChangeCommand>();
            changes.addAll(processor.createChanges());

            for (ChangeCommand c : changes) {
                if (dryRun)
                    info("Change: "+c+" skipped (dry run)");
                else
                    invokeChange(c); 
            }
            processor.closeConnections();
        }
    }

    private void invokeChange(ChangeCommand c) throws IOException {

        if (confirm) {
            System.out.println("Change: " + c.toString());
            System.out.println("Press 'y' to commit change, any other key to skip");

            String line = new Scanner(System.in).next();
            if (!line.toLowerCase().equals("y")) {
                info("User skipped change " + c);
                return;
            }
        }
            
        try {
            c.invoke();
            info("Change successful: "+c);
        }
        catch (Exception e) {
            warn("Change failed to commit: "+c);
        }
    }
}
