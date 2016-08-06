package scalive;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Net {
    public static final InetAddress LOCALHOST = getLocalHostAddress();

    public static int getLocalFreePort() throws Exception {
        ServerSocket server = new ServerSocket(0, 0, Net.LOCALHOST);
        int          port   = server.getLocalPort();
        server.close();
        return port;
    }

    private static InetAddress getLocalHostAddress() {
        try {
            return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
