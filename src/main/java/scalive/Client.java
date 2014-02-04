package scalive;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    private static final InetAddress LOCALHOST = getLocalHostAddress();

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

    public static void connectToRepl(int port) throws Exception {
        final Socket       client = new Socket(LOCALHOST, port);
        final InputStream  in     = client.getInputStream();
        final OutputStream out    = client.getOutputStream();

        System.out.println("[Scalive] Attached to remote process at port " + port);

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

        boolean closed = false;
        while (!closed) {
            try {
                String line = System.console().readLine();
                out.write(line.getBytes());
                out.write('\n');
                out.flush();
            } catch (Exception e) {
                closed = true;
            }
        }

        client.close();
    }
}
