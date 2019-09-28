package itx.image.service.dto;

public class ExifInfo {

    private final int height;
    private final int width;
    private final int precisionBites;
    private final String compressionType;

    public ExifInfo(int height, int width, int precisionBites, String compressionType) {
        this.height = height;
        this.width = width;
        this.precisionBites = precisionBites;
        this.compressionType = compressionType;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getPrecisionBites() {
        return precisionBites;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public static class Builder {

        private int height;
        private int width;
        private int precisionBites;
        private String compressionType;

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setPrecisionBites(int precisionBites) {
            this.precisionBites = precisionBites;
        }

        public void setCompressionType(String compressionType) {
            this.compressionType = compressionType;
        }

        public ExifInfo build() {
            return new ExifInfo(height, width, precisionBites, compressionType);
        }

    }

}
