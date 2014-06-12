package org.kunaldawn.module.frag;

/**
 * The Interface FragListner. Class that want to listen for the threaded
 * fragmentation, they need to implement this interface to listen when the task
 * has been done and also to get the associated data with it.
 */
public interface FragListner {

	/**
	 * On frag done.
	 * 
	 * @param fileInfo
	 *            the file info
	 * @param ex
	 */
	public void onFragDone(FileInfo fileInfo, Exception ex);
}
