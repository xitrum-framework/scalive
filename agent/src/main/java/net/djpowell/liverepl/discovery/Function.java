package net.djpowell.liverepl.discovery;

public interface Function<R, A> {

    R invoke (A arg);

}
