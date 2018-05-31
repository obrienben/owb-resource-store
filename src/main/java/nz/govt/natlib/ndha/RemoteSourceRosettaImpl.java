package nz.govt.natlib.ndha;

import com.exlibris.dps.DeliveryAccessWS;
import com.exlibris.dps.DeliveryAccessWS_Service;
import com.exlibris.dps.Exception_Exception;
import nz.govt.natlib.ndha.rosettaIEMetaDataParser.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.IOUtils;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by Developer on 15/12/2017.
 */
public class RemoteSourceRosettaImpl implements RemoteSource {

    private static final Logger log = LogManager.getLogger(RemoteSourceRosettaImpl.class);

    // The Rosetta Web Services is available on localhost.
    //private static final String NDHA_WEB_SERVICES_HOSTNAME = "localhost:1801";
    private static final String NDHA_WEB_SERVICES_HOSTNAME = "appserv25.natlib.govt.nz:1801";
//    private static final String NDHA_WEB_SERVICES_HOSTNAME = "ndhadelivertest.natlib.govt.nz";
    // Delivery Web Services wsdl URL to initialize the web service method calls
    private static final String DELIVERY_WS_WSDL_URL = "/dpsws/delivery/DeliveryAccessWS?wsdl";
    // Delivery Servlet URL for viewers (Rosetta) - used for generating the dps_dvs session
    private static final String DELIVERY_VIEWER_URL = "/delivery/DeliveryManagerServlet?dps_pid=";
    private Map<String, String> filePaths = null;
    private int accessCode = 0;

    private DeliveryAccessWS deliveryWS;

    public RemoteSourceRosettaImpl(){
        filePaths = new HashMap<>();
        deliveryWS = null;
    }

    @Override
    public boolean lookup(String filename) {
        String dps_pid = null;

        if(filename.endsWith(".warc")){
            dps_pid = filename.substring(0, filename.indexOf(".warc"));
        }
        else if(filename.endsWith(".arc")){
            dps_pid = filename.substring(0, filename.indexOf(".arc"));
        }

        return processRequest(dps_pid);
    }

    @Override
    public boolean accessAllowed() {
        if(accessCode > 0 && accessCode < 200){
            return true;
        }
        return false;
    }

    @Override
    public Path getWarc(String name) throws Exception {
        Path warcPath = Paths.get(filePaths.get(name));
        return warcPath;
    }

    @Override
    public Map<String, String> getAllWarcs() {
        return filePaths;
    }

    private String getIeMetsString(String dps_pid) throws MalformedURLException, Exception_Exception {
        String dps_session = null;
        String ieMetsXml = null;

        // Initialize the Web Service call
        deliveryWS = new DeliveryAccessWS_Service(new URL("http://" + NDHA_WEB_SERVICES_HOSTNAME + DELIVERY_WS_WSDL_URL),
                new QName("http://dps.exlibris.com/", "DeliveryAccessWS")).getDeliveryAccessWSPort();

        if (deliveryWS != null) {
            log.info("Rosetta Delivery Web Service initialized for pid:[" + dps_pid + "]");

            dps_session = generateDPSSession(dps_pid);
            log.info("New Rosetta Delivery session:[" + dps_session + "] generated for pid:[" + dps_pid + "]");

            // Retrieve the rosetta IE METS xml to retrieve the file path
            ieMetsXml = deliveryWS.getExtendedIEByDVS(dps_session, 0);

            if (ieMetsXml != null) {
                log.debug("Received rosetta IE METS xml:\r\n" + ieMetsXml + "\r\n");
                return ieMetsXml;
            }
        }
        return null;
    }


