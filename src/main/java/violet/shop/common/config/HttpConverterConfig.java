package violet.shop.common.config;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class HttpConverterConfig {

    @Bean
    public HttpMessageConverter<Message> protoJsonConverter() {
        return new ProtoJsonConverter();
    }

    private static class ProtoJsonConverter extends AbstractHttpMessageConverter<Message> {

        public ProtoJsonConverter() {
            super(MediaType.APPLICATION_JSON);
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return Message.class.isAssignableFrom(clazz);
        }

        @Override
        protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage) throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputMessage.getBody(), StandardCharsets.UTF_8))) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                Message.Builder builder = (Message.Builder) clazz.getMethod("newBuilder").invoke(null);
                JsonFormat.parser().merge(json.toString(), builder);
                return builder.build();
            } catch (Exception e) {
                throw new IOException("Failed to parse protobuf message", e);
            }
        }

        @Override
        protected void writeInternal(Message message, HttpOutputMessage outputMessage) throws IOException {
            outputMessage.getBody().write(JsonFormat.printer().print(message).getBytes(StandardCharsets.UTF_8));
        }
    }
}