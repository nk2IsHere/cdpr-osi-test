package tools.kot.nk2.cdprshop.domain.game.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.ConnectionFactory;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import tools.kot.nk2.cdprshop.domain.tag.protocol.Tag;
import io.r2dbc.postgresql.codec.Json;

import java.util.List;

@Configuration
public class GameDomainConfiguration {

    static class JsonToListTagConverter implements Converter<Json, List<Tag>> {

        private final ObjectMapper objectMapper;

        JsonToListTagConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @SneakyThrows
        @Override
        public List<Tag> convert(Json source) {
            return objectMapper.readValue(source.asString(), new TypeReference<>() {});
        }
    }

    static class ListTagToJsonConverter implements Converter<List<Tag>, Json> {

        private final ObjectMapper objectMapper;

        ListTagToJsonConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @SneakyThrows
        @Override
        public Json convert(@NonNull List<Tag> source) {
            return Json.of(objectMapper.writeValueAsString(source));
        }
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(
        ConnectionFactory connectionFactory,
        ObjectMapper objectMapper
    ) {
        var dialect = DialectResolver.getDialect(connectionFactory);
        return R2dbcCustomConversions
            .of(
                dialect,
                List.of(
                    new JsonToListTagConverter(objectMapper),
                    new ListTagToJsonConverter(objectMapper)
                )
            );
    }
}
