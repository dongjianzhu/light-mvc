/**
 * This file created at 2010-11-28.
 *
 * Copyright (c) 2002-2010 Bingosoft, Inc. All rights reserved.
 */
package org.lightframework.mvc.gzip;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * <code>{@link GzipResponseWrapper}</code>
 *
 * TODO : document me
 *
 * @author User
 */
public class GzipResponseWrapper extends HttpServletResponseWrapper {
    protected HttpServletResponse response;
    protected ServletOutputStream stream;
    protected PrintWriter writer;

    public GzipResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    public void finishResponse() throws IOException {
        if (writer != null) {
            writer.close();
        } else {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private ServletOutputStream createOutputStream() throws IOException {
        return new GzipResponseStream(response);
    }

    public void flushBuffer() throws IOException {
        stream.flush();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called!");
        }

        if (stream == null)
            stream = createOutputStream();

        return stream;
    }

    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }

        if (stream == null) {
            stream = createOutputStream();
        }

        writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));

        return (writer);
    }

    public void setContentLength(int length) {
        // No operations.
    }
}
