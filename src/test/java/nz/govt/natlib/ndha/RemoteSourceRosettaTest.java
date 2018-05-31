package nz.govt.natlib.ndha;

import com.exlibris.dps.DeliveryAccessWS;
import com.exlibris.dps.DeliveryAccessWS_Service;
import com.exlibris.dps.Exception_Exception;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;


import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by Developer on 14/05/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RemoteSourceRosettaImpl.class)
public class RemoteSourceRosettaTest {

    protected static Log log = LogFactory.getLog(RemoteSourceRosettaTest.class);
    private String VALID_WARC_PATH = null;
    private String VALID_WARC_PID = null;
    private String VALID_WARC_NAME = null;
    private Map<String, String> VALID_WARC_PATHS = new HashMap<>();
    private RemoteSourceRosettaImpl mock;



//    DeliveryAccessWS deliveryAccessSpy = null;

    @Before
    public void setUp() throws Exception {
        log.debug("Setting up HarvestAgentH3Test.");
//        hah3 = new HarvestAgentH3();
        VALID_WARC_NAME = "FL18894153.warc";
        VALID_WARC_PID = "FL18894153";
        VALID_WARC_PATH = "";
        VALID_WARC_PATHS.put("", "");

        mock = PowerMockito.spy(new RemoteSourceRosettaImpl());

    }

    @Test
    public void testValidLookup() {

        // stubbing getActiveH3Jobs method for spying
        try {
            PowerMockito.doReturn("Test XML").when(mock, "getIeMetsString", ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        Path warcPath = null;

        // Test lookup
        if(mock.lookup(VALID_WARC_NAME)){
            try {
                PowerMockito.verifyPrivate(mock).invoke("generateDPSSession", ArgumentMatchers.anyString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Test access restriction
            if(mock.accessAllowed()){

                // Test warc paths retrieval
                try {
                    warcPath = mock.getWarc(VALID_WARC_NAME);
                    assertEquals(warcPath.toString(), VALID_WARC_PATH);
                } catch (Exception e) {
                    fail(e.getMessage());
                }

                Map<String, String> warcPaths = mock.getAllWarcs();
                assertNotNull(warcPaths);
                warcPaths.forEach((k,v)->assertTrue(VALID_WARC_PATHS.containsValue(v)));


            }
            else{
                fail("Test - accessAllowed failed for pid: " + VALID_WARC_NAME);
            }
        }
        else{
            fail("Test - lookup failed for pid: " + VALID_WARC_NAME);
        }


    }


}
