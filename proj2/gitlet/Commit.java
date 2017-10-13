//bring in Hashmap
/**
 * Created by shene on 7/12/2017.
 */
package gitlet;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class Commit implements Serializable {
    private String _logMessage;
    private String _commitDate;
    private String author;
    private String _parentCommit;
    protected String hashID;
    private String vals;
    protected HashMap<String, String> blobs;

    //blank constructor of Commit/method commit
    public Commit(String lMsg, String cD, String auth, String pCmt, HashMap<String, String> blobs) {
        this._logMessage = lMsg;
        this._commitDate = cD;
        this.author = auth;
        this._parentCommit = pCmt;
        this.blobs = blobs;
        this.vals = new String();
        blobs.forEach((k, v) -> vals.concat(v));
        this.hashID = Utils.sha1(_logMessage, _commitDate, vals);
    }

    public Commit() {
        this._logMessage = "initial commit";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        this._commitDate = LocalDateTime.now().format(dtf);
        this.hashID = Utils.sha1(_logMessage, _commitDate);
    }

    public void serializeCommit() {
        Commit obj = this;
        File outFile = new File(System.getProperty("user.dir") + "/.gitlet/Commits/" + hashID);
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutput out = null;
            out = new ObjectOutputStream(stream);
            out.writeObject(obj);
            out.flush();
            byte[] byteArray = stream.toByteArray();
            Utils.writeContents(outFile, byteArray);
            out.close();
        } catch (IOException excp) {
            System.out.println("Commit doesn't exist");
        }
    }

    public static Commit deserializeCommit(File commit) {
        Commit obj;
        File inFile = commit;
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
            obj = (Commit) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;

    }


    public String getLogMessage() {
        return _logMessage;
    }

    public String getCommitDate() {
        return _commitDate;
    }

    public String getParentCommit() {
        return _parentCommit;
    }

    public String getHashId() {
        return hashID;
    }

}
