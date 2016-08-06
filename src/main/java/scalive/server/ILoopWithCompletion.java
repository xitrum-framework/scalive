package scalive.server;

import scala.tools.nsc.interpreter.Completion;
import scala.tools.nsc.interpreter.ILoop;
import scala.tools.nsc.interpreter.IMain;
import scala.tools.nsc.interpreter.JLineCompletion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

class ILoopWithCompletion extends ILoop {
    private Completion completion = null;

    ILoopWithCompletion(InputStream in, OutputStream out) throws UnsupportedEncodingException {
        super(new BufferedReader(new InputStreamReader(in, "UTF-8")), new PrintWriter(out));
    }

    Completion getCompletion() {
        return completion;
    }

    @Override
    public void createInterpreter() {
        super.createInterpreter();
        IMain intp = intp();
        completion = new JLineCompletion(intp);
    }
}
