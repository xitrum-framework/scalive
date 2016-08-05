package scalive;

import jline.console.ConsoleReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    private static final InetAddress LOCALHOST    = getLocalHostAddress();
    private static final String      SCALA_PROMPT = "scala> ";  // Needs to match the prompt of REPL server

    private static InetAddress getLocalHostAddress() {
        try {
            return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    public static int getFreePort() throws Exception {
        ServerSocket server = new ServerSocket(0, 0, LOCALHOST);
        int          port   = server.getLocalPort();
        server.close();
        return port;
    }

    public static void connectToReplServer(int port) throws Exception {
        Socket socket = new Socket(LOCALHOST, port);
        System.out.println("[Scalive] Attached to remote process at port " + port);

        InputStream  sin  = socket.getInputStream();
        OutputStream sout = socket.getOutputStream();
        printServerOutput(sin);

        ConsoleReader reader = new ConsoleReader(System.in, System.out);
        reader.setPrompt(SCALA_PROMPT);

        boolean closed = false;
        while (!closed) {
            try {
                String line = reader.readLine();
                sout.write(line.getBytes());
                sout.write('\n');
                sout.flush();
            } catch (Exception e) {
                closed = true;
            }
        }

        socket.close();
    }

    private static void printServerOutput(final InputStream in) {
        new Thread(new Runnable() {
            @Override public void run() {
                InputStreamReader reader = new InputStreamReader(in);
                boolean           closed = false;
                while (!closed) {
                    try {
                        int i = reader.read();
                        closed = i < 0;
                        if (!closed) System.out.print((char) i);
                    } catch (Exception e) {
                        closed = true;
                    }
                }

                System.out.println("[Scalive] Connection to remote process closed");
                System.exit(0);
            }
        }).start();
    }
}
