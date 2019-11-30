package itx.fs.service.dto;

import java.util.Objects;

public class CheckSum {

    private final String value;
    private final String algorithm;

    public CheckSum(String value, String algorithm) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(algorithm);
        this.value = value;
        this.algorithm = algorithm;
    }

    public String getValue() {
        return value;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckSum checkSum = (CheckSum) o;
        return Objects.equals(this.value, checkSum.value) &&
                Objects.equals(algorithm, checkSum.algorithm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, algorithm);
    }

    @Override
    public String toString() {
        return value + ":" + algorithm;
    }
}
