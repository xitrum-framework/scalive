package scalive.client;

import jline.console.ConsoleReader;

import scalive.Log;
import scalive.Net;

import java.io.IOException;
import java.net.Socket;

class Client {
    static void run(int port) throws Exception {
        Log.log("Attach to remote process at port " + port);
        final Socket replSocket      = new Socket(Net.LOCALHOST, port);
        final Socket completerSocket = new Socket(Net.LOCALHOST, port);

        // Try to notify the remote process to clean up when the client
        // is suddenly terminated
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    replSocket.close();
                    completerSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ConsoleReader reader = new ConsoleReader(System.in, System.out);
        Completer.setup(completerSocket, reader);
        Repl.run(replSocket, reader);
    }
}
