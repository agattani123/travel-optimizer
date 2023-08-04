
// import statements
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class RouteReader{
    private BufferedReader reader;
    public RouteReader(String filePath) { // code is based on NETS 1500 recitation code
        try {
            this.reader = new BufferedReader(
                    new FileReader(filePath)
            );
        } catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<String> readLines() throws IOException { // code is based on NETS 1500 recitation code
        List<String> lines = new LinkedList<>();
        String line = this.reader.readLine();
        while (line != null){
            lines.add(line);
            line = this.reader.readLine();
        }
        this.reader.close();
        return lines;
    }
}
