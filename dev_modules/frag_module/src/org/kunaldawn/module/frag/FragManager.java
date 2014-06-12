package org.kunaldawn.module.frag;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import com.esotericsoftware.minlog.Log;

/**
 * Handles all fragmentation related functions.
 */
public class FragManager {

	/**
	 * This method helps fragmenting file into small chunks.
	 * 
	 * @param fileURI
	 *            Absolute path of the file to fragment.
	 * @param chunkSize
	 *            Size if the chunks.
	 * @param rootOutputDirectory
	 *            Absolute path of the output directory.
	 * @return FileInfo Class object which holds all chunk information.
	 * @throws Exception
	 *             the exception
	 */
	public static FileInfo doFragmentation(final String fileURI,
			final int chunkSize, final String rootOutputDirectory)
			throws Exception {

		FileInfo fileInfo = new FileInfo();
		File inputFile = new File(fileURI);
		Log.debug("FRAG", "START FRAGMENTATION FOR : " + inputFile.getName());

		BufferedInputStream inputStream = new BufferedInputStream(
				new FileInputStream(inputFile));

		byte[] buffer = new byte[chunkSize];
		int fragCounter = 1;
		int temp;

		String chunkFolder = UUID.randomUUID().toString();
		File rootDir = new File(rootOutputDirectory);
		File chuckFileDir = new File(rootDir, chunkFolder);
		chuckFileDir.mkdirs();

		ArrayList<FragInfo> fragInfos = new ArrayList<FragInfo>();

		while ((temp = inputStream.read(buffer)) > 0) {
			String chunkFileName = UUID.randomUUID().toString() + ".part"
					+ fragCounter;
			File chunkFile = new File(chuckFileDir, chunkFileName);
			Log.debug("FRAG", "CREATING FRAGMENT : " + chunkFile.getName());
			chunkFile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(chunkFile);
			fileOutputStream.write(buffer, 0, temp);
			fileOutputStream.close();

			FragInfo fragInfo = new FragInfo();
			fragInfo.setFragFileId(fragCounter);
			fragInfo.setFragFileLocation(chunkFile.getAbsolutePath());
			fragInfo.setFragFileName(chunkFile.getName());
			fragInfo.setFragFileSize(chunkFile.length());
			fragInfos.add(fragInfo);

			fragCounter++;

		}
		fileInfo.setFileName(inputFile.getName());
		fileInfo.setFileSize(inputFile.length());
		fileInfo.setFileLocation(inputFile.getAbsolutePath());
		fileInfo.setFileFragments(fragInfos);

		inputStream.close();

		Log.debug("FRAG", "FINISH FRAGMENTATION FOR : " + inputFile.getName());
		return fileInfo;
	}

	public static void doFragmentationThreaded(final String fileURI,
			final int chunkSize, final String rootOutputDirectory,
			final FragListner listner) {
		Thread thd = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listner.onFragDone(FragManager.doFragmentation(fileURI,
							chunkSize, rootOutputDirectory), null);
				} catch (Exception e) {
					listner.onFragDone(null, e);
				}
			}
		});
		thd.start();
	}
}
