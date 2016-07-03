package com.gifisan.nio.component.future;

import java.io.InputStream;
import java.io.OutputStream;

import com.gifisan.nio.component.DefaultParameters;
import com.gifisan.nio.component.Parameters;

public abstract class ReadFutureImpl extends FutureImpl implements ReadFuture {

	private Parameters			parameters	;
	protected OutputStream		outputStream	;
	protected InputStream		inputStream	;

	public Parameters getParameters() {
		if (parameters == null) {
			parameters = new DefaultParameters(getText());
		}
		return parameters;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public boolean hasOutputStream() {
		return outputStream != null;
	}
	
	public int getStreamLength() {
		return 0;
	}
	
}