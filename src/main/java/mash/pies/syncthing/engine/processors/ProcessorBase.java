package mash.pies.syncthing.engine.processors;

/**
 * Base class for most processor components
 */
public abstract class ProcessorBase extends LogBase {

    private String name;
    private String description;

    public String getName() {
        if (name != null)
            return this.name;
        else 
            return "<NO NAME>";
    }
    public void setName(String name) {this.name = name;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    /************Logging ***********/

    public void trace(String msg) {super.trace(getName()+": "+msg);}
    public void debug(String msg) {super.debug(getName()+": "+msg);}
}
