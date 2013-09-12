package com.logentries.jenkins;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.expectLastCall;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Before;
import org.junit.Test;


public class LogentriesLogDecoratorTest {
	
	/** UTF-8 output character set. */
	private static final Charset UTF8 = Charset.forName("UTF-8");

	private OutputStream mockOs;
	private LogentriesWriter mockLogentriesWriter;
	private LogentriesLogDecorator logentriesLogDecorator;
	
	
	/**
	 * Creates the mocks used in the tests.
	 * @throws IOException Shouldn't happen
	 * @throws UnknownHostException Shouldn't happen
	 */
	@Before
	public void initMocks() throws UnknownHostException, IOException {
		mockOs = createMock(OutputStream.class);
		mockLogentriesWriter = createMock(LogentriesWriter.class);
		logentriesLogDecorator = new LogentriesLogDecorator(mockOs, mockLogentriesWriter);
	}
	
	/**
	 * Verifies that lines are written to both the wrapped OutputStream and the 
	 * LogentriesWriter.
	 * @throws IOException Shouldn't happen
	 */
	@Test
	public void writeLines() throws IOException {
		String[] lines = new String[]{
				"Output line1",
				"Output line2",
				"Output line3"
		};
		for (String line : lines) {
			mockLogentriesWriter.writeLogentry(line);
			mockOs.write(ByteArrayStartsWith.startsWIthBytes((line + "\n").getBytes(UTF8)), eq(0), eq(line.length() + 1));
		}
		replay(mockLogentriesWriter, mockOs);
		
		for (String line : lines) {
			logentriesLogDecorator.write((line + "\n").getBytes(UTF8));
		}
		verify(mockLogentriesWriter, mockOs);
	}

	/**
	 * Verifies that an error in writing to the LogentriesWriter does not cause
	 * an exception to bubble.
	 * 
	 * @throws IOException Shouldn't happen
	 */
	@Test
	public void writeError() throws IOException {
		String line = "errot line";
		mockLogentriesWriter.writeLogentry(line);
		expectLastCall().andThrow(new RuntimeException("Arrrgh"));
		replay(mockLogentriesWriter);
		logentriesLogDecorator.write((line + "\n").getBytes(UTF8));
	}

	private static class ByteArrayStartsWith implements IArgumentMatcher {
	    
		private final byte[] expectedBytes;;

	    public ByteArrayStartsWith(byte[] expectedBytes) {
	        this.expectedBytes = expectedBytes;
	    }

	    public boolean matches(Object actual) {
	        if (!(actual instanceof byte[])) {
	            return false;
	        }
	        return matches((byte[]) actual, expectedBytes);
	        
	    }
	    
	    public boolean matches(byte[] actual, byte[] expected) {
			boolean matches = true;
			if (actual.length >= expected.length) {
				for (int i = 0; i < expected.length && matches; i++) {
					matches = actual[i] == expected[i];
				}
			} else {
				matches = false;
			}
			return matches;
		}
	    
	    public static byte[] startsWIthBytes(byte[] bytes) {
	        EasyMock.reportMatcher(new ByteArrayStartsWith(bytes));
	        return null;
	    }
	    
	    public void appendTo(StringBuffer buffer) {
	        buffer.append("starteWIthBytes()");
	    }
	}	
}
