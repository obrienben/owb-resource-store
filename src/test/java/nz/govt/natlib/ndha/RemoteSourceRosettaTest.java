package nz.govt.natlib.ndha;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by Ben O'Brien on 14/05/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RemoteSourceRosettaImpl.class)
public class RemoteSourceRosettaTest {

    private Path VALID_WARC_PATH = null;
    private String VALID_WARC_PID = null;
    private String VALID_WARC_NAME = null;
    private String INVALID_WARC_NAME = null;
    private List<String> VALID_WARC_PATHS = new ArrayList<>();
    private RemoteSourceRosettaImpl mock;



    @Before
    public void setUp() throws Exception {
        VALID_WARC_NAME = "FL18894153.warc";
        INVALID_WARC_NAME = "FL9999X999.warc";
        VALID_WARC_PID = "FL18894153";
        VALID_WARC_PATH = Paths.get("/server/storage/file/V1-FL18894153.warc");
        VALID_WARC_PATHS.add("/server/storage/file/V1-FL18894154.warc");
        VALID_WARC_PATHS.add("/server/storage/file/V1-FL18894153.warc");
        VALID_WARC_PATHS.add("/server/storage/file/V1-FL18894155.warc");

        mock = PowerMockito.spy(new RemoteSourceRosettaImpl());
    }

    /**
     * Test a valid lookup
     * Conditions:
     * - Mets XML is valid
     * - File PID exists
     * - Access is open
     * - Warc path exists for File PID
     * - All warc paths exist
     */
    @Test
    public void testValidOpenLookup() {

        try {
            StringBuilder ieMets = readInTestFile("mets_valid_open.xml");
            PowerMockito.doReturn(ieMets.toString()).when(mock, "getIeMetsString", ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test lookup
        assertTrue(mock.lookup(VALID_WARC_NAME));

        // Test access restriction
        assertTrue(mock.accessAllowed());

        // Test warc paths retrieval
        try {
            Path warcPath = mock.getWarc(VALID_WARC_NAME);
            assertEquals(warcPath, VALID_WARC_PATH);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Map<String, String> warcPaths = mock.getAllWarcsInIE();
        assertNotNull(warcPaths);
        warcPaths.forEach((k,v)->assertTrue(VALID_WARC_PATHS.contains(v)));
    }

    /**
     * Test a valid lookup that has restricted access
     * Conditions:
     * - Mets XML is valid
     * - File PID exists
     * - Access is restricted
     */
    @Test
    public void testValidRestrictedLookup() {

        try {
            StringBuilder ieMets = readInTestFile("mets_valid_restricted.xml");
            PowerMockito.doReturn(ieMets.toString()).when(mock, "getIeMetsString", ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test lookup
        assertTrue(mock.lookup(VALID_WARC_NAME));

        // Test access restriction
        assertFalse(mock.accessAllowed());
    }

    /**
     * Test a lookup with invalid XML
     * Conditions:
     * - Mets XML is invalid
     * - File PID not found
     */
    @Test
    public void testInvalidXmlLookup() {

        try {
            StringBuilder ieMets = readInTestFile("mets_invalid.xml");
            PowerMockito.doReturn(ieMets.toString()).when(mock, "getIeMetsString", ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test lookup
        assertFalse(mock.lookup(VALID_WARC_NAME));
    }

    /**
     * Test failure to get a warc path on a valid lookup
     * Conditions:
     * - Mets XML is valid
     * - File PID exists
     * - Access is open
     * - Warc path does not exists for File PID
     */
    @Test
    public void testInvalidGetWarc() {

        try {
            StringBuilder ieMets = readInTestFile("mets_valid_open.xml");
            PowerMockito.doReturn(ieMets.toString()).when(mock, "getIeMetsString", ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test lookup
        assertTrue(mock.lookup(VALID_WARC_NAME));

        // Test access restriction
        assertTrue(mock.accessAllowed());

        // Test warc path retrieval
        try {
            Path warcPath = mock.getWarc(INVALID_WARC_NAME);
            assertNull(warcPath);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test network error on a valid lookup
     * Conditions:
     * - Mets XML is valid
     * - File PID lookup fails
     */
    @Test
    public void testNetworkErrorLookup() {

        try {
            PowerMockito.doThrow(new MalformedURLException("Generic Network Error")).when(mock, "getIeMetsString", ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test lookup
        assertFalse(mock.lookup(VALID_WARC_NAME));
    }

    /**
     * Read file helper method
     * @param filename
     * @return StringBuilder with file contents
     * @throws URISyntaxException
     * @throws IOException
     */
    private StringBuilder readInTestFile(String filename) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
        StringBuilder data = new StringBuilder();
        Stream<String> lines = Files.lines(path);
        lines.forEach(line -> data.append(line).append("\n"));
        lines.close();
        return data;
    }

}
