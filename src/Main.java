import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    static Map<String, String> modelList = new HashMap<>() {{
        put("动漫模型", "anime");
        put("高准确率公测动漫模型", "anime_model_lovelive");
        put("GalGame模型", "game");
        put("高准确率公测GalGame模型", "game_model_kirakira");
//        put("漫画（开发中）", "manga");
    }};
    private static String Recognize(String modelName, boolean aiDetect, String imageUrl){
        try {
            int flagAiDetect = aiDetect ? 1 : 0;

            if (modelList.get(modelName)==null){
                return "模型不存在";
            }

            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            URI url = new URI("https://aiapiv2.animedb.cn/ai/api/detect?force_one=1&model=%s&ai_detect=%s".formatted(modelName, String.valueOf(flagAiDetect)));
            // 创建HttpClient
            HttpClient client = HttpClient.newHttpClient();
            if (!Files.exists(Path.of(imageUrl))){
                return "图片不存在";
            }
            byte[] fileByte = Files.readAllBytes(Paths.get(imageUrl));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            String boundary = "WebKitFormBoundaryQQGkjc4BHeWAQfRL";
            String body = "------" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"image\"; filename=\"tes.png\"" +
                    "\r\n"+
                    "Content-Type: image/png" +
                    "\r\n" +
                    "\r\n";
            byteArrayOutputStream.write(body.getBytes());
            byteArrayOutputStream.write(fileByte);
            String end = "\r\n" + "\r\n"+ "--" + boundary + "--\r\n";
            byteArrayOutputStream.write(end.getBytes());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println(response.statusCode());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
        String json = Recognize("动漫模型",true, "src/tes.png");
        if (json!=null){
            System.out.println(json);
        }

    }
}
