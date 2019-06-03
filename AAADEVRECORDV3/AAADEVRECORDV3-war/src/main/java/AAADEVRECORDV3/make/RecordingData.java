package AAADEVRECORDV3.make;

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