package container.restaurant.server.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class fileUpload {
    public void upload(MultipartFile image) {
        String charset = "UTF-8";
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        try {
//            URL url = new URL("http://dlwfp.synology.me:22304/api/v1/restaurant/image/upload");
            URL url = new URL("http://localhost:22304/api/v1/restaurant/image/upload");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-Type", "multipart/form-data;charset=" + charset + ";boundary=" + boundary);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);

            OutputStream outputStream = conn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + image.getOriginalFilename() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(image.getOriginalFilename())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF);
            writer.flush();

            File tempImage = createTempImage(image);
            FileInputStream inputStream = new FileInputStream(tempImage);
            byte[] buffer = new byte[(int) tempImage.length()];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            writer.append(CRLF);
            writer.flush();

            try (InputStream in = conn.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buf = new byte[1024 * 8];
                int length = 0;
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                System.out.println(new String(out.toByteArray(), "UTF-8"));
            }
            safeClose(inputStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createTempImage(MultipartFile image) throws IOException {
        InputStream imageFileStream = image.getInputStream();
        File tempImage = File.createTempFile(UUID.randomUUID().toString(), image.getOriginalFilename().replace(image.getName(), ""));
        FileUtils.copyInputStreamToFile(imageFileStream, tempImage);
        return tempImage;
    }

    private void safeClose(InputStream inputStream) throws IOException {
        if (inputStream != null)
            inputStream.close();
    }
}
