package nz.govt.natlib.ndha;

import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Developer on 15/12/2017.
 */
public class RemoteSourceRosettaImpl implements RemoteSource {


    @Override
    public boolean lookup(String filename) {
        return false;
    }

    @Override
    public boolean accessAllowed() {
        return false;
    }

    @Override
    public Path getWarc(String name) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> getAllWarcs(String filename) {
        return null;
    }

}
