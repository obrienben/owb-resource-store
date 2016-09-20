package nz.govt.natlib.ndha;

import java.util.Date;

/**
 * Created by Developer on 20/09/2016.
 */
public class WarcResource {


    private String filename;
    private String filepath;
    private Date lastUpdated;

    public WarcResource(String filename, String filepath){
        this.filename = filename;
        this.filepath = filepath;
        lastUpdated = new Date();
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void regenLastUpdated() {
        lastUpdated = new Date();
    }
}
