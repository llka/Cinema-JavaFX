package kate.cinema.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.exception.BackendException;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class JsonUtil {
    private static final Logger logger = LogManager.getLogger(JsonUtil.class);

    public static String serialize(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error(e);
            throw new BackendException("Cannot create json from " + object);
        }
    }

    public static <T> T deserialize(String json, Class<T> type) throws ApplicationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            logger.error(e);
            throw new ApplicationException("Cannot create object from " + json, ResponseStatus.BAD_REQUEST);
        }
    }

}
