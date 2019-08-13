package AAADEVRECORDV3.util;

public enum RecordingData
{
    INSTANCE;
    private String recordingFilename = null;

    public String getRecordingFilename()
    {
        return recordingFilename;
    }

    public void setRecordingFilename(final String recordingFilename)
    {
        this.recordingFilename = recordingFilename;
    }
}