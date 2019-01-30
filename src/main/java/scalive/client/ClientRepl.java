package scalive.client;

import scala.tools.jline_embedded.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class ClientRepl {
    static void run(Socket socket, final ConsoleReader reader) throws IOException {
        final InputStream  in  = socket.getInputStream();
        final OutputStream out = socket.getOutputStream();

        new Thread(ClientRepl.class.getName() + "-printServerOutput") {
            @Override
            public void run() {
                try {
                    printServerOutput(in);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        readLocalInput(reader, out);
    }

    private static void readLocalInput(ConsoleReader reader, OutputStream out) throws IOException {
        while (true) {
            // Read
            String line = reader.readLine();
            if (line == null) break;

            // Evaluate
            try {
                out.write(line.getBytes(StandardCharsets.UTF_8));
                out.write('\n');
                out.flush();
            } catch (IOException e) {
                // Socket closed
                break;
            }
        }
    }

    private static void printServerOutput(InputStream in) {
        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        while (true) {
            int i;
            try {
                i = reader.read();
            } catch (IOException e) {
                // Socket closed
                break;
            }
            if (i < 0) break;

            System.out.print((char) i);
            System.out.flush();
        }

        // The loop above is broken when REPL is closed by the target process; exit now
        System.exit(0);
    }
}
