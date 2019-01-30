package scalive.server;

import scala.tools.nsc.interpreter.Completion;
import scala.tools.nsc.interpreter.ILoop;
import scala.tools.nsc.interpreter.IMain;
import scala.tools.nsc.interpreter.PresentationCompilerCompleter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

class ILoopWithCompletion extends ILoop {
    private Completion completion = null;

    ILoopWithCompletion(InputStream in, OutputStream out) {
        super(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)), new PrintWriter(out));
    }

    Completion getCompletion() {
        return completion;
    }

    @Override
    public void createInterpreter() {
        super.createInterpreter();
        IMain intp = intp();
        completion = new PresentationCompilerCompleter(intp);
    }
}
