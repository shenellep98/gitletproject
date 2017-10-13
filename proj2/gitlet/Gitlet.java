//need to import branch, staging, etc

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;

import static gitlet.Utils.readContents;
import static gitlet.Utils.sha1;



public class Gitlet implements Serializable {
    private String currentCommit;
    private String head;
    private ArrayList<String> branches;
    private HashMap<String, String> added;
    private HashMap<String, String> removed; //file names, not hashes
//head says what branch youre on (usually points to master), takes in a string - tag master
//master can also be a string - references a string

    public Gitlet() {
        added = new HashMap<String, String>();
        removed = new HashMap<String, String>();

    }


    public void init() {
        String parent = System.getProperty("user.dir");
        File exists = new File(parent, ".gitlet");
//        System.out.println(exists.exists());
//        System.out.println(exists.isDirectory());
        if (exists.exists() && exists.isDirectory()) {
            String msg = "A gitlet version-control system already exists in the current directory.";
            System.out.println(msg);
            return;
        } else {
            File gitletDir = new File(parent, ".gitlet");
            File branchDir = new File(gitletDir, "Branches");
            File commitDir = new File(gitletDir, "Commits");
            File blobsDir = new File(gitletDir, "Blobs");
            gitletDir.mkdir();
            branchDir.mkdir();
            commitDir.mkdir();
            blobsDir.mkdir();
            Commit initial = new Commit(); //no files, commit message is "inital commit"
            Branch master = new Branch("master", initial.getHashId());
            head = "master";
            branches = new ArrayList<>();
            branches.add("master");
            //Head.setcurrCommitHash(initial.get_HashId());//Master points to initial commit
            currentCommit = initial.getHashId(); //Master becomes the current branch
            initial.serializeCommit();
            master.serializeBranch("master");
            //should call the gitlet constructor
            //call the commit constructor (new commit with no files called initial commit)
            //needs branch master that points to initial and head that points to master
            //if check for mkdir (because its a boolean), throw error message
            //since that would mean that there is an existing directory already
        }
    }

