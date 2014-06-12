package org.kunaldawn.module.frag;

import java.util.ArrayList;

/**
 * Class represents basic file information for which fragmentation has
 * been done and information of all the fragments.
 */
public class FileInfo {

    
    /** The file name. */
    private String fileName;
    
    /** The file location. */
    private String fileLocation;
    
    /** The file size. */
    private long fileSize;
    
    /** The file fragments. */
    private ArrayList<FragInfo> fileFragments;

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the file location.
     *
     * @return the file location
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Sets the file location.
     *
     * @param fileLocation the new file location
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * Gets the file size.
     *
     * @return the file size
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the file size.
     *
     * @param fileSize the new file size
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Gets the file fragments.
     *
     * @return the file fragments
     */
    public ArrayList<FragInfo> getFileFragments() {
        return fileFragments;
    }

    /**
     * Sets the file fragments.
     *
     * @param fileFragments the new file fragments
     */
    public void setFileFragments(ArrayList<FragInfo> fileFragments) {
        this.fileFragments = fileFragments;
    }
}
