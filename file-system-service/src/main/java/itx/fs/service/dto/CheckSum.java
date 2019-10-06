package itx.fs.service.dto;

import java.util.Objects;

public class CheckSum {

    private final String checksum;
    private final String algorithm;

    public CheckSum(String checksum, String algorithm) {
        Objects.requireNonNull(checksum);
        Objects.requireNonNull(algorithm);
        this.checksum = checksum;
        this.algorithm = algorithm;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckSum checkSum = (CheckSum) o;
        return Objects.equals(checksum, checkSum.checksum) &&
                Objects.equals(algorithm, checkSum.algorithm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, algorithm);
    }

    @Override
    public String toString() {
        return checksum + ":" + algorithm;
    }
}
