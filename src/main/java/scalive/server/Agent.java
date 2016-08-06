package scalive.server;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;

public class Agent {
    /**
     * @param agentArgs <jarSearchDir1>[<File.pathSeparator><jarSearchDir2>...] <port for REPL and completer>
     *
     * <pre>{@code
     * jarSearchDir/
     *   scalive.jar
     *
     *   scala-library-2.11.0.jar
     *   scala-compiler-2.11.0.jar
     *   scala-reflect-2.11.0.jar
     *
     *   [Other Scala versions]
     * }</pre>
     */
    public static void agentmain(String agentArgs, Instrumentation inst) throws IOException {
        final String[] args          = agentArgs.split(" ");
        final String[] jarSearchDirs = args[0].split(File.pathSeparator);
        final int      port          = Integer.parseInt(args[1]);

        // Need to open server socket first before creating the thread below and returning,
        // otherwise the client won't be able to connect
        final ServerSocket serverSocket = Server.open(port);

        // Need to start a new thread because:
        // - The server is blocking for connections
        // - VirtualMachine#loadAgent at the client does not return until this agentmain method returns
        // - The client only connects to the server after VirtualMachine#loadAgent returns
        new Thread(Agent.class.getName() + "-Server") {
            @Override
            public void run() {
                try {
                    Server.run(serverSocket, jarSearchDirs);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}
