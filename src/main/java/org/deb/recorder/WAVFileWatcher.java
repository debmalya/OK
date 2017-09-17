package org.deb.recorder;

import java.io.File;

import org.apache.log4j.Logger;
import org.deb.OK;

public class WAVFileWatcher implements Runnable {
	public static final long FILE_SIZE = 524288000L;
	private boolean fileSizeExceeded = false;
	private DebRecorder debRecorder;

	private static final Logger debugLog = Logger.getLogger("debugLogger");

	private static final Logger errorLog = Logger.getLogger("errorLogger");

	public DebRecorder getDebRecorder() {
		return this.debRecorder;
	}

	public void setDebRecorder(DebRecorder debRecorder) {
		this.debRecorder = debRecorder;
	}

	public void run() {
		try {
			for (;;) {
				long size = DebRecorder.getWavFile().length();
				Thread.sleep(5000L);
				if (size >= 524288000L) {
					setFileSizeExceeded(true);
					debugLog.debug("Restart");
					this.debRecorder.finish();
					setFileSizeExceeded(false);
					DebRecorder
							.setWavFile(new File("Images" + File.separator + OK.getDate("ddMMyyyy_kkmmss") + ".wav"));
					this.debRecorder.setWatcher(this);
					this.debRecorder.start();
					debugLog.debug("After starting");
				} else {
					setFileSizeExceeded(false);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isFileSizeExceeded() {
		return this.fileSizeExceeded;
	}

	public void setFileSizeExceeded(boolean fileSizeExceeded) {
		this.fileSizeExceeded = fileSizeExceeded;
	}

}
