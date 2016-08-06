package scalive.client;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

class Repl {
    static void run(Socket socket, final ConsoleReader reader) throws IOException {
        final InputStream  in  = socket.getInputStream();
        final OutputStream out = socket.getOutputStream();

        new Thread(Repl.class.getName() + "-printServerOutput") {
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
        // Need to set, even to empty, otherwise JLine doesn't work well
        reader.setPrompt("");

        while (true) {
            // Read
            String line = reader.readLine();
            if (line == null) break;

            // Evaluate
            try {
                out.write(line.getBytes("UTF-8"));
                out.write('\n');
                out.flush();
            } catch (IOException e) {
                // Socket closed
                break;
            }
        }
    }

    private static void printServerOutput(InputStream in) throws UnsupportedEncodingException {
        InputStreamReader reader = new InputStreamReader(in, "UTF-8");
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
