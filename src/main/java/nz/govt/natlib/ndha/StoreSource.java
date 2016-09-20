package nz.govt.natlib.ndha;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer on 20/09/2016.
 */

public class StoreSource {

    private static Map<String, WarcResource> warcs;

    public StoreSource(){
        System.out.println("Initializing Store Source");
        warcs = new HashMap<String, WarcResource>();
        WarcResource newWarc = new WarcResource("WEB-20160603014432482-00000-9193-ubuntu-8443.warc",
                "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\WEB-20160603014432482-00000-9193-ubuntu-8443.warc");
        warcs.put("WEB-20160603014432482-00000-9193-ubuntu-8443.warc", newWarc);
    }


    public boolean warcExists(String name){
        if(name != null && warcs.containsKey(name))
            return true;

        return false;
    }

    public String getWarc(String name){
        if(warcExists(name)){
            return warcs.get(name).getFilepath();
        }
        return null;
    }

    public boolean addWarc(String name){
        if(warcExists(name)){
            // Update timestamp
            warcs.get(name).regenLastUpdated();
            return true;
        }
        else{
            WarcResource newWarc = new WarcResource(name, "C:\\\\wct\\\\openwayback2.2\\\\store\\\\mwg\\\\" + name);
            warcs.put(name, newWarc);
            return true;
        }
    }

}
