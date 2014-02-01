package scalive;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;
import java.net.Socket;

public class Agent {
    /**
     * @param agentArgs <jarpath> <server port>;
     * jarpath: absolute path to directory that contains scalive-agent.jar,
     * scala-library.jar, scala-compiler.jar, and scala-reflect.jar
     * @throws IOException
     */
    public static void agentmain(String agentArgs, Instrumentation inst) throws IOException {
        final String[] args    = agentArgs.split(" ");
        final String   jarpath = args[0];
        final int      port    = Integer.parseInt(args[1]);

        System.out.println("[Scalive] REPL server starts at port " + port);
        final ServerSocket server = new ServerSocket(port);

        // Need to start a new thread because:
        // - ServerSocket#accept blocks until a connection comes in
        // - VirtualMachine#loadAgent at the client does not return until this
        //   agentmain method returns
        // - The client only connects to the server after VirtualMachine#loadAgent
        //   returns
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket client = server.accept();  // Block until a connection comes in
                    Server.serve(client, jarpath);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        server.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
