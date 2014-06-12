package org.kunaldawn.module.frag;


import com.esotericsoftware.minlog.Log;

public class Test {

	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		Test t = new Test();
		t.start();
	}

	private void start() {
		Log.debug("TESTING", "STARTING FIRST STAGE");
		try {
			FileInfo fileInfo = FragManager.doFragmentation("data.run",
					1000 * 1000 * 10, "data");
			System.out.println("----------------------------------");
			System.out.println("FILE INFORMATION");
			System.out.println("----------------------------------");
			System.out.println(fileInfo.getFileLocation());
			System.out.println(fileInfo.getFileName());
			System.out.println(fileInfo.getFileSize());
			for (FragInfo fragInfo : fileInfo.getFileFragments()) {
				System.out.println("----------------------------------");
				System.out.println("FRAGMENT INFORMATION");
				System.out.println("----------------------------------");
				System.out.println(fragInfo.getFragFileId());
				System.out.println(fragInfo.getFragFileLocation());
				System.out.println(fragInfo.getFragFileName());
				System.out.println(fragInfo.getFragFileSize());
			}
			Log.debug("TESTING", "FIRST STAGE DONE");

		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.debug("TESTING", "STARTING SECOND STAGE");
		FragManager.doFragmentationThreaded("data.run", 1000 * 1000 * 10,
				"data", new FragListner() {

					@Override
					public void onFragDone(FileInfo fileInfo, Exception ex) {
						if (ex != null)
							Log.debug("TESTING", "SECOND STAGE DONE");
						else
							ex.printStackTrace();
					}
				});

		Log.debug("TESTING", "STARTING THIRD STAGE");
		FragManager.doFragmentationThreaded("data.run", 1000 * 1000 * 10,
				"data", new FragListner() {

					@Override
					public void onFragDone(FileInfo fileInfo, Exception ex) {
						if (ex != null)
							Log.debug("TESTING", "THIRD STAGE DONE");
						else
							ex.printStackTrace();
					}
				});

		Log.debug("TESTING", "METHOD COMPLETED");
	}

}
