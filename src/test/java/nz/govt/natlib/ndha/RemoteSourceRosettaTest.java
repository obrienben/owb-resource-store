package nz.govt.natlib.ndha;

import com.exlibris.dps.DeliveryAccessWS;
import com.exlibris.dps.DeliveryAccessWS_Service;
import com.exlibris.dps.Exception_Exception;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import javax.xml.namespace.QName;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Developer on 14/05/2018.
 */
public class RemoteSourceRosettaTest {

    protected static Log log = LogFactory.getLog(RemoteSourceRosettaTest.class);
    private String VALID_WARC_PATH = null;
    private String VALID_WARC_PID = null;
    private String VALID_WARC_NAME = null;
    private Map<String, String> VALID_WARC_PATHS = new HashMap<>();
    RemoteSourceRosettaImpl rosettaTest;
    RemoteSourceRosettaImpl rosettaTestSpy;

//    DeliveryAccessWS deliveryAccessWSTest;
    DeliveryAccessWS deliveryAccessWSTest = new DeliveryAccessWS() {
        @Override
        public String getExtendedIEByDVS(String s, long l) throws Exception_Exception {
            return null;
        }

        @Override
        public String getDnxDocument(String s, String s1) throws Exception_Exception {
            return null;
        }

        @Override
        public String getMetadata(String s, String s1, String s2, String s3) throws Exception_Exception {
            return null;
        }

        @Override
        public String getBaseFileUrl(String s) throws Exception_Exception {
            return null;
        }

        @Override
        public String getHeartBit() {
            return null;
        }

        @Override
        public String getFilePathByDvs(String s, String s1) throws Exception_Exception {
            return null;
        }

        @Override
        public String getIEByDVS(String s) throws Exception_Exception {
            return null;
        }

        @Override
        public String getCMSRecord(String s, String s1) throws Exception_Exception {
            return null;
        }

        @Override
        public String getFullIEByDVS(String s) throws Exception_Exception {
            return null;
        }

        @Override
        public String getIE(String s) throws Exception_Exception {
            return null;
        }
    };




//    DeliveryAccessWS deliveryAccessSpy = null;

