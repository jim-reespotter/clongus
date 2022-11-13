package mash.pies.syncthing.engine.processors.matcher;

import mash.pies.syncthing.engine.processors.Entity;

public interface SignatureGenerator {
    
    Signature getSignature (Entity e);
}
