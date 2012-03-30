/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lightframework.mvc.gzip;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * <code>{@link GzipResponseStream}</code>
 *
 * @since 1.0.0
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

