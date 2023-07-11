package mash.pies.syncthing.engine.processors.query;

import java.util.Map;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;

/**
 * Query that reads from one query, writes to another
 */
public class ReadWriteQuery extends Query {

    private Query read;
    private Query write;

    public Query getRead() {return read;}
    public void setRead(Query read) {this.read = read;}
    public Query getWrite() {return write;}
    public void setWrite(Query write) {this.write = write;}


    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {
        return read.read(params);
    }

    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {        
        return write.getChangeCommandGenerator(params);
    }

    @Override
    public void close() {
        read.close();
        write.close();
    }
}
