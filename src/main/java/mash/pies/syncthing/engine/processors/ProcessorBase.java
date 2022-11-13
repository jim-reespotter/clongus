package mash.pies.syncthing.engine.processors;

/**
 * Base class for most processor components
 */
public abstract class ProcessorBase {

    private String name;
    private String description;

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public String toString() {return getName();}
}
