package scalive.client;

import scala.tools.jline_embedded.console.ConsoleReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Input: cursor buffer
 *
 * Output: cursor candidate1 candidate2 candidate3...
 */
class ClientCompleter {
    static void setup(Socket socket, ConsoleReader reader) throws Exception {
        final InputStream in  = socket.getInputStream();
        final OutputStream out = socket.getOutputStream();

        final BufferedReader b = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        reader.addCompleter((String buffer, int cursor, List<CharSequence> candidates) -> {
            try {
                String request = String.format("%d %s\n", cursor, buffer);
                out.write(request.getBytes(StandardCharsets.UTF_8));
                out.flush();

                String response = b.readLine();

                // socket closed; the client should exit
                if (response == null) return -1;

                String[] args = response.split(" ");

                int c = Integer.parseInt(args[0]);

                for (int i = 1; i < args.length; i++) {
                    String candidate = args[i];
                    candidates.add(candidate);
                }

                return c;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
