/*
 * Copyright (c) 2004-2005 SLF4J.ORG
 * Copyright (c) 2004-2005 QOS.ch
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute, and/or sell copies of  the Software, and to permit persons
 * to whom  the Software is furnished  to do so, provided  that the above
 * copyright notice(s) and this permission notice appear in all copies of
 * the  Software and  that both  the above  copyright notice(s)  and this
 * permission notice appear in supporting documentation.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR  A PARTICULAR PURPOSE AND NONINFRINGEMENT
 * OF  THIRD PARTY  RIGHTS. IN  NO EVENT  SHALL THE  COPYRIGHT  HOLDER OR
 * HOLDERS  INCLUDED IN  THIS  NOTICE BE  LIABLE  FOR ANY  CLAIM, OR  ANY
 * SPECIAL INDIRECT  OR CONSEQUENTIAL DAMAGES, OR  ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS  OF USE, DATA OR PROFITS, WHETHER  IN AN ACTION OF
 * CONTRACT, NEGLIGENCE  OR OTHER TORTIOUS  ACTION, ARISING OUT OF  OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * Except as  contained in  this notice, the  name of a  copyright holder
 * shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorization
 * of the copyright holder.
 *
 */

package org.slf4j.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * A simple (and direct) implementation that logs messages of level INFO or
 * higher on the console (<code>System.err<code>).
 * 
 * <p>The output includes the relative time in milliseconds, thread
 * name, the level, logger name, and the message followed by the line
 * separator for the host.  In log4j terms it amounts to the "%r [%t]
 * %level %logger - %m%n" pattern. </p>
 * 
 * <p>Sample output follows.</p>
<pre>
176 [main] INFO examples.Sort - Populating an array of 2 elements in reverse order.
225 [main] INFO examples.SortAlgo - Entered the sort method.
304 [main] INFO examples.SortAlgo - Dump of integer array:
317 [main] INFO examples.SortAlgo - Element [0] = 0
331 [main] INFO examples.SortAlgo - Element [1] = 1
343 [main] INFO examples.Sort - The next log statement should be an error message.
346 [main] ERROR examples.SortAlgo - Tried to dump an uninitialized array.
        at org.log4j.examples.SortAlgo.dump(SortAlgo.java:58)
        at org.log4j.examples.Sort.main(Sort.java:64)
467 [main] INFO  examples.Sort - Exiting main method.
</pre>
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class SimpleLogger extends MarkerIgnoringBase {

	private static final long serialVersionUID = -6560244151660620173L;

	/**
	 * Mark the time when this class gets loaded into memory.
	 */
	private static final SimpleDateFormat formtter = new SimpleDateFormat("HH:mm:ss.SSS");
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String TRACE = "TRACE";
	private static final String DEBUG = "DEBUG";
	private static final String INFO  = "INFO ";
	private static final String WARN  = "WARN ";
	private static final String ERROR = "ERROR";

	/**
	 * Package access allows only {@link SimpleLoggerFactory} to instantiate
	 * SimpleLogger instances.
	 */
	SimpleLogger(String name) {
		this.name = name;
	}

	public boolean isTraceEnabled() {
		return true;
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isInfoEnabled() {
		return true;
	}

	public boolean isWarnEnabled() {
		return true;
	}

	public boolean isErrorEnabled() {
		return true;
	}

	// ------------trace---------------
	public void trace(String msg) {
		log(TRACE, msg, null);
	}

	public void trace(String format, Object arg) {
		formatAndLog(TRACE, format, arg, null);
	}

	public void trace(String format, Object arg1, Object arg2) {
		formatAndLog(TRACE, format, arg1, arg2);
	}

	public void trace(String format, Object[] argArray) {
		formatAndLog(TRACE, format, argArray);
	}

	public void trace(String msg, Throwable t) {
		log(TRACE, msg, t);
	}

	// ------------debug---------------
	public void debug(String msg) {
		log(DEBUG, msg, null);
	}

	public void debug(String format, Object arg) {
		formatAndLog(DEBUG, format, arg, null);
	}

	public void debug(String format, Object arg1, Object arg2) {
		formatAndLog(DEBUG, format, arg1, arg2);
	}

	public void debug(String format, Object[] argArray) {
		formatAndLog(DEBUG, format, argArray);
	}

	public void debug(String msg, Throwable t) {
		log(DEBUG, msg, t);
	}

	// -------------info-------------------
	public void info(String msg) {
		log(INFO, msg, null);
	}

	public void info(String format, Object arg) {
		formatAndLog(INFO, format, arg, null);
	}

	public void info(String format, Object arg1, Object arg2) {
		formatAndLog(INFO, format, arg1, arg2);
	}

	public void info(String format, Object[] argArray) {
		formatAndLog(INFO, format, argArray);
	}

	public void info(String msg, Throwable t) {
		log(INFO, msg, t);
	}

	// -------------warn-------------------
	public void warn(String msg) {
		log(WARN, msg, null);
	}

	public void warn(String format, Object arg) {
		formatAndLog(WARN, format, arg, null);
	}

	public void warn(String format, Object arg1, Object arg2) {
		formatAndLog(WARN, format, arg1, arg2);
	}

	public void warn(String format, Object[] argArray) {
		formatAndLog(WARN, format, argArray);
	}

	public void warn(String msg, Throwable t) {
		log(WARN, msg, t);
	}

	// -------------error-------------------
	public void error(String msg) {
		log(ERROR, msg, null);
	}

	public void error(String format, Object arg) {
		formatAndLog(ERROR, format, arg, null);
	}

	public void error(String format, Object arg1, Object arg2) {
		formatAndLog(ERROR, format, arg1, arg2);
	}

	public void error(String format, Object[] argArray) {
		formatAndLog(ERROR, format, argArray);
	}

	public void error(String msg, Throwable t) {
		log(ERROR, msg, t);
	}

	private void log(String level, String message, Throwable t) {
		StringBuffer buf = new StringBuffer();

		buf.append(formtter.format(new Date()));

		buf.append(" [");
		buf.append(Thread.currentThread().getName());
		buf.append("] ");

		buf.append(level);
		buf.append(" ");

		buf.append(name);
		buf.append(" - ");

		buf.append(message);

		buf.append(LINE_SEPARATOR);

		System.err.print(buf.toString());
		if (t != null) {
			t.printStackTrace(System.err);
		}
		System.err.flush();
	}

	private void formatAndLog(String level, String format, Object arg1, Object arg2) {
		FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	private void formatAndLog(String level, String format, Object[] argArray) {
		FormattingTuple tp = MessageFormatter.arrayFormat(format, argArray);
		log(level, tp.getMessage(), tp.getThrowable());
	}
}