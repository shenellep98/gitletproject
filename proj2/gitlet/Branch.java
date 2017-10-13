package gitlet;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Created by Dipra on 7/12/2017.
 */
public class Branch implements Serializable {
    protected String name;
    protected String currCommitHash; //this should be a pointer


    //makes branch with null everything except author, commit date and next
    public Branch(String name, String branchhash) {
        this.name = name;
        this.currCommitHash = branchhash;
    }

    public void serializeBranch(String bName) {
        Branch obj = this;
        File outFile = new File(System.getProperty("user.dir") + "/.gitlet/Branches/" + bName);
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
            System.out.println("Branch doesn't exist");
        }
    }


    public static Branch deserializeBranch(String bName) {
        Branch obj;
        File inFile = new File(System.getProperty("user.dir") + "/.gitlet/Branches/" + bName);
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
            obj = (Branch) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;

    }

    public String getName() {
        return name;
    }

    public String getCurrCommitHash() {
        return currCommitHash;
    }
}
