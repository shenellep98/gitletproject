package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.plainFilenamesIn;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */

public class Main {

    private static Gitlet currGitlet;
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */

    public static void noArgs() {
        System.out.println("Please enter a command");
        System.exit(0);
    }
    public static void incorrectOps() {
        System.out.print("Incorrect operands.");
        System.exit(0);
    }
    public static void gitInit() {
        currGitlet = new Gitlet();
        currGitlet.init();
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitExistCheck() {
        File pwd = new File(System.getProperty("user.dir") + "/.gitlet");
        List filesinDir = plainFilenamesIn(pwd);
        if (!filesinDir.contains("Gitlet")) {
            System.out.println("Not in an intialized gitlet directory.");
            System.exit(0);
        }
    }

    public static void gitAdd(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.add(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitCommit(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.commit(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitRm(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.rm(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitLog(int arglength) {
        if (arglength != 1) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.log();
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitGlobalLog(int arglength) {
        if (arglength != 1) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.globalLog();
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitFind(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.find(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitStatus(int arglength) {
        if (arglength != 1) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.status();
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitBranchCheckout(String arg) {
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.branchCheckout(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitCheckout(String arg) {
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.checkout(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitCheckout2(String arg1, String arg2) {
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.checkout(arg1, arg2);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitBranch(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.branch(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitRmBranch(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.rmBranch(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitReset(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.reset(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void gitMerge(String arg, int arglength) {
        if (arglength != 2) {
            incorrectOps();
        }
        currGitlet = Gitlet.deserializeGitlet();
        currGitlet.merge(arg);
        currGitlet.serializeGitlet();
        System.exit(0);
    }

    public static void main(String... args) {
        if (args.length == 0) {
            noArgs();
        }
        if (args[0].equals("init")) {
            if (args.length > 1) {
                incorrectOps();
            }
            gitInit();
        }
        gitExistCheck();
        if (args[0].equals("add")) {
            gitAdd(args[1], args.length);
        }
        if (args[0].equals("commit")) {
            if (args[1].equals("")) {
                System.out.print("Please enter a commit message.");
                System.exit(0);
            }
            gitCommit(args[1], args.length);
        }
        if (args[0].equals("rm")) {
            gitRm(args[1], args.length);
        }
        if (args[0].equals("log")) {
            gitLog(args.length);
        }
        if (args[0].equals("global-log")) {
            gitGlobalLog(args.length);
        }
        if (args[0].equals("find")) {
            gitFind(args[1], args.length);
        }
        if (args[0].equals("status")) {
            gitStatus(args.length);
        }
        if (args[0].equals("checkout")) {
            if (args.length < 2 || args.length > 4) {
                incorrectOps();
            }
            if (args.length == 2) {
                gitBranchCheckout(args[1]);
            }
            if (args.length == 3) {
                if (args[1].equals("--")) {
                    gitCheckout(args[2]);
                } else {
                    incorrectOps();
                }
            }
            if (args.length == 4) {
                if (args[2].equals("--")) {
                    gitCheckout2(args[1], args[3]);
                } else {
                    incorrectOps();
                }
            }
        }
        if (args[0].equals("branch")) {
            gitBranch(args[1], args.length);
        }
        if (args[0].equals("rm-branch")) {
            gitRmBranch(args[1], args.length);
        }
        if (args[0].equals("reset")) {
            gitReset(args[1], args.length);
        }
        if (args[0].equals("merge")) {
            gitMerge(args[1], args.length);
        } else {
            System.out.print("No command with that name exists.");
            System.exit(0);
        }
    }
}
