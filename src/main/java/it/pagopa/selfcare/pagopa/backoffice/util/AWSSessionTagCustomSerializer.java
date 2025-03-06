package it.pagopa.selfcare.pagopa.backoffice.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import software.amazon.awssdk.services.quicksight.model.SessionTag;

import java.io.IOException;

/**
 * This class permits to serialize an AWS session tag, encapsulated in {@link SessionTag} object, to a
 * JSON body request using Jackson serializer.
 */
public class AWSSessionTagCustomSerializer extends StdSerializer<SessionTag> {

    protected AWSSessionTagCustomSerializer(Class<SessionTag> t) {
        super(t);
    }
    public AWSSessionTagCustomSerializer(){
        this(null);
    }

    @Override
    public void serialize(SessionTag sessionTag, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("key", sessionTag.key());
        gen.writeStringField("value",sessionTag.value());
    }
}
