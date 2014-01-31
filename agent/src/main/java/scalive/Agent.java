package scalive;

import java.lang.instrument.Instrumentation;

public class Agent {
    /**
     * @param agentArgs See Server.main
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        String[] args    = agentArgs.split(" ");
        String   jarpath = args[0];

        // Add scala-library.jar immediately so that we can switch to Scala code
        // (Server.main)
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Classpath.findAndAddJar(cl, jarpath, "scala-library");
        Classpath.findAndAddJar(cl, jarpath, "scala-compiler");
        Classpath.findAndAddJar(cl, jarpath, "scala-reflect");

        Server.main(args);
    }
}