    @Before
    public void setUp() throws Exception {
        log.debug("Setting up HarvestAgentH3Test.");
//        hah3 = new HarvestAgentH3();
        VALID_WARC_NAME = "FL18894153.warc";
        VALID_WARC_PID = "FL18894153";
        VALID_WARC_PATH = "";
        VALID_WARC_PATHS.put("", "");

//        DeliveryAccessWS deliveryAccessWS = new DeliveryAccessWS_Service(new URL("http://" + "test" + "/delivery/DeliveryAccessWS?wsdl"),
//                new QName("http://dps.exlibris.com/", "DeliveryAccessWS")).getDeliveryAccessWSPort();

        rosettaTest = new RemoteSourceRosettaImpl();
        rosettaTestSpy = spy(rosettaTest);

        // stubbing getActiveH3Jobs method for spying
        try {
            doReturn(deliveryAccessWSTest).when(rosettaTestSpy).initializeDeliveryWS();
            doReturn("test~dps~session").when(rosettaTestSpy).generateDPSSession(VALID_WARC_PID);
            doReturn("Test XML").when(rosettaTestSpy).getIeMetsXml("test~dps~session");
        } catch (Exception_Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testValidLookup() {

        rosettaTest = new RemoteSourceRosettaImpl();
        Path warcPath = null;

        // Test lookup
        if(rosettaTestSpy.lookup(VALID_WARC_NAME)){

            // Test access restriction
            if(rosettaTestSpy.accessAllowed()){

                // Test warc paths retrieval
                try {
                    warcPath = rosettaTestSpy.getWarc(VALID_WARC_NAME);
                    assertEquals(warcPath.toString(), VALID_WARC_PATH);
                } catch (Exception e) {
                    fail(e.getMessage());
                }

                Map<String, String> warcPaths = rosettaTestSpy.getAllWarcs();
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


    private String getIeMetsXmlString(){
        return "<mets:mets xmlns:mets=\"http://www.loc.gov/METS/\">\n" +
                "  <mets:dmdSec ID=\"ie-dmd\">\n" +
                "    <mets:mdWrap MDTYPE=\"DC\">\n" +
                "      <mets:xmlData>\n" +
                "        <dc:record xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "          <dc:title>http://www.mch.govt.nz/</dc:title>\n" +
                "          <dc:date>2017-08-03 10:07:09.0</dc:date>\n" +
                "          <dcterms:available>2017-08-03 10:07:09.0</dcterms:available>\n" +
                "          <dc:rights>100</dc:rights>\n" +
                "          <dc:type>InteractiveResource</dc:type>\n" +
                "          <dc:format>text</dc:format>\n" +
                "        </dc:record>\n" +
                "      </mets:xmlData>\n" +
                "    </mets:mdWrap>\n" +
                "  </mets:dmdSec>\n" +
                "  <mets:amdSec ID=\"REP18894152-amd\">\n" +
                "    <mets:techMD ID=\"REP18894152-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"generalRepCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"preservationType\">PRESERVATION_MASTER</key>\n" +
                "                <key id=\"usageType\">VIEW</key>\n" +
                "                <key id=\"DigitalOriginal\">true</key>\n" +
                "                <key id=\"RevisionNumber\">1</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">REP18894152</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"objectType\">REPRESENTATION</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"REP18894152-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"REP18894152-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_REP18894152</key>\n" +
                "                <key id=\"UUID\">1913903025</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"MID\">REP18894152-1</key>\n" +
                "                <key id=\"UUID\">1913903022</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:25</key>\n" +
                "                <key id=\"metadataType\">32</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"REP18894152-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894162-amd\">\n" +
                "    <mets:techMD ID=\"FL18894162-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">10</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894162</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">5d2ce18903f0a0c935364b0596db2361</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">dd5ae108e01c144486a69c6a3a4679b07e1bc355</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">788d4278</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894162.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">crawl-manifest.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">crawl-manifest.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">crawl-manifest.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/crawl-manifest.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">689</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894162-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894162-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894162</key>\n" +
                "                <key id=\"UUID\">1913903034</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894162-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894162;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl-manifest.txt;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894162;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl-manifest.txt;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894162;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl-manifest.txt;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894162;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894162;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894162;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:13</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894162;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894161-amd\">\n" +
                "    <mets:techMD ID=\"FL18894161-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">9</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894161</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">e98aed9c38e631a4bfbe5aa7ea3dafa8</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">b25595388ffbbf8d26263c5c57f66ba9f7bbdd2e</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">23d73177</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894161.log: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta, Rule ID: 1821566679</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/62</key>\n" +
                "                <key id=\"formatName\">x-fmt/62</key>\n" +
                "                <key id=\"formatDescription\">Log File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">strippedcrawl.log</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">strippedcrawl.log</key>\n" +
                "                <key id=\"fileOriginalPath\">strippedcrawl.log</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/strippedcrawl.log</key>\n" +
                "                <key id=\"fileExtension\">log</key>\n" +
                "                <key id=\"fileSizeBytes\">150279</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/62</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894161-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894161-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894161</key>\n" +
                "                <key id=\"UUID\">1913903043</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:24</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894161-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894161;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=strippedcrawl.log;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894161;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=strippedcrawl.log;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894161;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=strippedcrawl.log;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894161;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.formatIdList eq x-fmt/62,fmt/904;input.fileExtension eq log;input.producerId eq any;input.pluginInstanceName eq any;input.identificationMethod eq any;output.formatId eq x-fmt/62</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894161;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894161;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894172-amd\">\n" +
                "    <mets:techMD ID=\"FL18894172-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">20</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894172</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">8648af626b9342af5de41414503a439d</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">17b21faf8ed7d74d7944abc9753c42423d4ead4d</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">aa31cdf9</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"type\">TECHMD</key>\n" +
                "                <key id=\"vsAgent\">JHOVE , XML-hul 1.4 , Plugin Version 3.0</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894172.xml: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_DROID</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">fmt/101</key>\n" +
                "                <key id=\"formatName\">fmt/101</key>\n" +
                "                <key id=\"formatVersion\">1</key>\n" +
                "                <key id=\"formatDescription\">Extensible Markup Language</key>\n" +
                "                <key id=\"exactFormatIdentification\">true</key>\n" +
                "                <key id=\"mimeType\">text/xml</key>\n" +
                "                <key id=\"agentVersion\">6.1.5</key>\n" +
                "                <key id=\"agentSignatureVersion\">Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">METS-30212207.xml</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">METS-30212207.xml</key>\n" +
                "                <key id=\"fileOriginalPath\">METS-30212207.xml</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/METS-30212207.xml</key>\n" +
                "                <key id=\"fileExtension\">xml</key>\n" +
                "                <key id=\"fileMIMEType\">text/xml</key>\n" +
                "                <key id=\"fileSizeBytes\">13908</key>\n" +
                "                <key id=\"formatLibraryId\">fmt/101</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"significantProperties\">\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.namespaceURI</key>\n" +
                "                <key id=\"significantPropertiesValue\">http://www.loc.gov/METS/</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">text.encoding</key>\n" +
                "                <key id=\"significantPropertiesValue\">UTF-8</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.namespaces</key>\n" +
                "                <key id=\"significantPropertiesValue\">[Prefix= ;URI=http://www.loc.gov/METS/ ;Prefix=mets ;URI=http://www.loc.gov/METS/ ;Prefix=vCard ;URI=http://www.w3.org/2001/vcard-rdf/3.0# ;Prefix=wct ;URI=http://dia-nz.github.io/webcurator/schemata/webcuratortool-1.0.dtd ;Prefix=xlink ;URI=http://www.w3.org/1999/xlink ;Prefix=xsi ;URI=http://www.w3.org/2001/XMLSchema-instance ;Prefix=dc ;URI=http://purl.org/dc/elements/1.1/]</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.parser</key>\n" +
                "                <key id=\"significantPropertiesValue\">org.apache.xerces.jaxp.SAXParserImpl$JAXPSAXParser</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.schemaRoot</key>\n" +
                "                <key id=\"significantPropertiesValue\">mets:mets</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.schemaNamespaceURI</key>\n" +
                "                <key id=\"significantPropertiesValue\">http://www.loc.gov/METS/</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.schemaLocation</key>\n" +
                "                <key id=\"significantPropertiesValue\">http://www.loc.gov/standards/mets/mets.xsd</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileValidation\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">JHOVE , XML-hul 1.4 , Plugin Version 3.0</key>\n" +
                "                <key id=\"pluginName\">XML-hul-1.10</key>\n" +
                "                <key id=\"format\">XML</key>\n" +
                "                <key id=\"version\">1.0</key>\n" +
                "                <key id=\"mimeType\">text/xml</key>\n" +
                "                <key id=\"isValid\">true</key>\n" +
                "                <key id=\"isWellFormed\">true</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894172-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894172-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894172</key>\n" +
                "                <key id=\"UUID\">1913903044</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:25</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894172-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894172;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=METS-30212207.xml;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894172;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=METS-30212207.xml;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894172;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=METS-30212207.xml;DATE=03 08 2017 11:55:27;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894172;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:27</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=fmt/101;IDENTIFICATION_METHOD=SIGNATURE;FILE_EXTENSION=xml;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894172;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">165</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Technical Metadata extraction performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">JHOVE , XML-hul 1.4 , Plugin Version 3.0</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894172;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=49;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894171-amd\">\n" +
                "    <mets:techMD ID=\"FL18894171-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">19</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894171</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">b12bfafe7121cc2fd1014a28c391f9b1</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">6bc6da0efcf5fe7b7ecf6518acada949e46ef129</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">d1dd42fc</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894171.cdx: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta, Rule ID: 1772794170</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">fmt/869</key>\n" +
                "                <key id=\"formatName\">fmt/869</key>\n" +
                "                <key id=\"formatDescription\">CDX Internet Archive Index</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">IAH-20170802233027-00001-blake-z1.cdx</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">IAH-20170802233027-00001-blake-z1.cdx</key>\n" +
                "                <key id=\"fileOriginalPath\">IAH-20170802233027-00001-blake-z1.cdx</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/IAH-20170802233027-00001-blake-z1.cdx</key>\n" +
                "                <key id=\"fileExtension\">cdx</key>\n" +
                "                <key id=\"fileSizeBytes\">368058</key>\n" +
                "                <key id=\"formatLibraryId\">fmt/869</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894171-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894171-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894171</key>\n" +
                "                <key id=\"UUID\">1913903042</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:25</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894171-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894171;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00001-blake-z1.cdx;DATE=03 08 2017 11:55:28;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894171;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00001-blake-z1.cdx;DATE=03 08 2017 11:55:28;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894171;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00001-blake-z1.cdx;DATE=03 08 2017 11:55:28;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894171;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq cdx;input.pluginInstanceName eq any;input.identificationMethod eq any;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq fmt/869</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=fmt/869;FILE_EXTENSION=cdx;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894171;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=fmt/869;FILE_EXTENSION=cdx;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894171;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894160-amd\">\n" +
                "    <mets:techMD ID=\"FL18894160-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">8</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894160</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">c7befc07da7075e367b8fd5e9f362ccf</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">53e49ed0becc6ce5627ff59c6ad4dec9e621ac74</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">d2cc7c0d</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894160.log: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta, Rule ID: 1821566679</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/62</key>\n" +
                "                <key id=\"formatName\">x-fmt/62</key>\n" +
                "                <key id=\"formatDescription\">Log File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">crawl.log</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">crawl.log</key>\n" +
                "                <key id=\"fileOriginalPath\">crawl.log</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/crawl.log</key>\n" +
                "                <key id=\"fileExtension\">log</key>\n" +
                "                <key id=\"fileSizeBytes\">154943</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/62</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894160-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894160-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894160</key>\n" +
                "                <key id=\"UUID\">1913903041</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:24</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894160-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894160;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl.log;DATE=03 08 2017 11:55:28;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:28</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894160;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl.log;DATE=03 08 2017 11:55:28;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894160;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl.log;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894160;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.formatIdList eq x-fmt/62,fmt/904;input.fileExtension eq log;input.producerId eq any;input.pluginInstanceName eq any;input.identificationMethod eq any;output.formatId eq x-fmt/62</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894160;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894160;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894170-amd\">\n" +
                "    <mets:techMD ID=\"FL18894170-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">18</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894170</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">9936717b0cd0692812fde33033827ff8</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">421fb29fc82da8537d2052c3ba47fcb89d8d81d7</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">9272cc40</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894170.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">processors-report.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">processors-report.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">processors-report.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/processors-report.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">1260</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894170-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894170-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894170</key>\n" +
                "                <key id=\"UUID\">1913903040</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894170-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894170;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=processors-report.txt;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894170;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=processors-report.txt;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894170;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=processors-report.txt;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894170;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894170;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894170;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:13</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894170;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894159-amd\">\n" +
                "    <mets:techMD ID=\"FL18894159-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">7</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894159</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">b8c38d5c575b4668c6d1039fb1436b49</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">aa5346af042ee895e01e6a047c3a445b425ad9a4</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">fa4f5a93</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894159.log: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta, Rule ID: 1821566679</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/62</key>\n" +
                "                <key id=\"formatName\">x-fmt/62</key>\n" +
                "                <key id=\"formatDescription\">Log File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">progress-statistics.log</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">progress-statistics.log</key>\n" +
                "                <key id=\"fileOriginalPath\">progress-statistics.log</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/progress-statistics.log</key>\n" +
                "                <key id=\"fileExtension\">log</key>\n" +
                "                <key id=\"fileSizeBytes\">15108</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/62</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894159-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894159-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894159</key>\n" +
                "                <key id=\"UUID\">1913903023</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:24</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894159-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894159;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=progress-statistics.log;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894159;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=progress-statistics.log;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894159;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=progress-statistics.log;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894159;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.formatIdList eq x-fmt/62,fmt/904;input.fileExtension eq log;input.producerId eq any;input.pluginInstanceName eq any;input.identificationMethod eq any;output.formatId eq x-fmt/62</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894159;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894159;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894158-amd\">\n" +
                "    <mets:techMD ID=\"FL18894158-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">6</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894158</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">3235d6f2da7fb446eaa4f407ed093079</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">6dc8467013082b8c06f91509833b38b76d8cd0a5</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">7bd55ac4</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894158.log: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta, Rule ID: 1821566679</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/62</key>\n" +
                "                <key id=\"formatName\">x-fmt/62</key>\n" +
                "                <key id=\"formatDescription\">Log File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">uri-errors.log</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">uri-errors.log</key>\n" +
                "                <key id=\"fileOriginalPath\">uri-errors.log</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/uri-errors.log</key>\n" +
                "                <key id=\"fileExtension\">log</key>\n" +
                "                <key id=\"fileSizeBytes\">95631</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/62</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894158-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894158-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894158</key>\n" +
                "                <key id=\"UUID\">1913903032</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:24</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894158-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894158;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=uri-errors.log;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894158;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=uri-errors.log;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894158;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=uri-errors.log;DATE=03 08 2017 11:55:29;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:29</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894158;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.formatIdList eq x-fmt/62,fmt/904;input.fileExtension eq log;input.producerId eq any;input.pluginInstanceName eq any;input.identificationMethod eq any;output.formatId eq x-fmt/62</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894158;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894158;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894169-amd\">\n" +
                "    <mets:techMD ID=\"FL18894169-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">17</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894169</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">2a1935336f8dc81bd1fe0f352fb78ead</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">8db32cc5956ed4f122a458b89f951149e84c9316</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">95b0bba5</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894169.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">frontier-report.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">frontier-report.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">frontier-report.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/frontier-report.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">846</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894169-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894169-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894169</key>\n" +
                "                <key id=\"UUID\">1913903033</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894169-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894169;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=frontier-report.txt;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894169;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=frontier-report.txt;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894169;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=frontier-report.txt;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894169;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894169;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894169;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894169;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894157-amd\">\n" +
                "    <mets:techMD ID=\"FL18894157-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">5</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894157</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">f3b14b43e76fd1b69096f339718198a6</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">47bf19061653c7a65227960dc5bd7657ba36fccf</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">cb15dd34</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894157.log: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta, Rule ID: 1821566679</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/62</key>\n" +
                "                <key id=\"formatName\">x-fmt/62</key>\n" +
                "                <key id=\"formatDescription\">Log File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">sortedcrawl.log</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">sortedcrawl.log</key>\n" +
                "                <key id=\"fileOriginalPath\">sortedcrawl.log</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/sortedcrawl.log</key>\n" +
                "                <key id=\"fileExtension\">log</key>\n" +
                "                <key id=\"fileSizeBytes\">150279</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/62</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894157-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894157-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894157</key>\n" +
                "                <key id=\"UUID\">1913903030</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:24</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894157-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894157;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=sortedcrawl.log;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894157;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=sortedcrawl.log;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894157;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=sortedcrawl.log;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894157;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.formatIdList eq x-fmt/62,fmt/904;input.fileExtension eq log;input.producerId eq any;input.pluginInstanceName eq any;input.identificationMethod eq any;output.formatId eq x-fmt/62</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894157;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/62;FILE_EXTENSION=log;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894157;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894168-amd\">\n" +
                "    <mets:techMD ID=\"FL18894168-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">16</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894168</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">725eb71124d714b0eeed49b9c442d851</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">5c34e5b425a293abcf1f65dac2811de2c2d29db4</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">8a126ae9</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894168.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">seeds-report.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">seeds-report.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">seeds-report.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/seeds-report.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">70</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894168-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894168-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894168</key>\n" +
                "                <key id=\"UUID\">1913903031</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894168-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894168;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=seeds-report.txt;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894168;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=seeds-report.txt;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894168;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=seeds-report.txt;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894168;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894168;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894168;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894168;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894156-amd\">\n" +
                "    <mets:techMD ID=\"FL18894156-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">4</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894156</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">11ace400b3a4358adcdc6ea438d8f2db</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">552b660a985923f7cce4ea6cdf595f4a6e100f28</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">3fcf1fc2</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">TECHMD</key>\n" +
                "                <key id=\"vsAgent\">JHOVE , XML-hul 1.4 , Plugin Version 3.0</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894156.xml: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_DROID</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">fmt/101</key>\n" +
                "                <key id=\"formatName\">fmt/101</key>\n" +
                "                <key id=\"formatVersion\">1</key>\n" +
                "                <key id=\"formatDescription\">Extensible Markup Language</key>\n" +
                "                <key id=\"exactFormatIdentification\">true</key>\n" +
                "                <key id=\"mimeType\">text/xml</key>\n" +
                "                <key id=\"agentVersion\">6.1.5</key>\n" +
                "                <key id=\"agentSignatureVersion\">Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">order.xml</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">order.xml</key>\n" +
                "                <key id=\"fileOriginalPath\">order.xml</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/order.xml</key>\n" +
                "                <key id=\"fileExtension\">xml</key>\n" +
                "                <key id=\"fileMIMEType\">text/xml</key>\n" +
                "                <key id=\"fileSizeBytes\">16841</key>\n" +
                "                <key id=\"formatLibraryId\">fmt/101</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"significantProperties\">\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.namespacePrefix</key>\n" +
                "                <key id=\"significantPropertiesValue\">xsi</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.namespaceURI</key>\n" +
                "                <key id=\"significantPropertiesValue\">http://www.w3.org/2001/XMLSchema-instance</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">text.encoding</key>\n" +
                "                <key id=\"significantPropertiesValue\">UTF-8</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.namespaces</key>\n" +
                "                <key id=\"significantPropertiesValue\">[Prefix=xsi ;URI=http://www.w3.org/2001/XMLSchema-instance]</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.parser</key>\n" +
                "                <key id=\"significantPropertiesValue\">org.apache.xerces.jaxp.SAXParserImpl$JAXPSAXParser</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">xml.schemaRoot</key>\n" +
                "                <key id=\"significantPropertiesValue\">crawl-order</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileValidation\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">JHOVE , XML-hul 1.4 , Plugin Version 3.0</key>\n" +
                "                <key id=\"pluginName\">XML-hul-1.10</key>\n" +
                "                <key id=\"format\">XML</key>\n" +
                "                <key id=\"version\">1.0</key>\n" +
                "                <key id=\"mimeType\">text/xml</key>\n" +
                "                <key id=\"isValid\">true</key>\n" +
                "                <key id=\"isWellFormed\">true</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894156-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894156-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894156</key>\n" +
                "                <key id=\"UUID\">1913903028</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:23</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894156-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894156;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=order.xml;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894156;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=order.xml;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:30</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894156;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=order.xml;DATE=03 08 2017 11:55:30;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894156;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=fmt/101;IDENTIFICATION_METHOD=SIGNATURE;FILE_EXTENSION=xml;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894156;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">165</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Technical Metadata extraction performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">JHOVE , XML-hul 1.4 , Plugin Version 3.0</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894156;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=49;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894167-amd\">\n" +
                "    <mets:techMD ID=\"FL18894167-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">15</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894167</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">2e69710dcb7edaad3bc5defb52c7cbfc</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">412955cdc8996cc9aa774de3cc24db8ad325b940</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">f8a51ad3</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894167.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">mimetype-report.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">mimetype-report.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">mimetype-report.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/mimetype-report.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">444</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894167-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894167-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894167</key>\n" +
                "                <key id=\"UUID\">1913903029</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894167-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894167;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=mimetype-report.txt;DATE=03 08 2017 11:55:31;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894167;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=mimetype-report.txt;DATE=03 08 2017 11:55:31;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894167;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=mimetype-report.txt;DATE=03 08 2017 11:55:31;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894167;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894167;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894167;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894167;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894155-amd\">\n" +
                "    <mets:techMD ID=\"FL18894155-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">3</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894155</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">9aacebbcf205e41b80c9c99b984647ed</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">6ea4446e335bd17acd3d6cff65e5f41fe05c010e</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">969491cf</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">TECHMD</key>\n" +
                "                <key id=\"vsAgent\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894155.warc: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_DROID</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">fmt/289</key>\n" +
                "                <key id=\"formatName\">fmt/289</key>\n" +
                "                <key id=\"formatDescription\">WARC</key>\n" +
                "                <key id=\"exactFormatIdentification\">true</key>\n" +
                "                <key id=\"agentVersion\">6.1.5</key>\n" +
                "                <key id=\"agentSignatureVersion\">Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">IAH-20170802233027-00000-blake-z1.warc</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">IAH-20170802233027-00000-blake-z1.warc</key>\n" +
                "                <key id=\"fileOriginalPath\">IAH-20170802233027-00000-blake-z1.warc</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/IAH-20170802233027-00000-blake-z1.warc</key>\n" +
                "                <key id=\"fileExtension\">warc</key>\n" +
                "                <key id=\"fileSizeBytes\">64799</key>\n" +
                "                <key id=\"formatLibraryId\">fmt/289</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"significantProperties\">\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.creatingSoftware</key>\n" +
                "                <key id=\"significantPropertiesValue\">Heritrix/1.14.1 http://crawler.archive.org</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.hostName</key>\n" +
                "                <key id=\"significantPropertiesValue\">blake-z1</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.iPAddress</key>\n" +
                "                <key id=\"significantPropertiesValue\">10.4.1.104</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.operator</key>\n" +
                "                <key id=\"significantPropertiesValue\">WCT</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.createdDate</key>\n" +
                "                <key id=\"significantPropertiesValue\">2017-08-02T22:07:06Z</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.robotPolicy</key>\n" +
                "                <key id=\"significantPropertiesValue\">ignore</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.warcFormat</key>\n" +
                "                <key id=\"significantPropertiesValue\">WARC File Format 0.17</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.conformsTo</key>\n" +
                "                <key id=\"significantPropertiesValue\">http://crawler.archive.org/warc/0.17/WARC0.17ISO.doc</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.warcDate</key>\n" +
                "                <key id=\"significantPropertiesValue\">2017-08-02T23:30:27Z</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.compressed</key>\n" +
                "                <key id=\"significantPropertiesValue\">false</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.mimeReport</key>\n" +
                "                <key id=\"significantPropertiesValue\">not recorded:2</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileValidation\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"pluginName\">nz.govt.natlib.adapter.arc.WarcAdapter</key>\n" +
                "                <key id=\"format\">WARC File Format 0.17</key>\n" +
                "                <key id=\"mimeType\">application/warc</key>\n" +
                "                <key id=\"isValid\">true</key>\n" +
                "                <key id=\"isWellFormed\">true</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894155-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894155-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894155</key>\n" +
                "                <key id=\"UUID\">1913903026</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:23</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894155-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894155;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00000-blake-z1.warc;DATE=03 08 2017 11:55:31;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894155;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00000-blake-z1.warc;DATE=03 08 2017 11:55:31;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894155;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00000-blake-z1.warc;DATE=03 08 2017 11:55:31;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894155;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:31</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=fmt/289;IDENTIFICATION_METHOD=SIGNATURE;FILE_EXTENSION=warc;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894155;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">165</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Technical Metadata extraction performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894155;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=49;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894166-amd\">\n" +
                "    <mets:techMD ID=\"FL18894166-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">14</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894166</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">77f2b7cc411db2d9f0c5bc98507cf84c</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">a52ccdbeb6b02d5befbb7dff146734f94ba6a0b6</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">8f43fbc3</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894166.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">hosts-report.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">hosts-report.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">hosts-report.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/hosts-report.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">684</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894166-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894166-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894166</key>\n" +
                "                <key id=\"UUID\">1913903027</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894166-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894166;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=hosts-report.txt;DATE=03 08 2017 11:55:32;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894166;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=hosts-report.txt;DATE=03 08 2017 11:55:32;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894166;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=hosts-report.txt;DATE=03 08 2017 11:55:32;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894166;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894166;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894166;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894166;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894154-amd\">\n" +
                "    <mets:techMD ID=\"FL18894154-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">2</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894154</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">05b5516994a1c94639fb742f55e9d96a</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">658773d14309dab7f274b13a84b5e0f64907a68a</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">e8ce41dd</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">TECHMD</key>\n" +
                "                <key id=\"vsAgent\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894154.warc: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_DROID</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">fmt/289</key>\n" +
                "                <key id=\"formatName\">fmt/289</key>\n" +
                "                <key id=\"formatDescription\">WARC</key>\n" +
                "                <key id=\"exactFormatIdentification\">true</key>\n" +
                "                <key id=\"agentVersion\">6.1.5</key>\n" +
                "                <key id=\"agentSignatureVersion\">Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">IAH-20170802233035-00002-blake-z1.warc</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">IAH-20170802233035-00002-blake-z1.warc</key>\n" +
                "                <key id=\"fileOriginalPath\">IAH-20170802233035-00002-blake-z1.warc</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/IAH-20170802233035-00002-blake-z1.warc</key>\n" +
                "                <key id=\"fileExtension\">warc</key>\n" +
                "                <key id=\"fileSizeBytes\">1093319</key>\n" +
                "                <key id=\"formatLibraryId\">fmt/289</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"significantProperties\">\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.creatingSoftware</key>\n" +
                "                <key id=\"significantPropertiesValue\">Heritrix/1.14.1 http://crawler.archive.org</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.hostName</key>\n" +
                "                <key id=\"significantPropertiesValue\">blake-z1</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.iPAddress</key>\n" +
                "                <key id=\"significantPropertiesValue\">10.4.1.104</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.operator</key>\n" +
                "                <key id=\"significantPropertiesValue\">WCT</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.createdDate</key>\n" +
                "                <key id=\"significantPropertiesValue\">2017-08-02T22:07:06Z</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.robotPolicy</key>\n" +
                "                <key id=\"significantPropertiesValue\">ignore</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.warcFormat</key>\n" +
                "                <key id=\"significantPropertiesValue\">WARC File Format 0.17</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.conformsTo</key>\n" +
                "                <key id=\"significantPropertiesValue\">http://crawler.archive.org/warc/0.17/WARC0.17ISO.doc</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.warcDate</key>\n" +
                "                <key id=\"significantPropertiesValue\">2017-08-02T23:30:35Z</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.compressed</key>\n" +
                "                <key id=\"significantPropertiesValue\">false</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.mimeReport</key>\n" +
                "                <key id=\"significantPropertiesValue\">not recorded:3</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileValidation\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"pluginName\">nz.govt.natlib.adapter.arc.WarcAdapter</key>\n" +
                "                <key id=\"format\">WARC File Format 0.17</key>\n" +
                "                <key id=\"mimeType\">application/warc</key>\n" +
                "                <key id=\"isValid\">true</key>\n" +
                "                <key id=\"isWellFormed\">true</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894154-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894154-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894154</key>\n" +
                "                <key id=\"UUID\">1913903038</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:23</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894154-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894154;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233035-00002-blake-z1.warc;DATE=03 08 2017 11:55:32;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894154;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233035-00002-blake-z1.warc;DATE=03 08 2017 11:55:32;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894154;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233035-00002-blake-z1.warc;DATE=03 08 2017 11:55:32;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894154;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=fmt/289;IDENTIFICATION_METHOD=SIGNATURE;FILE_EXTENSION=warc;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894154;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">165</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Technical Metadata extraction performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894154;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=49;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894165-amd\">\n" +
                "    <mets:techMD ID=\"FL18894165-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">13</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894165</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">d598c93e3209fbc9e4967f1c2148ebbf</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">f8842084143b3ece5cdd0444119da505633f1fa6</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">f3646f83</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894165.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">responsecode-report.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">responsecode-report.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">responsecode-report.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/responsecode-report.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">62</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894165-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894165-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894165</key>\n" +
                "                <key id=\"UUID\">1913903039</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894165-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:32</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894165;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=responsecode-report.txt;DATE=03 08 2017 11:55:32;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894165;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=responsecode-report.txt;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894165;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=responsecode-report.txt;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894165;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894165;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894165;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894165;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894164-amd\">\n" +
                "    <mets:techMD ID=\"FL18894164-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">12</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894164</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">bb423650a6d936485d39e77406f9fb5b</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">25b33fa93fc428c73e510077866b9f8a6ff957b1</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">71b710fa</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894164.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">crawl-report.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">crawl-report.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">crawl-report.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/crawl-report.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">318</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894164-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894164-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894164</key>\n" +
                "                <key id=\"UUID\">1913903037</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894164-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894164;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl-report.txt;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894164;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl-report.txt;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894164;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=crawl-report.txt;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894164;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894164;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894164;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894164;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894153-amd\">\n" +
                "    <mets:techMD ID=\"FL18894153-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">1</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"modifiedBy\">NLNZwct</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894153</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">447b03dc9152b989069677a8b0d5b249</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">7a23565493dbfb2273dfaee713342335aa8f3043</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">cc9c94b4</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:35</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:35</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"type\">TECHMD</key>\n" +
                "                <key id=\"vsAgent\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894153.warc: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_DROID</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">fmt/289</key>\n" +
                "                <key id=\"formatName\">fmt/289</key>\n" +
                "                <key id=\"formatDescription\">WARC</key>\n" +
                "                <key id=\"exactFormatIdentification\">true</key>\n" +
                "                <key id=\"agentVersion\">6.1.5</key>\n" +
                "                <key id=\"agentSignatureVersion\">Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">IAH-20170802233027-00001-blake-z1.warc</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">IAH-20170802233027-00001-blake-z1.warc</key>\n" +
                "                <key id=\"fileOriginalPath\">IAH-20170802233027-00001-blake-z1.warc</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/IAH-20170802233027-00001-blake-z1.warc</key>\n" +
                "                <key id=\"fileExtension\">warc</key>\n" +
                "                <key id=\"fileSizeBytes\">35806245</key>\n" +
                "                <key id=\"formatLibraryId\">fmt/289</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"significantProperties\">\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.creatingSoftware</key>\n" +
                "                <key id=\"significantPropertiesValue\">Heritrix/1.14.1 http://crawler.archive.org</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.hostName</key>\n" +
                "                <key id=\"significantPropertiesValue\">blake-z1</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.iPAddress</key>\n" +
                "                <key id=\"significantPropertiesValue\">10.4.1.104</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.operator</key>\n" +
                "                <key id=\"significantPropertiesValue\">WCT</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.createdDate</key>\n" +
                "                <key id=\"significantPropertiesValue\">2017-08-02T22:07:06Z</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.robotPolicy</key>\n" +
                "                <key id=\"significantPropertiesValue\">ignore</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.warcFormat</key>\n" +
                "                <key id=\"significantPropertiesValue\">WARC File Format 0.17</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.conformsTo</key>\n" +
                "                <key id=\"significantPropertiesValue\">http://crawler.archive.org/warc/0.17/WARC0.17ISO.doc</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.warcDate</key>\n" +
                "                <key id=\"significantPropertiesValue\">2017-08-02T23:30:27Z</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.compressed</key>\n" +
                "                <key id=\"significantPropertiesValue\">false</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"significantPropertiesType\">web.mimeReport</key>\n" +
                "                <key id=\"significantPropertiesValue\">application/font-woff:1, application/font-woff2:1, application/javascript:17, application/vnd.ms-fontobject:2, application/x-font-ttf:1, application/xml:1, image/gif:2, image/jpeg:219, image/png:56, image/png; qs=0.7:2, image/svg+xml:3, not recorded:1247, text/css:16, text/html:6, text/html; charset=iso-8859-1:9, text/html; charset=utf-8:232, text/javascript:2, text/plain:12, text/plain; charset=utf-8:2, text/plain;charset=UTF-8:1, text/turtle; charset=utf-8:2</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileValidation\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"pluginName\">nz.govt.natlib.adapter.arc.WarcAdapter</key>\n" +
                "                <key id=\"format\">WARC File Format 0.17</key>\n" +
                "                <key id=\"mimeType\">application/warc</key>\n" +
                "                <key id=\"isValid\">true</key>\n" +
                "                <key id=\"isWellFormed\">true</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894153-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894153-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894153</key>\n" +
                "                <key id=\"UUID\">1913903036</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:23</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894153-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894153;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00001-blake-z1.warc;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894153;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00001-blake-z1.warc;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:33</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894153;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=IAH-20170802233027-00001-blake-z1.warc;DATE=03 08 2017 11:55:33;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:35</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894153;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:35</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=fmt/289;IDENTIFICATION_METHOD=SIGNATURE;FILE_EXTENSION=warc;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894153;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">165</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Technical Metadata extraction performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">Adapter: nz.govt.natlib.adapter.warc.WarcAdapter, Version 1.0, Plugin Version 1</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894153;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=49;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"FL18894163-amd\">\n" +
                "    <mets:techMD ID=\"FL18894163-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"groupID\">11</key>\n" +
                "                <key id=\"objectType\">FILE</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">FL18894163</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFixity\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">MD5</key>\n" +
                "                <key id=\"fixityValue\">78c80208a1b1af3ad65f4b36b75f57e5</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">SHA1</key>\n" +
                "                <key id=\"fixityValue\">ed6f24df31ec6fe85204e052e4f83f00aa69d0bb</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"agent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"fixityType\">CRC32</key>\n" +
                "                <key id=\"fixityValue\">236eed54</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"vsOutcome\">\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"type\">CHECKSUM</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"type\">VIRUSCHECK</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"type\">FILE_FORMAT</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"checkDate\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"type\">RISK_ANALYSIS</key>\n" +
                "                <key id=\"vsAgent\">REG_SA_DPS</key>\n" +
                "                <key id=\"result\">PASSED</key>\n" +
                "                <key id=\"vsEvaluation\">PASSED</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileVirusCheck\">\n" +
                "              <record>\n" +
                "                <key id=\"status\">PASSED</key>\n" +
                "                <key id=\"agent\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"content\">/exlibris1/operational_storage/oper_01/2017/08/03/file_17/V1-FL18894163.txt: OK</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"fileFormat\">\n" +
                "              <record>\n" +
                "                <key id=\"agent\">Rosetta</key>\n" +
                "                <key id=\"formatRegistry\">PRONOM</key>\n" +
                "                <key id=\"formatRegistryId\">x-fmt/111</key>\n" +
                "                <key id=\"formatName\">x-fmt/111</key>\n" +
                "                <key id=\"formatDescription\">Plain Text File</key>\n" +
                "                <key id=\"exactFormatIdentification\">false</key>\n" +
                "                <key id=\"mimeType\">text/plain</key>\n" +
                "                <key id=\"identificationMethod\">Rule</key>\n" +
                "                <key id=\"formatLibraryVersion\">5.209</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalFileCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"label\">seeds.txt</key>\n" +
                "                <key id=\"fileLocationType\">FILE</key>\n" +
                "                <key id=\"fileOriginalName\">seeds.txt</key>\n" +
                "                <key id=\"fileOriginalPath\">seeds.txt</key>\n" +
                "                <key id=\"fileOriginalID\">/exlibris1/deposit_storage//337001-338000/dep_337046/deposit/content/streams/seeds.txt</key>\n" +
                "                <key id=\"fileExtension\">txt</key>\n" +
                "                <key id=\"fileMIMEType\">text/plain</key>\n" +
                "                <key id=\"fileSizeBytes\">24</key>\n" +
                "                <key id=\"formatLibraryId\">x-fmt/111</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"FL18894163-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\"/>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"FL18894163-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_FL18894163</key>\n" +
                "                <key id=\"UUID\">1913903035</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"FL18894163-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=MD5;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894163;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=seeds.txt;DATE=03 08 2017 11:55:36;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=SHA1;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894163;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=seeds.txt;DATE=03 08 2017 11:55:36;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">27</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Fixity check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_JAVA5_FIXITY</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">IE_PID=IE18894151;COPY_ID=null;ALGORITHM_NAME=CRC32;DEPOSIT_ACTIVITY_ID=337046;FILE_PID=FL18894163;SIP_ID=274658;PRODUCER_ID=80349;FILE_NAME=seeds.txt;DATE=03 08 2017 11:55:36;STATUS=SUCCESS;REP_PID=REP18894152;TASK_ID=1;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">24</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Virus check performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_VC_CLAMAV</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">DEPOSIT_ACTIVITY_ID=337046;PID=FL18894163;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=7;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894163;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:36</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">25</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Format Identification performed on file</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">REG_SA_DROID , Version 6.1.5 , Signature version Binary SF v.90/ Container SF v.20</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894163;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"eventType\">VALIDATION</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">198</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Automatically Set Format Library ID on File</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">SOFTWARE</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">ruleId=1811517138;input.producerId eq any;input.formatIdList eq any;input.fileExtension eq txt;input.mimeType eq any;input.fileSize eq any;input.createDate eq any;input.agent eq any;input.method eq any;output.formatId eq x-fmt/111</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">FORMAT_ID=x-fmt/111;RULE_ID=1811517138;FILE_EXTENSION=txt;DEPOSIT_ACTIVITY_ID=337046;PID=FL18894163;SIP_ID=274658;PRODUCER_ID=80349;TASK_ID=48;PROCESS_ID=548410831;MF_ID=7;</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:amdSec ID=\"ie-amd\">\n" +
                "    <mets:techMD ID=\"ie-amd-tech\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"webHarvesting\">\n" +
                "              <record>\n" +
                "                <key id=\"harvestDate\">2017-08-03 10:07:09.0</key>\n" +
                "                <key id=\"primarySeedURL\">http://www.mch.govt.nz/</key>\n" +
                "                <key id=\"targetName\">Ministry for Culture and Heritage</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"generalIECharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"IEEntityType\">WebHarvestIE</key>\n" +
                "                <key id=\"submissionReason\">Web Harvesting</key>\n" +
                "                <key id=\"status\">ACTIVE</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"internalIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">SIPID</key>\n" +
                "                <key id=\"internalIdentifierValue\">274658</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">PID</key>\n" +
                "                <key id=\"internalIdentifierValue\">IE18894151</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">DepositSetID</key>\n" +
                "                <key id=\"internalIdentifierValue\">337046</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"internalIdentifierType\">handle</key>\n" +
                "                <key id=\"internalIdentifierValue\">1727.10/1227234</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"objectCharacteristics\">\n" +
                "              <record>\n" +
                "                <key id=\"objectType\">INTELLECTUAL_ENTITY</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-09-27 14:05:14</key>\n" +
                "                <key id=\"modifiedBy\">smithk</key>\n" +
                "                <key id=\"owner\">CRS00.INS00.DPR00</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"objectIdentifier\">\n" +
                "              <record>\n" +
                "                <key id=\"objectIdentifierType\">ALMAMMS</key>\n" +
                "                <key id=\"objectIdentifierValue\">997260563502836</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"objectIdentifierType\">ALMAREP</key>\n" +
                "                <key id=\"objectIdentifierValue\">32279668060002836</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"objectIdentifierType\">OAI</key>\n" +
                "                <key id=\"objectIdentifierValue\">oai:nlnz.alma.exlibrisgroup.com:32279668060002836</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:techMD>\n" +
                "    <mets:rightsMD ID=\"ie-amd-rights\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"accessRightsPolicy\">\n" +
                "              <record>\n" +
                "                <key id=\"policyId\">100</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:rightsMD>\n" +
                "    <mets:sourceMD ID=\"ie-amd-source\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"metaData\">\n" +
                "              <record>\n" +
                "                <key id=\"MID\">DNX_IE18894151</key>\n" +
                "                <key id=\"UUID\">1913903021</key>\n" +
                "                <key id=\"creationDate\">2017-08-03 11:55:22</key>\n" +
                "                <key id=\"createdBy\">NLNZwct</key>\n" +
                "                <key id=\"modificationDate\">2017-08-03 11:55:55</key>\n" +
                "                <key id=\"modifiedBy\">SYSTEM</key>\n" +
                "                <key id=\"metadataType\">21</key>\n" +
                "                <key id=\"application\">validationProfileBasic</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:sourceMD>\n" +
                "    <mets:digiprovMD ID=\"ie-amd-digiprov\">\n" +
                "      <mets:mdWrap MDTYPE=\"OTHER\" OTHERMDTYPE=\"dnx\">\n" +
                "        <mets:xmlData>\n" +
                "          <dnx xmlns=\"http://www.exlibrisgroup.com/dps/dnx\">\n" +
                "            <section id=\"event\">\n" +
                "              <record>\n" +
                "                <key id=\"eventIdentifierType\">WCT</key>\n" +
                "                <key id=\"eventIdentifierValue\">WCT_1</key>\n" +
                "                <key id=\"eventType\">CREATION</key>\n" +
                "                <key id=\"eventDescription\">IE Created in NLNZ WCT</key>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 10:07:09</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">Created by leeg</key>\n" +
                "                <key id=\"eventOutcomeDetail2\">Created on 2017-08-03</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventIdentifierType\">WCT</key>\n" +
                "                <key id=\"eventIdentifierValue\">WCT_2</key>\n" +
                "                <key id=\"eventType\">CREATION</key>\n" +
                "                <key id=\"eventDescription\">Provenance Note from NLNZ WCT</key>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 00:00:00</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventOutcomeDetail1\">imported pdf and pubs pages</key>\n" +
                "              </record>\n" +
                "              <record>\n" +
                "                <key id=\"eventDateTime\">2017-08-03 11:55:55</key>\n" +
                "                <key id=\"eventType\">PROCESSING</key>\n" +
                "                <key id=\"eventIdentifierType\">DPS</key>\n" +
                "                <key id=\"eventIdentifierValue\">130</key>\n" +
                "                <key id=\"eventOutcome1\">SUCCESS</key>\n" +
                "                <key id=\"eventDescription\">Object's Metadata Record Modified</key>\n" +
                "                <key id=\"linkingAgentIdentifierType1\">USER</key>\n" +
                "                <key id=\"linkingAgentIdentifierValue1\">SYSTEM</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"producer\">\n" +
                "              <record>\n" +
                "                <key id=\"address1\">Wellington</key>\n" +
                "                <key id=\"address4\">NewZealand</key>\n" +
                "                <key id=\"defaultLanguage\">en</key>\n" +
                "                <key id=\"emailAddress\">ndha.admin@natlib.govt.nz</key>\n" +
                "                <key id=\"firstName\">NLNZ</key>\n" +
                "                <key id=\"lastName\">Internal Digitisation Programmes</key>\n" +
                "                <key id=\"telephone1\">04-4743000</key>\n" +
                "                <key id=\"authorativeName\">NLNZ Internal Digitisation Programmes</key>\n" +
                "                <key id=\"producerId\">80349</key>\n" +
                "                <key id=\"userIdAppId\">80345</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "            <section id=\"producerAgent\">\n" +
                "              <record>\n" +
                "                <key id=\"firstName\">Web Curator</key>\n" +
                "                <key id=\"lastName\">Admin</key>\n" +
                "              </record>\n" +
                "            </section>\n" +
                "          </dnx>\n" +
                "        </mets:xmlData>\n" +
                "      </mets:mdWrap>\n" +
                "    </mets:digiprovMD>\n" +
                "  </mets:amdSec>\n" +
                "  <mets:fileSec>\n" +
                "    <mets:fileGrp ID=\"REP18894152\" ADMID=\"REP18894152-amd\">\n" +
                "      <mets:file ID=\"FL18894162\" GROUPID=\"10\" ADMID=\"FL18894162-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894162.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894161\" GROUPID=\"9\" ADMID=\"FL18894161-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894161.log\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894172\" GROUPID=\"20\" ADMID=\"FL18894172-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894172.xml\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894171\" GROUPID=\"19\" ADMID=\"FL18894171-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894171.cdx\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894160\" GROUPID=\"8\" ADMID=\"FL18894160-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894160.log\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894170\" GROUPID=\"18\" ADMID=\"FL18894170-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894170.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894159\" GROUPID=\"7\" ADMID=\"FL18894159-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894159.log\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894158\" GROUPID=\"6\" ADMID=\"FL18894158-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894158.log\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894169\" GROUPID=\"17\" ADMID=\"FL18894169-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894169.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894157\" GROUPID=\"5\" ADMID=\"FL18894157-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894157.log\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894168\" GROUPID=\"16\" ADMID=\"FL18894168-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894168.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894156\" GROUPID=\"4\" ADMID=\"FL18894156-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894156.xml\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894167\" GROUPID=\"15\" ADMID=\"FL18894167-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894167.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894155\" GROUPID=\"3\" ADMID=\"FL18894155-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894155.warc\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894166\" GROUPID=\"14\" ADMID=\"FL18894166-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894166.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894154\" GROUPID=\"2\" ADMID=\"FL18894154-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894154.warc\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894165\" GROUPID=\"13\" ADMID=\"FL18894165-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894165.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894164\" GROUPID=\"12\" ADMID=\"FL18894164-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894164.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894153\" GROUPID=\"1\" ADMID=\"FL18894153-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_01/2017/08/03/file_7/V1-FL18894153.warc\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "      <mets:file ID=\"FL18894163\" GROUPID=\"11\" ADMID=\"FL18894163-amd\">\n" +
                "        <mets:FLocat LOCTYPE=\"URL\" xlin:href=\"/exlibris3/permanent_storage/file_02/2017/08/03/file_7/V1-FL18894163.txt\" xmlns:xlin=\"http://www.w3.org/1999/xlink\"/>\n" +
                "      </mets:file>\n" +
                "    </mets:fileGrp>\n" +
                "  </mets:fileSec>\n" +
                "  <mets:structMap ID=\"REP18894152-1\" TYPE=\"PHYSICAL\">\n" +
                "    <mets:div LABEL=\"Preservation Master\">\n" +
                "      <mets:div LABEL=\"Table of Contents\">\n" +
                "        <mets:div LABEL=\"IAH-20170802233027-00001-blake-z1.warc\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894153\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"IAH-20170802233035-00002-blake-z1.warc\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894154\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"IAH-20170802233027-00000-blake-z1.warc\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894155\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"order.xml\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894156\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"sortedcrawl.log\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894157\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"uri-errors.log\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894158\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"progress-statistics.log\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894159\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"crawl.log\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894160\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"strippedcrawl.log\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894161\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"crawl-manifest.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894162\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"seeds.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894163\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"crawl-report.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894164\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"responsecode-report.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894165\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"hosts-report.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894166\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"mimetype-report.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894167\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"seeds-report.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894168\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"frontier-report.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894169\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"processors-report.txt\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894170\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"IAH-20170802233027-00001-blake-z1.cdx\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894171\"/>\n" +
                "        </mets:div>\n" +
                "        <mets:div LABEL=\"METS-30212207.xml\" TYPE=\"FILE\">\n" +
                "          <mets:fptr FILEID=\"FL18894172\"/>\n" +
                "        </mets:div>\n" +
                "      </mets:div>\n" +
                "    </mets:div>\n" +
                "  </mets:structMap>\n" +
                "</mets:mets>";
    }

}
