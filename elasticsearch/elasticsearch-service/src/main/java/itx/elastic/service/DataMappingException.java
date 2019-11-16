package itx.elastic.service;

/**
 * This exception indicates, that data mapping has failed within {@link DataTransformer}.
 * This exeprion is thrown when raw data from ElasticSearch can't be mapped into particular java POJO.
 */
public class DataMappingException extends Exception {

    public DataMappingException(String message) {
        super(message);
    }

    public DataMappingException(Throwable t) {
        super(t);
    }

}
