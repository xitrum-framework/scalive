package scalive.client;

import jline.console.ConsoleReader;

import scalive.Log;
import scalive.Net;

import java.net.Socket;

class Client {
    static void run(int port) throws Exception {
        Log.log("Attach to target process at port " + port);
        final Socket replSocket      = new Socket(Net.LOCALHOST, port);
        final Socket completerSocket = new Socket(Net.LOCALHOST, port);

        // Try to notify the target process to clean up when the client is terminated
        final Runnable socketCleaner = Net.getSocketCleaner(replSocket, completerSocket);
        Runtime.getRuntime().addShutdownHook(new Thread(Client.class.getName() + "-ShutdownHook") {
            @Override
            public void run() {
                socketCleaner.run();
            }
        });

        ConsoleReader reader = new ConsoleReader(System.in, System.out);
        Completer.setup(completerSocket, reader);
        Repl.run(replSocket, reader);
    }
}
