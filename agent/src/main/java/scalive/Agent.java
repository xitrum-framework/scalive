package scalive;

import java.lang.instrument.Instrumentation;

public class Agent {
    /**
     * @param agentArgs <jarpath> <server port> [class loader id]
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        String[] args    = agentArgs.split(" ");
        String   jarpath = args[0];

        // Add scala-library.jar immediately so that we can switch to Scala code
        Classpath.findAndAddJar(ClassLoader.getSystemClassLoader(), jarpath, "scala-library");

        Server.start(args);
    }
}
