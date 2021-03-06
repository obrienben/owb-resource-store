package nz.govt.natlib.ndha;


import java.sql.SQLException;

/**
 * Created by Developer on 20/09/2016.
 */

public interface StoreSource {

    boolean warcExists(String name);

    String getWarc(String name, Boolean useResourceStorePool) throws Exception;

    boolean addWarc(String name, String path) throws Exception;

    void startConnection(boolean flag) throws Exception;

    void endConnection() throws Exception;

    boolean isConnectionAlive() throws Exception;

}
