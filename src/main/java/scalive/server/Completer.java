package scalive.server;

import scala.collection.Iterator;
import scala.collection.immutable.List;
import scala.tools.nsc.interpreter.Completion;
import scala.tools.nsc.interpreter.Completion.Candidates;

import scalive.Log;
import scalive.Net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @see scalive.client.Completer
 */
class Completer {
    static void run(
            Socket socket, ILoopWithCompletion iloop, Runnable socketCleaner
    ) throws IOException, InterruptedException {
        InputStream  in  = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        Net.throwSocketTimeoutExceptionForLongInactivity(socket);
        try {
            while (true) {
                // See throwSocketTimeoutExceptionForLongInactivity above
                String line = reader.readLine();

                // Socket closed
                if (line == null) break;

                int idx = line.indexOf(" ");
                String cursorString = line.substring(0, idx);
                int cursor = Integer.parseInt(cursorString);
                String buffer = line.substring(idx + 1);

                Completion completion = getCompletion(iloop);
                Candidates candidates = completion.completer().complete(buffer, cursor);

                out.write(("" + candidates.cursor()).getBytes("UTF-8"));

                List<String> list = candidates.candidates();
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    String candidate = it.next();
                    out.write(' ');
                    out.write(candidate.getBytes("UTF-8"));
                }

                out.write('\n');
                out.flush();
            }
        } catch (IOException e) {
            // Socket closed
        }

        socketCleaner.run();

        // Before logging this out, wait a litte for System.out to be restored back to the remote process
        Thread.sleep(1000);
        Log.log("Completer closed");
    }

    static private Completion getCompletion(ILoopWithCompletion iloop) {
        while (true) {
            // iloop may recreate completion as crash recovery, so do not cache it locally
            Completion completion = iloop.getCompletion();

            if (completion != null) return completion;

            // Wait for completion to be created by iloop
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
    }
}
