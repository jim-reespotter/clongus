package mash.pies.syncthing.engine.processors.filter;

import mash.pies.syncthing.engine.processors.ProcessorBase;

public class FilterRule extends ProcessorBase {

    private String[] actions;  // should this be enum?

    public String [] getActions() {return actions;}
    public void setActions(String[] actions) {this.actions = actions;}
}
