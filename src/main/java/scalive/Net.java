package scalive;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Net {
    // After this time, the REPL and completer connections should be closed,
    // to avoid blocking socket reads to infinitely block threads created by Scalive in target process
    private static final int LONG_INACTIVITY = (int) TimeUnit.HOURS.toMillis(1);

    public static final InetAddress LOCALHOST = getLocalHostAddress();

    public static int getLocalFreePort() throws Exception {
        ServerSocket server = new ServerSocket(0, 0, Net.LOCALHOST);
        int          port   = server.getLocalPort();
        server.close();
        return port;
    }

    /**
     * {@link SocketTimeoutException} will be thrown if there's no activity for a long time.
     * This is to avoid blocking reads to block threads infinitely, causing leaks in the target process.
     */
    public static void throwSocketTimeoutExceptionForLongInactivity(Socket socket) throws SocketException {
        socket.setSoTimeout(LONG_INACTIVITY);
    }

    /**
     * Use socket closing as a way to notify/cleanup socket blocking read.
     * The sockets are closed in the order they are given.
     */
    public static Runnable getSocketCleaner(final Socket... sockets) {
        return () -> {
            for (Socket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        };
    }

    private static InetAddress getLocalHostAddress() {
        try {
            return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
