package mash.pies.syncthing.engine.processors.util;

public enum UpdateAction {
    OVERWRITE,  // replace all existing values
    APPEND,     // replace existing value if single valued?
    REMOVE,     // remove specific value(s)
    CLEAR       // remove all values
}