    private boolean processRequest(String dps_pid) {

        // Ensure the DPS PID and DPS SESSION is not null
        if ( (dps_pid != null) && (!(dps_pid.isEmpty())) ) {

            String ieMetsXml = null;

            try {

                ieMetsXml = getIeMetsString(dps_pid);

                if (ieMetsXml != null) {

                    // Parse the IE Mets xml to retrieve the file path for the given file ID
                    IEModel ieObj = new DOMBasedIEMetaDataParser().parseIEMetadata(dps_pid, ieMetsXml);

                    // Retrieve the file path based on the file pid from the IE/REP/FILEGRP
                    if (ieObj != null) {

                        // Set access code for IE
                        accessCode = Integer.parseInt(ieObj.getAccessRightPolicy().getPolicyId());

                        // Retrieve all the Representations
                        Map<String, RepresentationModel> repModels = ieObj.getRepresentations();
                        if (repModels != null) {

                            // Retrieve the File Model from the PM RepModel
                            for (Map.Entry<String, RepresentationModel> repModel : repModels.entrySet()) {
                                RepresentationModel repStaff = repModel.getValue();

                                Map<String, FileModel> fileModels = repStaff.getFiles();
                                // For each file model, check if the file pid matches to get the file location
                                for (Map.Entry<String, FileModel> fileModel : fileModels.entrySet()) {
                                    FileModel fmVal = fileModel.getValue();
                                    if (fmVal != null) {
                                        if (fmVal.getFileExtension().equals("warc") || fmVal.getFileExtension().equals("arc")) {
                                            String filePath = fmVal.getFileLocation();
                                            String fileName = fmVal.getId() + "." + fmVal.getFileExtension();
                                            filePaths.put(fileName, filePath);
                                        }
                                    }
                                }

                            } //  END OF IF NULL FILE MODEL CHECK

                        } // END OF IF STAFF USER CHECK?

                    } // END OF IF IE OBJECT LOOP

                } // END OF IF deliveryWS NULL CHECK

            } catch (IEMetaDataParseException pex) {
                log.error("Error occurred during the IE Metadata Parse call to retrieve the rosetta file list. " + pex.getMessage());

            } catch (Exception_Exception ex) {
                log.error("Error occurred during the web service call to retrieve the rosetta file list. " + ex.getMessage());

            } catch (Exception ex) {
                log.error("Error: " + ex.getMessage());
                ex.printStackTrace();
            }

            if (!filePaths.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /*
	 * Method to generate the dps_dvs Rosetta session value for a given PID if the session is not present
	 */
    private String generateDPSSession(String pid) {

        String deliveryUrl = "http://" + NDHA_WEB_SERVICES_HOSTNAME + DELIVERY_VIEWER_URL + pid;

        log.info("Rosetta DeliveryURL created: " + deliveryUrl);

        try {
            String rosettaDeliveryResponse = "";
            URL url = new URL(deliveryUrl);
            URLConnection urlConn = url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            InputStream is = urlConn.getInputStream();

            String char_encoding = urlConn.getContentEncoding();
            if ((char_encoding != null) && (char_encoding.equals("gzip"))) {
                rosettaDeliveryResponse = IOUtils.toString(new GZIPInputStream(is));
            } else {
                rosettaDeliveryResponse = IOUtils.toString(is);
            }

            if (rosettaDeliveryResponse == null || rosettaDeliveryResponse.trim().isEmpty()) {
                return null;

            } else {

                int iFrameStartPosition = rosettaDeliveryResponse.indexOf("<iframe");
                int iFrameEndPosition = rosettaDeliveryResponse.indexOf("</iframe>");

                if (iFrameStartPosition > 0 && iFrameEndPosition > 0) {
                    rosettaDeliveryResponse = rosettaDeliveryResponse.substring(iFrameStartPosition, iFrameEndPosition);
                }

                int parameterNamePosition = rosettaDeliveryResponse.indexOf("dps_dvs=");
                if (parameterNamePosition < 0)
                    return null;

                String remainingString = rosettaDeliveryResponse.substring(parameterNamePosition);
                String[] parameterPairs = remainingString.split("[&]");
                if (parameterPairs == null || parameterPairs.length <= 0)
                    return null;

                String parameterNameValuePair = parameterPairs[0];
                return (parameterNameValuePair == null ? null : parameterNameValuePair.replace("dps_dvs=", ""));
            }

        } catch (Exception ex) {
            log.error("Error occurred while trying to generate a rosetta session. " + ex.getMessage());
            return null;
        }
    }


    public static void main(String[] args) {

        String filePID = "FL18894153.warc";
        //IE11459933
        RemoteSourceRosettaImpl rosetta = new RemoteSourceRosettaImpl();
        boolean result = rosetta.lookup(filePID);
//        String dpsSession = rosetta.generateDPSSession(filePID);
        if(result){
            System.out.println("Successful Rosetta lookup completed");
            System.out.println("Access Code: " + rosetta.accessCode);
            if(rosetta.accessAllowed()){
                System.out.println("Access granted");
            }
            else{
                System.out.println("Access denied");
            }
            Map<String, String> warcPaths = rosetta.getAllWarcs();
            System.out.println(warcPaths);
            try {
                Path oneWarc = rosetta.getWarc("FL18894153.warc");
                System.out.println(oneWarc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(result);
        // IE11459933
    }
}
