package mash.pies.syncthing.engine.processors.matcher;

public interface Signature {
    
    Object getSignature (); 

    public boolean equals(Object sig);
}
