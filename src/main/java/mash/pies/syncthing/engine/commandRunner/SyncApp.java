package mash.pies.syncthing.engine.commandRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Scanner;

import mash.pies.syncthing.engine.TaskRunner;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;

/**
 * Sample application that reads YAML configs from passed parameters and runs them
 */
public class SyncApp {

    public static void main(String[] args) throws Exception {
        new SyncApp(args);
    }

    private boolean confirm = true;
    private boolean dryRun = true;

    SyncApp(String[] args) throws Exception {   
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        
        if (argList.contains("--commit")) {
            dryRun = false;
            argList.remove("--commit");
        }

        if (argList.contains("--no-confirm")) {
            confirm = false;
            dryRun = false;
            argList.remove("--no-confirm");
        }

        for (String fileName : argList) {
            YamlFileReader configs = new YamlFileReader();
            configs.readFile(new File(fileName));

            for (String taskName : configs.getRegistry().getTasks().keySet()) {
                TaskRunner runner = new TaskRunner(configs.getRegistry().getTasks().get(taskName));
                Collection<ChangeCommand> changes = runner.createChanges();

                for (ChangeCommand c : changes) {
                    System.out.println (c);
                    if (dryRun)
                        System.out.println ("Dry run - no action taken");
                    else
                        invokeChange(c); 
                }
            }
        }
    }

    private void invokeChange(ChangeCommand c) throws IOException {

        if (confirm) {
            System.out.println("Press 'y' to commit change, any other key to skip");

            Scanner input = new Scanner(System.in);
            String line = input.next();
            if (!line.toLowerCase().equals("y")) {
                System.out.println ("...skipped");
                return;
            }
        }
            
        try {
            c.invoke();
            System.out.println("Success");
        }
        catch (Exception e) {
            System.out.println("Failed");
        }

    }
}
