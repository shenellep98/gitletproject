/**
 * Created by Dipra on 7/12/2017.
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

import static gitlet.Utils.readContents;
import static gitlet.Utils.sha1;

public class Blob implements Serializable {
    private String name;
    protected byte[] contents;
    protected String HashID;
    //name should be the SHA Hash,
    //contents needs to be serializable
    //write to a file using the i/o class java.io.ObjectOutputStream and read back (and deserialize)
    //with java.io.ObjectInputStream.
    //stick to bytearrays because we're looking at files

    public Blob(String filename) {
        String temp = System.getProperty("user.dir");
        temp = temp + "/" + filename;
        //creates a string that represents the file path
        File temp2 = new File(temp);
        //makes temp filepath into an actual file
        //checks if this file doesn't exist in the current directory
        if (!temp2.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        this.contents = readContents(temp2);
        //convert contents of the file into a byte array
        HashID = sha1(contents);
        //hash the contents
        String check = temp + "/.gitlet/Blobs/" + HashID;
        File tempcheck = new File(check);
        if (tempcheck.exists()) {
            return;
        }
        //check if File with the contents already exists in the given blob directory
        this.name = filename;
        this.serializeBlob(HashID);

        //getAbsoluteFile -- takes in a path name, use toPath()
        //proposed: takes in a file name and then converts into output stream
        //then it packages the file into a blob, assists with breaking down the file
    }
    public void serializeBlob(String hashID) {
        Blob obj = this;
        String child = ".gitlet/Blobs/" + hashID;
        File oFile = new File(System.getProperty(new File(".").getAbsolutePath()), child);
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutput out = null;
            out = new ObjectOutputStream(stream);
            out.writeObject(obj);
            out.flush();
            byte[] byteArray = stream.toByteArray();
            Utils.writeContents(oFile, byteArray);
            out.close();
        } catch (IOException excp) {
            System.out.println("Blob doesn't exist");
        }
    }

    public static Blob deserializeBlob(File blob) {
        Blob obj;
        File pwd = blob;
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(pwd));
            obj = (Blob) inp.readObject();
            inp.close();

        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;



    }
    // we need to save the serializations; they will just be written to a file.
    // FILES are needed for status, not blobs



}
