import java.io.IOException;
import java.util.*;

public class Analysis {
    public static void main(String[] args) throws IOException {

        WebScraper ws = new WebScraper();
        ArrayList<ArrayList<String>> scraped_data = ws.getCityDistances();
        ArrayList<ArrayList<String>> scraped_data2 = ws.getCityData2();
        ArrayList<ArrayList<String>> final_data = ws.finalCityData(scraped_data, scraped_data2);
        HashSet<String> all_cities = ws.num_nodes(final_data);
        HashMap<String, ArrayList<WebScraper.Pair>> city_adj_list =  ws.makeAdjList(final_data);
        Map<String, Integer> commuter_freq = new TreeMap<>(Collections.reverseOrder());

        // comments are similar to Dijkstra's shortest path algorithm in the WebScraper file, we just rerun it
        // for every combination of cities as start city and end city with minimum stops going from 1 to 2 to 3,
        // for all iterations
        // we also do not run dijkstra's when the start and end cities are the same
        for (int stops = 1; stops <= 3; stops++) {
            System.out.println("minimum number of stops are: " + stops);
            for (String start_city : all_cities) {
                for (String end_city : all_cities) {
                    if (!start_city.equals(end_city)) {
                        int min_stops = stops + 2; // one not counting start and end node
                        try {
                            ArrayList<String> shortest_path = ws.dijkstra(min_stops, city_adj_list, start_city, end_city);
                            int index = shortest_path.size() - 1;
                            shortest_path.remove(index); // remove start and end node since we are only interested in commuter ones
                            shortest_path.remove(0);
                            for (String stop : shortest_path) {
                                if (commuter_freq.containsKey(stop)) {
                                    commuter_freq.put(stop, commuter_freq.get(stop) + 1);
                                } else {
                                    commuter_freq.put(stop, 1);
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                }
            }
            // output the frequencies of the cities that were found as "commuter cities"
            // meaning that they were in teh shortest path when calculating Dijkstra's for all teh combinations of cities
            System.out.println("The frequencies of cities commuted are as follows (for minimum of " + stops + " stops): ");
            for (Map.Entry<String, Integer> entry : commuter_freq.entrySet()) {
                String city = entry.getKey().trim();
                Integer freq = entry.getValue();
                System.out.println("frequency of times: " + city + " = " + freq + ", ");
            }
        }
    }
}
