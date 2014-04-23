package scalive;

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Iterator;

public class AgentLoader {
    /**
     * @param args <jarpath1>[<File.pathSeparator><jarpath2>...] [pid]
     *
     * jarpath is the absolute path this directory:
     *
     * {{{
     * jarpath/
     *   scalive.jar
     *
     *   scala-library-2.11.0.jar
     *   scala-compiler-2.11.0.jar
     *   scala-reflect-2.11.0.jar
     *
     *   [Other Scala versions]
     * }}}
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1 && args.length != 2) {
            System.out.println("Arguments: <jarpath1>[<File.pathSeparator><jarpath2>...] [pid]");
            return;
        }

        addToolsDotJarToClasspath();

        if (args.length == 1) {
            listJvmProcesses();
            return;
        }

        String jarpaths = args[0];
        String pid      = args[1];
        loadAgent(jarpaths, pid);
    }

    /**
     * com.sun.tools.attach.VirtualMachine is in tools.jar, which is not in
     * classpath by default:
     *
     * {{{
     * jdk/
     *   bin/
     *     java
     *     javac
     *   jre/
     *     java
     *   lib/
     *     tools.jar
     * }}}
     */
    private static void addToolsDotJarToClasspath() throws Exception {
        String path = System.getProperty("java.home") + "/../lib/tools.jar";
        Classpath.addPath((URLClassLoader) ClassLoader.getSystemClassLoader(), path);
    }

    private static void listJvmProcesses() {
        System.out.println("JVM processes:");
        System.out.println("#pid\tDisplay name");

        Iterator<VirtualMachineDescriptor> it = VirtualMachine.list().iterator();
        while (it.hasNext()) {
            VirtualMachineDescriptor vmd = it.next();
            System.out.println(vmd.id() + "\t" + vmd.displayName());
        }
    }

    private static void loadAgent(String jarpaths, String pid) throws Exception {
        final String[]       ary      = jarpaths.split(File.pathSeparator);
        final String         agentJar = Classpath.findJar(ary, "scalive");
        final VirtualMachine vm       = VirtualMachine.attach(pid);
        final int            port     = Client.getFreePort();

        vm.loadAgent(agentJar, jarpaths + " " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override public void run() {
                try {
                    vm.detach();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Client.connectToRepl(port);
    }
}
