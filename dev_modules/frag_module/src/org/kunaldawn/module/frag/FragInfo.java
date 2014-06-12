package org.kunaldawn.module.frag;

/**
 * Created by kunaldawn on 12/6/14.
 */
public class FragInfo {
    
    /** File name of the fragment. */
    private String fragFileName;
    
    /** Absolute path of the fragment. */
    private String fragFileLocation;
    
    /** Size of the fragment. */
    private long fragFileSize;
    
    /** File ID of the fragment. */
    private long fragFileId;

    /**
     * Gets the fragment file name.
     *
     * @return the fragment file name
     */
    public String getFragFileName() {
        return fragFileName;
    }

    /**
     * Sets the fragment file name.
     *
     * @param fragFileName the new fragment file name
     */
    public void setFragFileName(String fragFileName) {
        this.fragFileName = fragFileName;
    }

    /**
     * Gets the frag file location.
     *
     * @return the frag file location
     */
    public String getFragFileLocation() {
        return fragFileLocation;
    }

    /**
     * Sets the fragment file location.
     *
     * @param fragFileLocation the new fragment file location
     */
    public void setFragFileLocation(String fragFileLocation) {
        this.fragFileLocation = fragFileLocation;
    }

    /**
     * Gets the fragment file size.
     *
     * @return the fragment file size
     */
    public long getFragFileSize() {
        return fragFileSize;
    }

    /**
     * Sets the fragment file size.
     *
     * @param fragFileSize the new fragment file size
     */
    public void setFragFileSize(long fragFileSize) {
        this.fragFileSize = fragFileSize;
    }

    /**
     * Gets the fragment file id.
     *
     * @return the fragment file id
     */
    public long getFragFileId() {
        return fragFileId;
    }

    /**
     * Sets the fragment file id.
     *
     * @param fragFileId the new fragment file id
     */
    public void setFragFileId(long fragFileId) {
        this.fragFileId = fragFileId;
    }
}
