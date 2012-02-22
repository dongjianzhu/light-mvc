/**
 * This file created at 2010-11-28.
 *
 * Copyright (c) 2002-2010 Bingosoft, Inc. All rights reserved.
 */
package org.lightframework.mvc.gzip;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * <code>{@link GzipResponseStream}</code>
 *
 * TODO : document me
 *
 * @author User
 */
public class GzipResponseStream extends ServletOutputStream {
    private ByteArrayOutputStream baos;
    private GZIPOutputStream gzipStream;
    private boolean closed;
    private HttpServletResponse response;
    private ServletOutputStream output;

    public GzipResponseStream(HttpServletResponse response) throws IOException {
        super();
        closed = false;
        this.response = response;
        this.output = response.getOutputStream();
        baos = new ByteArrayOutputStream();
        gzipStream = new GZIPOutputStream(baos);
    }

    public void close() throws IOException {
        if (closed) {
            throw new IOException("This output stream has already been closed");
        }

        gzipStream.finish();
        byte[] bytes = baos.toByteArray();

        response.addHeader("Content-Length", Integer.toString(bytes.length));
        response.addHeader("Content-Encoding", "gzip");

        output.write(bytes);
        output.flush();
        output.close();

        closed = true;
    }

    public void flush() throws IOException {
        if (closed) {
            throw new IOException("Cannot flush a closed output stream");
        }

        gzipStream.flush();
    }

    public void write(int b) throws IOException {
        if (closed) {
            throw new IOException("Cannot write to a closed output stream");
        }

        gzipStream.write(b);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        if (closed) {
            throw new IOException("Cannot write to a closed output stream");
        }

        gzipStream.write(b, off, len);
    }
}
