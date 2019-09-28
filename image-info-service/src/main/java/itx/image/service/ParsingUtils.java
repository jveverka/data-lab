package itx.image.service;

public final class ParsingUtils {

    public static final String IMAGE_HEIGHT = "Image Height";
    public static final String IMAGE_WIDTH = "Image Width";
    public static final String COMPRESSION_TYPE = "Compression Type";
    public static final String DATA_PRECISION = "Data Precision";

    private ParsingUtils() {
    }

    public static int getIntegerFromData(String data) {
        String[] words = data.split(" ");
        return Integer.parseInt(words[0]);
    }

}
