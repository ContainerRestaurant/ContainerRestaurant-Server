package container.restaurant.server.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.UUID;

import static container.restaurant.server.utils.JsonUtils.JsonStringBufferParse;

@Slf4j
@Component
public class MultipartUtility {

    @Value("${server.image.base.url}")
    private String BASE_URL;
    private final String DEFAULT_PATH = "/api/image/upload";
    private final String UserAgent = "ContainerRestaurant Agent";
    private final String LINE_FEED = "\r\n";
    private String boundary = "--------------------------";
    private HttpURLConnection conn;
    private OutputStream output;
    private PrintWriter writer;


    public void init() throws IOException {
        boundary += System.currentTimeMillis();
        URL url = new URL(BASE_URL + DEFAULT_PATH);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(1000);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("User-Agent", UserAgent);
        output = conn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"),
                true);
    }


    public void addFilePart(String fieldName, MultipartFile imageFile) throws IOException {
        File uploadFile = createTempImageFile(imageFile);
        String fileName = imageFile.getOriginalFilename();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED).flush();
        Files.copy(uploadFile.toPath(), output);
        output.flush();
        writer.append(LINE_FEED).flush();
    }

    public JsonElement finish() throws IOException {
        JsonObject response = new JsonObject();

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        int status = conn.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            StringBuffer resStringBuffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                resStringBuffer.append(line);
            }
            response = (JsonObject) JsonStringBufferParse(resStringBuffer);

            reader.close();
            conn.disconnect();
        } else {
            new IOException("Image File Server non-OK status => " + status);
        }
        return response;
    }

    private File createTempImageFile(MultipartFile image) throws IOException {
        InputStream imageFileStream = image.getInputStream();
        File tempImage = File.createTempFile(UUID.randomUUID().toString(), image.getOriginalFilename().replace(image.getName(), ""));
        FileUtils.copyInputStreamToFile(imageFileStream, tempImage);
        return tempImage;
    }

}
