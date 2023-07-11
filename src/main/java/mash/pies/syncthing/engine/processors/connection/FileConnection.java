package mash.pies.syncthing.engine.processors.connection;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A connection to a local filesystem
 */
public class FileConnection extends Connection {

    private String path;

    public String getPath () {return path;}
    public void setPath (String path) {this.path = path;}
    
    public File getFile (String fileName) throws FileNotFoundException {

        File dir = new File (path);
        if (!dir.exists())
            throw new FileNotFoundException(getName() + " : Directory  " + path + " not found.");

        if (!dir.isDirectory())
            throw new FileNotFoundException(getName() + " : Path  " + path + " not a directory.");

        File f = new File(dir, fileName);//this.path.getAbsolutePath()+"/"+fileName);
        if (!f.exists())
            throw new FileNotFoundException("file  " + f.getPath() + " not found.");

        if (f.isDirectory())
            throw new FileNotFoundException("path  " + f.getPath() + " is a directory (expecting a file).");

        return f;
    }
}
