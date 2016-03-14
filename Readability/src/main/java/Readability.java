import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Zhang on 3/14.
 */
public class Readability {
    private static String token = "b3ad4a0d0dae066420cb9a37004a89851a661baa";

    public static void main(String[] args) {
        String url = "http://www.expreview.com/45965.html";
        getContent("https://www.readability.com/api/content/v1/parser?url="+url+"&token="+token);
    }

    public static void getContent(String s) {
        try {
            URL url = new URL(s);
            URLConnection connection = url.openConnection();
            connection.connect();


            Map<String, List<String>> header = connection.getHeaderFields();
            header.entrySet().forEach(System.out::println);


            System.out.println("***************************************");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();

        } catch (MalformedURLException m) {
            m.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public static Reader getReader(String s) {
        try {
            URL url = new URL("https://www.readability.com/api/content/v1/parser?url=" + s + "&token=" + token);
            URLConnection connection = url.openConnection();
            connection.connect();


            Map<String, List<String>> header = connection.getHeaderFields();
            header.entrySet().forEach(System.out::println);


            System.out.println("***************************************");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return bufferedReader;
        } catch (MalformedURLException m) {
            m.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}
