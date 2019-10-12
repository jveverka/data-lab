package itx.image.service.model.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
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
import itx.image.service.model.values.Shorts;
import itx.image.service.model.values.StringValue;
import itx.image.service.model.values.StringValues;
import itx.image.service.model.values.TagValue;
import itx.image.service.model.values.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class StringToTagValueConverter extends JsonDeserializer<TagValue> {

    @Override
    public TagValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        Type type = Type.valueOf(node.get("type").textValue());
        String stringValue = node.get("value").asText();
        Optional<String> unit = Optional.ofNullable(node.get("unit").asText());
        switch (type) {
            case INTEGER:
                return new IntegerValue(Integer.parseInt(stringValue), unit);

            case DOUBLE:
                return new DoubleValue(Double.parseDouble(stringValue), unit);

            case STRING:
                return new StringValue(stringValue, unit);

            case JPEG_COMPONENT:
                JsonNode jpegNodeValue = node.get("value");
                JPEGComponent.Value jpegValue = new JPEGComponent.Value(
                        jpegNodeValue.get("name").asText(),
                        jpegNodeValue.get("samplingFactorByte").asInt(),
                        jpegNodeValue.get("quantizationTableNumber").asInt()
                );
                return new JPEGComponent(jpegValue, unit);

            case FRACTION:
                JsonNode jpegFractionValue = node.get("value");
                Fraction.Value fractionValue = new Fraction.Value(
                        jpegFractionValue.get("numerator").asLong(),
                        jpegFractionValue.get("denominator").asLong()
                );
                return new Fraction(fractionValue, unit);

            case FRACTIONS:
                JsonNode jpegFractionsValue = node.get("value");
                Iterator<JsonNode> fractionsIterator = jpegFractionsValue.elements();
                List<Fraction.Value> fractions = new ArrayList<>();
                while(fractionsIterator.hasNext()) {
                    JsonNode fractValueNode = fractionsIterator.next();
                    Fraction.Value fractValue = new Fraction.Value(
                            fractValueNode.get("numerator").asLong(),
                            fractValueNode.get("denominator").asLong()
                    );
                    fractions.add(fractValue);
                }
                return new Fractions(fractions.toArray(new Fraction.Value[fractions.size()]), unit);

            case LONG:
                return new LongValue(Long.parseLong(stringValue), unit);

            case BYTES:
                byte[] bytes = Base64.getDecoder().decode(stringValue);
                return new Bytes(bytes, unit);

            case SHORTS:
                JsonNode shortsValue = node.get("value");
                Iterator<JsonNode> shortsIterator = shortsValue.elements();
                List<Short> shorts = new ArrayList<>();
                while(shortsIterator.hasNext()) {
                    shorts.add((short)shortsIterator.next().asInt());
                }
                short[] shortsArray = new short[shorts.size()];
                for (int i=0; i<shortsArray.length; i++) {
                    shortsArray[i] = shorts.get(i);
                }
                return new Shorts(shortsArray, unit);

            case INTEGERS:
                JsonNode integersValue = node.get("value");
                Iterator<JsonNode> integersIterator = integersValue.elements();
                List<Integer> integers = new ArrayList<>();
                while(integersIterator.hasNext()) {
                    integers.add(integersIterator.next().asInt());
                }
                int[] integersArray = new int[integers.size()];
                for (int i=0; i<integersArray.length; i++) {
                    integersArray[i] = integers.get(i);
                }
                return new Integers(integersArray, unit);

            case BOOLEAN:
                return new BooleanValue(Boolean.valueOf(stringValue), unit);

            case FLOATS:
                JsonNode floatsValue = node.get("value");
                Iterator<JsonNode> floatsIterator = floatsValue.elements();
                List<Float> floats = new ArrayList<>();
                while(floatsIterator.hasNext()) {
                    floats.add(floatsIterator.next().floatValue());
                }
                float[] floatArray = new float[floats.size()];
                for (int i=0; i<floatArray.length; i++) {
                    floatArray[i] = floats.get(i);
                }
                return new Floats(floatArray, unit);

            case OBJECT_LIST:
                JsonNode objectListValue = node.get("value");
            break;

            case STRINGS:
                JsonNode stringsValue = node.get("value");
                Iterator<JsonNode> stringsIterator = stringsValue.elements();
                List<String> strings = new ArrayList<>();
                while(stringsIterator.hasNext()) {
                    strings.add(stringsIterator.next().asText());
                }
                return new StringValues(strings.toArray(new String[strings.size()]), unit);

            case DATE:
                long dateValue = node.get("value").asLong();
                return new DateValue(new Date(dateValue), unit);

            case FLOAT:
                return new FloatValue(Float.valueOf(stringValue),unit);

            default: throw new UnsupportedOperationException("");
        }
        throw new UnsupportedOperationException("Can't deserialize TagValue.");
    }

}
