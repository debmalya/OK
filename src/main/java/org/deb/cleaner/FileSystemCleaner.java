package org.deb.cleaner;

import java.io.File;

import org.apache.log4j.Logger;
import org.deb.OK;

public class FileSystemCleaner implements Runnable {
	
	private static final Logger debugLog = Logger.getLogger("debugLogger");

	private static final Logger errorLog = Logger.getLogger("errorLogger");


	@Override
	/**
	 * Issue #1 Delete files which are older than a week.
	 */
	public void run() {

		File file = new File(OK.DIR);
		File[] fileList = file.listFiles();
		for (File eachFile : fileList) {
			long lastModificationTime = eachFile.lastModified();
			if (System.currentTimeMillis() - lastModificationTime > 604800000) {
				// last modified seven days ago.
				if (eachFile.isDirectory()) {
					deleteFolder(eachFile);
					debugLog.debug("Deleted folder :" + eachFile.getAbsolutePath());
				} else {
					eachFile.delete();
					debugLog.debug("Deleted file :" + eachFile.getAbsolutePath());
				}
			}
		}

	}

	/**
	 * Delete folder. Copied from
	 * https://stackoverflow.com/questions/7768071/how-to-delete-directory-
	 * content-in-java#7768086
	 * 
	 * @param folder
	 */
	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty directories
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

}
