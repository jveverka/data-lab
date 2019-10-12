package itx.image.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.Tag;
import com.drew.metadata.jpeg.JpegComponent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import itx.image.service.model.DirectoryInfo;
import itx.image.service.model.ErrorInfo;
import itx.image.service.model.MetaData;
import itx.image.service.model.TagInfo;
import itx.image.service.model.values.BooleanValue;
import itx.image.service.model.values.Bytes;
import itx.image.service.model.values.DateValue;
import itx.image.service.model.values.DoubleValue;
import itx.image.service.model.values.FloatValue;
import itx.image.service.model.values.Floats;
import itx.image.service.model.values.Fraction;
import itx.image.service.model.values.Fractions;
import itx.image.service.model.values.IntegerValue;
import itx.image.service.model.values.Integers;
import itx.image.service.model.values.JPEGComponent;
import itx.image.service.model.values.LongValue;
import itx.image.service.model.values.Longs;
import itx.image.service.model.values.ObjectList;
import itx.image.service.model.values.Shorts;
import itx.image.service.model.values.StringValues;
import itx.image.service.model.values.TagValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public final class ParsingUtils {

    private ParsingUtils() {
    }

    private final static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new Jdk8Module());
    }

    public static MetaData createModel(InputStream is) throws ImageProcessingException, IOException {
        List<DirectoryInfo> directories = new ArrayList<>();
        Metadata metadata = ImageMetadataReader.readMetadata(is);
        for (Directory directory : metadata.getDirectories()) {
            List<TagInfo> tags = new ArrayList<>();
            String directoryName = ParsingUtils.normalizeName(directory.getName());
            for (Tag tag : directory.getTags()) {
                Object value = extractTagValue(directory, tag);
                Optional<String> unit = extractUnit(tag.getTagName(), tag.getDescription());
                String tagName = ParsingUtils.normalizeName(tag.getTagName());
                String tagDescription = tag.getDescription();
                TagInfo tagInfo = new TagInfo(tagName, tagDescription, resolveObjectValue(value, unit));
                tags.add(tagInfo);
            }
            List<ErrorInfo> errors = new ArrayList<>();
            for (String error: directory.getErrors()) {
                errors.add(new ErrorInfo(error));
            }
            directories.add(new DirectoryInfo(directoryName, tags, errors));
        }
        return new MetaData(directories);
    }

    public static String printToJson(MetaData metaData) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metaData);
    }

    public static String normalizeName(String name) {
        return name.toLowerCase().replace(' ', '-');
    }

    public static Object extractTagValue(Directory directory, Tag tag) {
        return directory.getObject(tag.getTagType());
    }

    public static Optional<String> extractUnit(String tagName, String tagDescription) {
        if (tagDescription == null) return Optional.empty();
        if (tagDescription.endsWith(" bits")) {
            return Optional.of("bits");
        } else if (tagDescription.endsWith(" pixels")) {
            return Optional.of("pixels");
        } else if (tagDescription.endsWith(" dots per inch")) {
            return Optional.of("dots/inch");
        } else if (tagDescription.equals("Inch")) {
            return Optional.of("inch");
        } else if (tagDescription.endsWith(" Inch")) {
            return Optional.of("inch");
        } else if (tagDescription.endsWith(" sec")) {
            return Optional.of("sec");
        } else if (tagDescription.endsWith(" EV")) {
            return Optional.of("EV");
        } else if (tagDescription.endsWith(" mm")) {
            return Optional.of("mm");
        } else if (tagDescription.endsWith(" bytes")) {
            return Optional.of("bytes");
        }
        if (tagName == null) return Optional.empty();
        if (tagName.contains("Date/Time")) {
            return Optional.of("timestamp");
        } else if (tagName.contains("ISO")) {
            return Optional.of("iso");
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static TagValue resolveObjectValue(Object value, Optional<String> unit) {
        if (value instanceof Integer) {
            Integer intValue = (Integer)value;
            return new IntegerValue(intValue, unit);
        } else if (value instanceof JpegComponent) {
            JpegComponent jpegComponentValue = (JpegComponent)value;
            JPEGComponent jpegComponent = new JPEGComponent(
                    new JPEGComponent.Value(jpegComponentValue.getComponentName(), jpegComponentValue.getVerticalSamplingFactor(), jpegComponentValue.getQuantizationTableNumber()),
                    unit
            );
            return jpegComponent;
        } else if (value instanceof StringValue) {
            StringValue stringValueValue = (StringValue)value;
            if (stringValueValue.getCharset() == null) {
                return new itx.image.service.model.values.StringValue(new String(stringValueValue.getBytes(), Charset.forName("UTF-8")), unit);
            } else {
                return new itx.image.service.model.values.StringValue(new String(stringValueValue.getBytes(), stringValueValue.getCharset()), unit);
            }
        } else if (value instanceof Rational) {
            Rational rational = (Rational)value;
            return new Fraction(new Fraction.Value(rational.getNumerator(), rational.getDenominator()), unit);
        } else if (value instanceof Long) {
            Long longValue = (Long)value;
            return new LongValue(longValue.longValue(), unit);
        } else if (value instanceof byte[]) {
            byte[] byteValue = (byte[])value;
            return new Bytes(byteValue, unit);
        } else if (value instanceof String) {
            String stringValue = (String)value;
            return new itx.image.service.model.values.StringValue(stringValue, unit);
        } else if (value instanceof Rational[]) {
            Rational[] rationals = (Rational[])value;
            Fraction.Value[] values = new Fraction.Value[rationals.length];
            for (int i=0; i<rationals.length; i++) {
                values[i] = new Fraction.Value(rationals[i].getNumerator(), rationals[i].getDenominator());
            }
            return new Fractions(values, unit);
        } else if (value instanceof int[]) {
            int[] intValues = (int[])value;
            return new Integers(intValues, unit);
        } else if (value instanceof short[]) {
            short[] shortValues = (short[])value;
            return new Shorts(shortValues, unit);
        } else if (value instanceof long[]) {
            long[] longValues = (long[])value;
            return new Longs(longValues, unit);
        } else if (value instanceof Double) {
            Double doubleValue = (Double)value;
            return new DoubleValue(doubleValue, unit);
        } else if (value instanceof Boolean) {
            Boolean booleanValue = (Boolean)value;
            return new BooleanValue(booleanValue, unit);
        } else if (value instanceof float[]) {
            float[] floatValues = (float[])value;
            return new Floats(floatValues, unit);
        } else if (value instanceof ArrayList) {
            ArrayList<Object> objectValues = (ArrayList<Object>)value;
            return new ObjectList(objectValues, unit);
        } else if (value instanceof String[]) {
            String[] stringsValues = (String[])value;
            return new StringValues(stringsValues, unit);
        } else if (value instanceof Date) {
            Date dateValue = (Date)value;
            return new DateValue(dateValue, unit);
        } else if (value instanceof Float) {
            Float floatValue = (Float)value;
            return new FloatValue(floatValue, unit);
        }
        throw new UnsupportedOperationException("Unsupported value type: " + value.getClass().getCanonicalName());
    }

}
