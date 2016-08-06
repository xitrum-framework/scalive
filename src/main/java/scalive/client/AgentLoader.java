package scalive.client;

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import scalive.Classpath;
import scalive.Log;
import scalive.Net;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

public class AgentLoader {
    /**
     * @param args <jarSearchDir1>[<File.pathSeparator><jarSearchDir2>...] [pid]
     *
     * <pre>{@code
     * jarSearchDir/
     *   scalive.jar
     *   jline-2.14.2.jar
     *
     *   scala-library-2.11.0.jar
     *   scala-compiler-2.11.0.jar
     *   scala-reflect-2.11.0.jar
     *
     *   [Other Scala versions]
     * }</pre>
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1 && args.length != 2) {
            Log.log("Arguments: <jarSearchDir1>[<File.pathSeparator><jarSearchDir2>...] [pid]");
            return;
        }

        addToolsDotJarToClasspath();

        if (args.length == 1) {
            listJvmProcesses();
            return;
        }

        String jarSearchDirs = args[0];
        String pid           = args[1];
        int    port          = loadAgent(jarSearchDirs, pid);
        Client.run(port);
    }

    /**
     * com.sun.tools.attach.VirtualMachine is in tools.jar, which is not in
     * classpath by default:
     *
     * <pre>{@code
     * jdk/
     *   bin/
     *     java
     *     javac
     *   jre/
     *     java
     *   lib/
     *     tools.jar
     * }</pre>
     */
    private static void addToolsDotJarToClasspath() throws Exception {
        String path = System.getProperty("java.home") + "/../lib/tools.jar";
        Classpath.addPath((URLClassLoader) ClassLoader.getSystemClassLoader(), path);
    }

    private static void listJvmProcesses() {
        Log.logNoTag("JVM processes:");
        Log.logNoTag("#pid\tDisplay name");

        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            Log.logNoTag(vmd.id() + "\t" + vmd.displayName());
        }
    }

    private static int loadAgent(String jarSearchDirs, String pid) throws Exception {
        final String[]       ary      = jarSearchDirs.split(File.pathSeparator);
        final String         agentJar = Classpath.findJar(ary, "scalive");
        final VirtualMachine vm       = VirtualMachine.attach(pid);
        final int            port     = Net.getLocalFreePort();

        vm.loadAgent(agentJar, jarSearchDirs + " " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    vm.detach();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return port;
    }
}