    public void commit(String logMessage) {
        if (added.isEmpty() && removed.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (logMessage == null) {
            System.out.println("Please enter a commit message.");
            return;

        }
        String logMsg = logMessage;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        String commitDate = LocalDateTime.now().format(dtf);
        String author = System.getProperty("user.name");
        Branch current = Branch.deserializeBranch(head);
        String parentCommit = current.getCurrCommitHash();
        HashMap<String, String> newBlobs = new HashMap<String, String>();
        File pFile = new File(System.getProperty("user.dir") + "/.gitlet/Commits/" + parentCommit);
        Commit parent = Commit.deserializeCommit(pFile);
        if (parent.blobs != null) {
            HashMap<String, String> parentBlobs = parent.blobs;
            newBlobs.putAll(parentBlobs);
            if (!added.isEmpty()) {
                newBlobs.putAll(added);
            }
            if (!removed.isEmpty()) {
                removed.forEach((String key, String value) -> newBlobs.remove(key));
            }
        }
        if (parent.blobs == null && !added.isEmpty()) {
            newBlobs.putAll(added);
        }
        Commit add = new Commit(logMsg, commitDate, author, parentCommit, newBlobs);
        currentCommit = add.hashID;
        current.currCommitHash = currentCommit;
        current.serializeBranch(head);
        add.serializeCommit();
        parent.serializeCommit();
        added.clear();
        removed.clear();
    }

    public void branch(String name) {

        if (branches.contains(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch curr = Branch.deserializeBranch(head);
        Branch future = new Branch(name, curr.getCurrCommitHash());
        branches.add(name);
        future.serializeBranch(name);
        curr.serializeBranch(head);
// should be adding to the branches folder (as solid file)
    }



    public void globalLog() {
        File cFile = new File(System.getProperty("user.dir") + "/.gitlet/Commits");
        List<String> cFiles = Utils.plainFilenamesIn(cFile);
        for (String current: cFiles) {
            File currCom = new File(System.getProperty("user.dir") + "/.gitlet/Commits/" + current);
            Commit now = Commit.deserializeCommit(currCom);
            System.out.println("===");
            System.out.println("Commit " + now.getHashId());
            System.out.println(now.getCommitDate());
            System.out.println(now.getLogMessage());
            System.out.println();
            now.serializeCommit();
        }
    }

    public void find(String commitMessage) {
        File cDir = new File(System.getProperty("user.dir") + "/.gitlet/Commits");
        List<String> cFiles = Utils.plainFilenamesIn(cDir);
        boolean contains = false;
        for (String current: cFiles) {
            File nowDir = new File(System.getProperty("user.dir") + "/.gitlet/Commits/" + current);
            Commit now = Commit.deserializeCommit(nowDir);
            if (now.getLogMessage().equals(commitMessage)) {
                contains = true;
                System.out.println(now.getHashId());
            }
            now.serializeCommit();
        }
        if (!contains) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void status() {
        System.out.println("=== Branches ===");
        Collections.sort(branches);
        for (String current : branches) {
            if (current.equals(head)) {
                System.out.println("*" + current);
            } else {
                System.out.println(current);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        ArrayList<String> addedVals = new ArrayList<String>();
        added.forEach((k, v) -> addedVals.add(k));
        Collections.sort(addedVals);
        for (String current: addedVals) {
            System.out.println(current);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        ArrayList<String> removedFiles = new ArrayList<String>();
        File uFile = new File(System.getProperty("user.dir"));
        removed.forEach((k, v) -> removedFiles.add(k));
        Collections.sort(removedFiles);
        for (String current: removedFiles) {
            System.out.println(current);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void reset(String id) {
        String parent = System.getProperty("user.dir");
        List<String> commitFiles = Utils.plainFilenamesIn(new File(parent + "/.gitlet/Commits"));
        if (id.length() < 40) {
            for (String commitName : commitFiles) {
                if (commitName.substring(0, id.length()).equals(id)) {
                    id = commitName;
                }
            }
        }
        Collection<String> workFiles = Utils.plainFilenamesIn(new File(parent));
        Commit givenCommit = Commit.deserializeCommit(new File(parent + "/.gitlet/Commits/" + id));
        if (givenCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Branch current = Branch.deserializeBranch(head);
        String cID = current.currCommitHash;
        Commit currCommit = Commit.deserializeCommit(new File(parent + "/.gitlet/Commits/" + cID));
        HashMap<String, String> gvnBlbs = givenCommit.blobs;
        HashMap<String, String> currBlobs = currCommit.blobs;
        if (!commitFiles.contains(id)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (gvnBlbs == null) {
            for (String file : workFiles) {
                Utils.restrictedDelete(new File(parent + "/" + file));
            }

        }
        if (gvnBlbs != null) {
            for (String file : workFiles) {
                if (!currBlobs.containsKey(file)
                        && !added.containsKey(file)) {
                    String m = "There is an untracked file in the way; delete it or add it first.";
                    System.out.println(m);
                    return;
                }
                Utils.restrictedDelete(new File(parent + "/" + file));
            }
            for (String file : gvnBlbs.keySet()) {
                checkout(id, file);
            }
        }
        Branch moveOver = Branch.deserializeBranch(head);
        moveOver.currCommitHash = id;
        moveOver.serializeBranch(head);
        added.clear();
        removed.clear();
    }

    public boolean fail(String branchName) {
        if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        if (!added.isEmpty() || !removed.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        if (branchName.equals(head)) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }
        return false;
    }

    public boolean untracked(List<String> workFiles, HashMap<String, String> currBlobs) {
        for (String file : workFiles) {
            if (!currBlobs.containsKey(file)) {
                String msg = "There is an untracked file in the way; delete it or add it first.";
                System.out.println(msg);
                return true;
            }
        }
        return false;

    }

    public boolean given(ArrayList<HashMap<String, String>> info, Commit gHS, String pGB) {
        HashMap<String, String> gvnBlbs = info.get(0);
        HashMap<String, String> splitBlobs = info.get(1);
        HashMap<String, String> currBlobs = info.get(2);
        for (Map.Entry<String, String> entry: gvnBlbs.entrySet()) {
            if (!splitBlobs.containsKey(entry.getKey()) && !currBlobs.containsKey(entry.getKey())) {
                added.put(entry.getKey(), gvnBlbs.get(entry.getKey()));
                checkout(gHS.hashID, entry.getKey());
            }
            if (!splitBlobs.containsKey(entry.getKey()) && currBlobs.containsKey(entry.getKey())
                    && !currBlobs.containsValue(entry.getValue())) {
                File workingFile = new File(System.getProperty("user.dir") + "/" + entry.getKey());
                Blob givenBlob = Blob.deserializeBlob(new File(
                        pGB + gvnBlbs.get(entry.getKey())));
                String given2 = new String();
                if (givenBlob == null) {
                    given2 = "";
                }
                if (givenBlob != null) {
                    byte[] given = givenBlob.contents;
                    given2 = new String(given);
                }
                String current2 = new String();
                Blob currBlob = Blob.deserializeBlob(new File(pGB + currBlobs.get(entry.getKey())));
                if (currBlob == null) {
                    current2 = "";
                }
                if (currBlob != null) {
                    byte[] current = currBlob.contents;
                    current2 = new String(current);
                }
                String merged = "<<<<<<< HEAD\n" + current2 + "=======\n" + given2 + ">>>>>>>\n";
                byte[] addin = merged.getBytes();
                Utils.writeContents(workingFile, addin);
                System.out.println("Encountered a merge conflict.");
                removed.clear();
                return true;

            }
        }
        return false;
    }

    public boolean split(ArrayList<HashMap<String, String>> info, String givenStarter, String pGB) {
        HashMap<String, String> gvnBlbs = info.get(0);
        HashMap<String, String> splitBlobs = info.get(1);
        HashMap<String, String> currBlobs = info.get(2);
        for (Map.Entry<String, String> entry : splitBlobs.entrySet()) {
            if (currBlobs.containsValue(entry.getValue()) && gvnBlbs.containsKey(entry.getKey())
                    && !gvnBlbs.containsValue(entry.getValue())) {
                added.put(entry.getKey(), gvnBlbs.get(entry.getKey()));
            }
            if (currBlobs.containsKey(entry.getKey()) && currBlobs.containsValue(entry.getValue())
                    && !gvnBlbs.containsKey(entry.getKey())) {
                Utils.restrictedDelete(new File(
                        System.getProperty("user.dir") + "/" + entry.getKey()));
                added.remove(entry.getKey());
                removed.put(entry.getKey(), entry.getValue());
            }
            if (gvnBlbs.containsKey(entry.getKey()) && !gvnBlbs.containsValue(entry.getValue())
                    && currBlobs.containsValue(entry.getValue())
                    && currBlobs.containsKey(entry.getKey())) {
                checkout(givenStarter, entry.getKey());
                added.put(entry.getKey(), gvnBlbs.get(entry.getKey()));
            }
            if ((currBlobs.containsKey(entry.getKey()) && !currBlobs.containsValue(entry.getValue())
                    && gvnBlbs.containsKey(entry.getKey())
                    && !gvnBlbs.containsValue(entry.getValue()))
                    || (currBlobs.containsKey(entry.getKey())
                    && !currBlobs.containsValue(entry.getValue())
                    && !gvnBlbs.containsKey(entry.getKey()))
                    || (gvnBlbs.containsKey(entry.getKey())
                    && !gvnBlbs.containsValue(entry.getValue())
                    && !currBlobs.containsKey(entry.getKey()))) {
                File workingFile = new File(System.getProperty("user.dir") + "/" + entry.getKey());
                Blob givenBlob = Blob.deserializeBlob(new File(
                        pGB + gvnBlbs.get(entry.getKey())));
                String given2 = new String();
                if (givenBlob == null) {
                    given2 = "";
                }
                if (givenBlob != null) {
                    byte[] given = givenBlob.contents;
                    given2 = new String(given);
                }
                String current2 = new String();
                Blob currBlob = Blob.deserializeBlob(new File(pGB + currBlobs.get(entry.getKey())));
                if (currBlob == null) {
                    current2 = "";
                }
                if (currBlob != null) {
                    byte[] current = currBlob.contents;
                    current2 = new String(current);
                }
                String merged = "<<<<<<< HEAD\n" + current2 + "=======\n" + given2 + ">>>>>>>\n";
                byte[] addin = merged.getBytes();
                Utils.writeContents(workingFile, addin);
                System.out.println("Encountered a merge conflict.");
                removed.clear();
                return true;

            }
        }
        return false;
    }

    public void merge(String branchName) {
        if (fail(branchName)) {
            return;
        }
        String pGC = System.getProperty("user.dir") + "/.gitlet/Commits/";
        String pGB = System.getProperty("user.dir") + "/.gitlet/Blobs/";
        String givenStarter = Branch.deserializeBranch(branchName).getCurrCommitHash();
        Commit givenHead = Commit.deserializeCommit(new File(pGC + givenStarter));
        Commit givenHeadSnapshot = givenHead;
        ArrayList<String> givenAncestors = new ArrayList<>();
        String currentStarter = Branch.deserializeBranch(head).currCommitHash;
        Commit currentHead = Commit.deserializeCommit(new File(pGC + currentStarter));
        Commit currentHeadSnapshot = currentHead;
        ArrayList<String> currentAncestors = new ArrayList<>();
        while (givenHead != null) {
            givenAncestors.add(givenHead.getHashId());
            givenHead = Commit.deserializeCommit(new File(pGC + givenHead.getParentCommit()));
            givenAncestors.remove(givenStarter);
        }
        while (currentHead != null) {
            currentAncestors.add(currentHead.getHashId());
            currentHead = Commit.deserializeCommit(new File(pGC + currentHead.getParentCommit()));
            currentAncestors.remove(currentStarter);
        }
        String splitId = null;
        for (String parent : givenAncestors) {
            if (currentAncestors.contains(parent)) {
                splitId = parent;
                break;
            }
        }
        Commit splitCommit = Commit.deserializeCommit(new File(pGC + splitId));
        if (splitCommit.getLogMessage().equals("initial commit")) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (givenAncestors.contains(currentHeadSnapshot.hashID)) {
            Branch moveOver = Branch.deserializeBranch(head);
            moveOver.name = branchName;
            moveOver.currCommitHash = givenStarter;
            moveOver.serializeBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        HashMap<String, String> gvnBlbs = Commit.deserializeCommit(
                new File(pGC + givenStarter)).blobs;
        HashMap<String, String> currBlobs = Commit.deserializeCommit(
                new File(pGC + currentStarter)).blobs;
        HashMap<String, String> splitBlobs = splitCommit.blobs;
        List<String> workFiles = Utils.plainFilenamesIn(new File(System.getProperty("user.dir")));
        if (untracked(workFiles, currBlobs)) {
            return;
        }
        ArrayList<HashMap<String, String>> info = new ArrayList<HashMap<String, String>>();
        info.add(gvnBlbs);
        info.add(splitBlobs);
        info.add(currBlobs);
        if (given(info, givenHeadSnapshot, pGB)) {
            return;
        }
        if (split(info, givenStarter, pGB)) {
            return;
        }
        commit("Merged " + head + " with " + branchName + ".");
    }

    //make sure to deserialize gitlet every time.
    //snapshot of the file given
    public void add(String fileName) {
        String p = System.getProperty("user.dir");
        if (!Utils.plainFilenamesIn(new File(p)).contains(fileName)) {
            System.out.println("File does not exist.");
            return;
        }
        if (removed.containsKey(fileName)) {
            removed.remove(fileName);
            return;
        }
        String cch = Branch.deserializeBranch(head).currCommitHash;
        Commit current = Commit.deserializeCommit(new File(p + "/.gitlet/Commits/" + cch));
        if (current.blobs != null) {
            if (current.blobs.containsKey(fileName)) {
                String toAdd = sha1(readContents(new File(p + "/" + fileName)));
                if (toAdd.equals(current.blobs.get(fileName))) {
                    return;
                }
            }

        }
        Blob x = new Blob(fileName);
        added.put(fileName, x.HashID);
    }

    //adds copy of file to staging area
    public void rm(String fileName) {
        String pGC = System.getProperty("user.dir") + "/.gitlet/Commits/";
        File commitPath = new File(pGC + Branch.deserializeBranch(head).currCommitHash);
        Commit current = Commit.deserializeCommit(commitPath);
        List<String> workFiles = Utils.plainFilenamesIn(new File(System.getProperty("user.dir")));
        if (current.blobs == null && added.isEmpty()) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (current.blobs != null && !current.blobs.containsKey(fileName)
                && !added.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if ((!added.isEmpty()) && added.containsKey(fileName)) {
            added.remove(fileName);
        }
        if (current.blobs != null && current.blobs.containsKey(fileName)) {
            Utils.restrictedDelete(new File(System.getProperty("user.dir") + "/" + fileName));
            removed.put(fileName, current.blobs.get(fileName));
        }



    }

    public void log() {
        String pGC = System.getProperty("user.dir") + "/.gitlet/Commits/";
        File commitPath = new File(pGC + Branch.deserializeBranch(head).currCommitHash);
        Commit hed = Commit.deserializeCommit(commitPath);

        while (hed != null) {
            System.out.println("===");
            System.out.println("Commit " + hed.getHashId());
            System.out.println(hed.getCommitDate());
            System.out.println(hed.getLogMessage());
            System.out.println();
            commitPath = new File(pGC + hed.getParentCommit());
            hed = Commit.deserializeCommit(commitPath);
        }



    }
    public void checkout(String name) {
        String currentID = Branch.deserializeBranch(head).currCommitHash;
        checkout(currentID, name);

    }
    public void checkout(String id, String name) {
        String pGC = System.getProperty("user.dir") + "/.gitlet/Commits/";
        if (id.length() < 40) {
            List<String> commitFiles = Utils.plainFilenamesIn(new File(pGC));
            for (String commitName : commitFiles) {
                if (commitName.substring(0, id.length()).equals(id)) {
                    id = commitName;
                }
            }
        }
        if (!new File(pGC + id).exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit toPull = Commit.deserializeCommit(new File(pGC + id));
        HashMap<String, String> toPullBlobs = toPull.blobs;
        if (!toPullBlobs.containsKey(name)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String p = System.getProperty("user.dir");
        File checkout = new File(p + "/" + name);
        Blob fleVer = Blob.deserializeBlob(new File(p + "/.gitlet/Blobs/" + toPullBlobs.get(name)));
        byte[] newContents = fleVer.contents;
        Utils.writeContents(checkout, newContents);
        toPull.serializeCommit();
        fleVer.serializeBlob(toPullBlobs.get(name));
    }

    public void branchCheckout(String branchName) {
        if (!branches.contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchName.equals(head)) {
            System.out.println("No need to check out the current branch.");
            return;
        }
        String pGC = System.getProperty("user.dir") + "/.gitlet/Commits/";
        Branch givenBranch = Branch.deserializeBranch(branchName);
        Commit givenCommit = Commit.deserializeCommit(new File(pGC + givenBranch.currCommitHash));
        Branch currentBranch = Branch.deserializeBranch(head);
        Commit currCommit = Commit.deserializeCommit(new File(pGC + currentBranch.currCommitHash));
        List<String> workFiles = Utils.plainFilenamesIn(new File(System.getProperty("user.dir")));
        if (givenCommit.blobs == null || currCommit.blobs == null) {
            for (String files : workFiles) {
                if (currCommit.blobs == null && givenCommit.blobs != null) {
                    if (givenCommit.blobs.containsKey(files)) {
                        String msg;
                        msg = "There is an untracked file in the way; delete it or add it first.";
                        System.out.println(msg);
                        return;
                    }
                }
                Utils.restrictedDelete(new File(System.getProperty("user.dir") + "/" + files));
            }



        }
        if (givenCommit.blobs != null && currCommit.blobs != null) {
            for (String file : workFiles) {
                if (givenCommit.blobs.containsKey(file) && !givenCommit.blobs.isEmpty()
                        && !currCommit.blobs.containsKey(file) && !added.containsKey(file)) {
                    String mg = "There is an untracked file in the way; delete it or add it first.";
                    System.out.println(mg);
                    return;
                }
                Utils.restrictedDelete(new File(System.getProperty("user.dir") + "/" + file));
            }
        }
        if (givenCommit.blobs != null) {
            for (String fileName : givenCommit.blobs.keySet()) {
                checkout(givenBranch.currCommitHash, fileName);
            }
        }
        currentBranch.serializeBranch(currentBranch.getName());
        head = branchName;
        added.clear();
        removed.clear();
        givenBranch.serializeBranch(head);
        givenCommit.serializeCommit();
        currCommit.serializeCommit();
    }

    public void rmBranch(String branchName) {
        if (head.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        } else if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else {
            branches.remove(branchName);
            return;
        }
    }

    public void serializeGitlet() {
        Gitlet obj = this;
        String child = ".gitlet/Gitlet";
        File outFile = new File(System.getProperty(new File(".").getAbsolutePath()), child);
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
            System.out.println("Gitlet doesn't exist");
        }
    }

    public static Gitlet deserializeGitlet() {
        Gitlet obj;
        String child = ".gitlet/Gitlet";
        File inFile = new File(System.getProperty(new File(".").getAbsolutePath()), child);
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
            obj = (Gitlet) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;

    }
}
