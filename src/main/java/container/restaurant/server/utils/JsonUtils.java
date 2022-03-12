package container.restaurant.server.utils;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtils {
    /*
     * Json String to JsonElement
     *
     * @return
     */
    public static JsonElement JsonStringParse(String jsonStr) {
        return new JsonParser().parse(jsonStr).getAsJsonObject();
    }

    /*
     * Json String Buffer to JsonElement
     *
     * @return
     */
    public static JsonElement JsonStringBufferParse(StringBuffer jsonBuffer) {
        return new JsonParser().parse(jsonBuffer.toString()).getAsJsonObject();
    }


}
