package container.restaurant.server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.UUID;

import static container.restaurant.server.utils.JsonUtils.JsonStringBufferParse;

public class MultipartUtility {
    private final String URL = "http://dlwfp.synology.me:22304/api/v1/restaurant/image/upload";
    private final String UserAgent = "ContainerRestaurant Agent";
    private final String LINE_FEED = "\r\n";
    private String boundary = "--------------------------";
    private HttpURLConnection conn;
    private OutputStream output;
    private PrintWriter writer;


    public MultipartUtility() throws IOException {
        boundary += System.currentTimeMillis();
        URL url = new URL(URL);
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
