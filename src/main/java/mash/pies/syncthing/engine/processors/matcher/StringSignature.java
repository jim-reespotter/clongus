package mash.pies.syncthing.engine.processors.matcher;

public class StringSignature implements Signature {

    private String signature;

    StringSignature (String signature) {
        this.signature = signature;
    }

    @Override
    public String getSignature() {
        return signature;
    }
    
    @Override
    public String toString() {
        return signature;
    }

    @Override
    public boolean equals(Object sig) {
        if (sig instanceof StringSignature && ((Signature)sig).getSignature().toString().equals(signature))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }
}
