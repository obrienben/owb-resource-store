package nz.govt.natlib.ndha;


import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Developer on 20/09/2016.
 */

public interface RemoteSource {


    /**
     * Lookup warc filename in remote source
     * @param filename
     * @return true is lookup was successful
     */
    boolean lookup(String filename);

    /**
     * Check that Access Rights of this file allow it to be viewed
     * @return true is access is allowed
     */
    boolean accessAllowed();

    /**
     * Get warc filepath for warc.
     * Requires a lookup to have been successful and access is allowed.
     * @param filename
     * @return
     * @throws Exception
     */
    Path getWarc(String filename) throws Exception;

    /**
     * Returns a map of warc filenames and paths, which contains the searched for warc filename, and
     * any associated warcs required to view the web harvest the original warc belongs to.
     * @return
     * @throws Exception
     */
    Map<String,String> getAllWarcsInIE();
}
