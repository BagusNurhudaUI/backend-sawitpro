import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class App 
{
    public static void main(String[] args) throws IOException, InterruptedException {
        String urlImage1 = "https://lh3.googleusercontent.com/MLze3lbJGhRvW7_uweMbSeu7dGlbNyJxrGHjZQS_qHZ4vD2lYdA9SStZGdB98gY2RCs=w2400";
        String urlImage2 = "https://lh5.googleusercontent.com/W9jmYbsTl1Ug2Xi7bKpSqACD4c1-Ku4dmGbebKvpCoDYIomCBv0kXNBckAmxZV_xrug=w2400";
        String urlImage3 = "https://lh4.googleusercontent.com/rrJ9_c_4jcUu6zRGEwN6qX_Zq-0E8fpRbHNVHsLIWLwu-db4c8lpBVMEmp9elUs4jBw=w2400";
        String urlImage4 = "https://lh6.googleusercontent.com/eVnDc43gE2ni3hmm4lbrmEiuMqPTmwMBXqzxCHp-NdfztvTJIS9Ij9Zcpl8iN_c3I0I=w2400";
        ocrApi(urlImage1);
    }

    public static void ocrApi(String url) throws IOException, InterruptedException {
        try {
            var httpClient = HttpClient.newBuilder().build();
            JSONObject payload = new JSONObject();
            payload.put("providers", "google");
            payload.put("language", "en");
            payload.put("file_url", url);
            var host = "https://api.edenai.run";
            var pathname = "/v2/ocr/ocr";
            var request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(payload)))
                    .uri(URI.create(host + pathname ))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMmVkYzhhNjctYjUzMi00M2UwLWIzMDAtZGFmNWQyMGFhNDBhIiwidHlwZSI6ImFwaV90b2tlbiJ9.dC00j9FYjflPiTRfOdVnaywi-AAqqjJvEOGC_YqlUOs")
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
                JSONObject res = new JSONObject(response.body());
                res = (JSONObject) res.getJSONObject("google");
                String result = res.getString("text");
                System.out.println(result);

                if(result.matches("(?s)([\\P{IsHan}]*[\\p{IsHan}]){8}.*")) {
                    // Accept name
                    System.out.println("china");
                    PrintWriter out = new PrintWriter("china.txt");
                    out.println(result);
                    out.close();
                }
                else {
                    // Ask to enter again
                    System.out.println("english");
                    PrintWriter out = new PrintWriter("english.txt");
                    out.println(result);
                    out.close();
                    toImage(result);
                }
            } else {
                System.out.println(response.body());
                throw new IOException("Error in response API");
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }


    }

    public static void toImage(String result) throws  IOException {
        String str = result;
        BufferedImage bufferedImage = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        g.setFont(new Font("TimesRoman", Font.BOLD, 24));

        java.util.List<String> myList = List.of(str.split(" "));
        int i = 10;
        int y = 20;
        for (String s: myList) {
            ((Graphics2D) g).setPaint(Color.WHITE);
            if(s.matches("(\\w+(o|O)\\w+)|(\\w+(o|O))|((o|O)\\w+)")){
                ((Graphics2D) g).setPaint(Color.BLUE);
            }
            g.drawString(s, i, y);
            i=i+(s.length()*17 + 5);
            if(i>=1000) {
                i = 10;
                y = y + 30;
            }
        }
        ImageIO.write(bufferedImage, "png", new File("result.png"));
    }
}
