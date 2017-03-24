package nz.govt.natlib.ndha;


import java.sql.SQLException;

/**
 * Created by Developer on 20/09/2016.
 */

public interface StoreSource {

    boolean warcExists(String name);

    String getWarc(String name);

    boolean addWarc(String name, String path);

    void startConnection(String method) throws Exception;

    void endConnection() throws Exception;

    boolean isConnectionAlive();
}
