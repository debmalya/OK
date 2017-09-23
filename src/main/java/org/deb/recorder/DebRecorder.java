package org.deb.recorder;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;
import org.deb.OK;

public class DebRecorder implements Runnable {

	private static File wavFile = null;
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
	TargetDataLine line;
	/**
	 * To check WAV file size, if size exceeds then create a new file.
	 */
	private WAVFileWatcher watcher = null;

	private static final Logger debugLog = Logger.getLogger("debugLogger");

	private static final Logger errorLog = Logger.getLogger("errorLogger");

	public WAVFileWatcher getWatcher() {
		return this.watcher;
	}

	public void setWatcher(WAVFileWatcher watcher) {
		this.watcher = watcher;
	}

	public void run() {
		if (wavFile == null) {
			wavFile = new File("Images" + File.separator + OK.getDate("ddMMyyyy_kkmmss") + ".wav");
		}
		start();
	}

	void start() {
		try {
			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
				errorLog.error("Line not supported");
				System.exit(0);
			}
			this.line = ((TargetDataLine) AudioSystem.getLine(info));
			this.line.open(format);
			this.line.start();

			debugLog.debug("Start capturing...");

			AudioInputStream ais = new AudioInputStream(this.line);

			debugLog.debug("Start recording...DebRecorder");

			debugLog.debug("Active thread count :" + Thread.activeCount());

			AudioSystem.write(ais, this.fileType, wavFile);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static File getWavFile() {
		return wavFile;
	}

	public static void setWavFile(File wavFile) {
		DebRecorder.wavFile = wavFile;
	}

	AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		return format;
	}

	void finish() {
		this.line.stop();
		this.line.close();
		debugLog.debug("Finished");
	}

	protected void finalize() throws Throwable {
		finish();
	}

}
