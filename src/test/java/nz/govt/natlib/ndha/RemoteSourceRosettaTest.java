package nz.govt.natlib.ndha;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Developer on 14/05/2018.
 */
public class RemoteSourceRosettaTest {

    protected static Log log = LogFactory.getLog(HarvestAgentH3Test.class);
    HarvestAgentH3 hah3;

    @Before
    public void setUp() throws Exception {
        log.debug("Setting up HarvestAgentH3Test.");
        hah3 = new HarvestAgentH3();
    }

    @Test
    public void testRecoverHarvests() {

        HarvestAgentH3 hah3Spy = spy(hah3);
        mock(HarvesterH3.class);

        // Define test Core Jobs
        List<String> coreJobs = new ArrayList<>();
        coreJobs.add("job01");
        coreJobs.add("job02");
        coreJobs.add("job03");
        coreJobs.add("job04");

        // Define test H3 Jobs
        Map<String, String> h3Jobs = new HashMap<>();
        h3Jobs.put("job01", "RUNNING");
        h3Jobs.put("job02", "FINISHED");
        h3Jobs.put("job03", "PAUSED");

        // stubbing getActiveH3Jobs method for spying
        doReturn(h3Jobs).when(hah3Spy).getActiveH3Jobs();

        // Test Harvest Agent H3 recoverHarvests()
        hah3Spy.recoverHarvests(coreJobs);

        for(String job : coreJobs){
            Harvester recoveredJob = hah3Spy.getHarvester(job);
            if(h3Jobs.containsKey(job)){
                assertNotNull(recoveredJob);
                assertEquals(recoveredJob.getName(), job);
            }
            else{
                assertNull(recoveredJob);
            }
        }
    }

}
