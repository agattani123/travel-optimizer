import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

public class WebScraper {
    private static Double shortest_distance;
    private String baseURL;
    private Document currentDoc;

    public WebScraper() { // base url is the academy awards wikipedia page
        this.baseURL = "https://distancecalculator.globefeed.com/India_Distance_Calculator.asp";
        try {
            this.currentDoc = Jsoup.connect(this.baseURL).get(); // we set the currentDoc to this url
        } catch (IOException e){ // catch exception in case this page does not exist in the future, maybe)
            System.out.println("couldn't get the city distances link");
        }
    }

    public ArrayList<ArrayList<String>> getCityDistances() throws IOException {
        this.currentDoc = Jsoup.connect(this.baseURL).get();
        Elements data = this.currentDoc.select("tr:eq(2), tr");
        ArrayList<ArrayList<String>> city_data = new ArrayList<ArrayList<String>>(); // create adj list
        for (Element e: data){
            ArrayList<String> entry = new ArrayList<>(); // start_city, end_city, distance
            String[] args = e.text().split("\\)");
            if (args.length > 1) { // check if there are enough arguments to extract data from
                String start_city = args[0].split(" ")[0].trim(); // first city
                String end_city = args[1].split(" ")[1].trim(); // second city
                String driving_distance = args[2].trim(); // distance between these cities
                entry.add(start_city);
                entry.add(end_city);
                entry.add(driving_distance);
                if (entry.size() > 0) { // we add these values into an arraylist
                    city_data.add(entry); // add the entry into anotehr arraylist
                }
            }
        }
        return city_data;
    }
    public ArrayList<ArrayList<String>> getCityData2() throws IOException{
        String fileName = "src/data/distanceData.txt"; // enter the file name
        ArrayList<ArrayList<String>> city_data = new ArrayList<ArrayList<String>>(); // create adj list
        RouteReader reader = new RouteReader(fileName); // use reader to read the file (check RouteReader class)
        List<String> arr = reader.readLines();
        for (int i = 0; i < arr.size(); i++) {
            ArrayList<String> entry = new ArrayList<>();
            String curr = arr.get(i);
            String[] args = curr.split(" ");
            entry.add(args[0].trim());
            entry.add(args[1].trim());
            entry.add(args[2].trim().replace(",",""));
            city_data.add(entry);
        }
        return city_data;
    }

    public ArrayList<ArrayList<String>> finalCityData(ArrayList<ArrayList<String>> data1, ArrayList<ArrayList<String>> data2) throws IOException{
        ArrayList<ArrayList<String>> city_data = new ArrayList<ArrayList<String>>(); // create adj list
        for (ArrayList<String> entry_ds1 : data1){
            for (ArrayList<String> entry_ds2 : data2) {
                    if ((entry_ds1.get(0).equals(entry_ds2.get(0))) && (entry_ds1.get(1).equals(entry_ds2.get(1)))) {
                        if (Double.parseDouble(entry_ds1.get(2)) > Double.parseDouble(entry_ds2.get(2))) {
                            // merge data where if there are duplicates with same start city and same end city, then we take the
                            // pair with the higher driving distance value, so we are fair to the user of this program
                            // we give them a "worst case" solution
                            if (!city_data.contains(entry_ds1)) {
                                city_data.add(entry_ds1);
                            }
                        } else {
                            if (!city_data.contains(entry_ds2)) {
                                city_data.add(entry_ds2);
                            }
                        }
                    } else {
                        city_data.add(entry_ds2);
                    }
            }
        }
        return city_data;
    }

    public static ArrayList<String> dijkstra(int min_stops, HashMap<String, ArrayList<Pair>> adj_list, String source, String end) throws IOException{
        ArrayList<String> shortest_path = new ArrayList<>();
        HashMap<String, String> parents = new HashMap<String, String>();
        HashMap<String, Double> distances = new HashMap<String, Double>();
        Double infinity = Double.MAX_VALUE; // initialized infinity to 0xFFFF
        for (Map.Entry<String, ArrayList<Pair>> entry : adj_list.entrySet()){ // initialize shortest distance from source to every node as infinity
            String key = entry.getKey().trim();
            distances.put(key, infinity);
        }
        distances.put(source.trim(), Double.parseDouble("0")); // set source shortest distance to 0
        PriorityQueue<Pair> queue = new PriorityQueue<>((v1, v2) -> (int) (v1.getWeight() - v2.getWeight()));
        queue.add(new Pair(source.trim(), Double.parseDouble("0")));
        while (queue.size() > 0){
            Pair current = queue.poll(); // take item from top of queue and remove it
            ArrayList<Pair> new_p = adj_list.get(current.node);
            for (Pair n : new_p){
                if (distances.get(current.node) + n.getWeight() < distances.get(n.node)){ // relaxes edge if the new distance after adding weight is smaller than current distacne in array
                    parents.put(n.node, current.node); // we update parent array with parent of current node
                    Double new_distance = n.getWeight() + distances.get(current.getNode()); // update distance array with new distance from node to the source node
                    distances.put(n.getNode(), new_distance);
                    queue.add(new Pair(n.getNode(), distances.get(n.getNode()))); // repeat process by adding new pair into priority queue
                }
            }
        }
        for (String u = end; u != null; u = parents.get(u)){
            shortest_path.add(u);
        }
        int stops = shortest_path.size();
        if (stops < min_stops){ // remove edges between first node and second node in path till we get
           String node1 = shortest_path.get(0); // an optimized path with the specified minimum number of stops
           String node2 = shortest_path.get(1);
           ArrayList<Pair> pairs1 = adj_list.get(node1);
           ArrayList<Pair> new_pairs1 = new ArrayList<>();
           for (Pair y : pairs1){ // removes the first edge
               if (!(y.getNode().equals(node2))){
                   new_pairs1.add(y);
               }
           }
           adj_list.put(node1, new_pairs1);
           ArrayList<Pair> pairs2 = adj_list.get(node2);
           ArrayList<Pair> new_pairs2 = new ArrayList<>();
           for (Pair y : pairs2){ // removes the second edge
               if (!(y.getNode().equals(node1))){
                   new_pairs2.add(y);
               }
           }
           adj_list.put(node2, new_pairs2);
           return dijkstra(min_stops, adj_list, source, end); // re-runs dijkstra's till we get a solution
        }
        Collections.reverse(shortest_path); // if it passes the minimum stops check, we reverse the order of the shortest path array
        for (Map.Entry<String, Double> entry : distances.entrySet()){
            String key = entry.getKey().trim();
            if (key.equals(end)){
                shortest_distance = entry.getValue();
            } // we update private variable with teh shortest distance from latest dijkstra algorithm iteration
        }
        return shortest_path; // return shortest path
    }

    public HashMap<String, ArrayList<Pair>> makeAdjList(ArrayList<ArrayList<String>> data){
        HashMap<String, ArrayList<Pair>> adj_list = new HashMap<String, ArrayList<Pair>>();
        for (ArrayList<String> entry : data){
            String start_city = entry.get(0).trim();
            String end_city = entry.get(1).trim();
            Double distance = Double.parseDouble(entry.get(2).trim());
            Pair new_pair = new Pair(end_city, distance);
            if (adj_list.containsKey(start_city)){ // make adj list with each node (city) having an arraylist
                ArrayList<Pair> prev_pairs = adj_list.get(start_city); // the arraylist is an arraylist of pairs with the
                prev_pairs.add(new_pair); // node that key of teh hashmap has an edge to, and the weight of the edge
                adj_list.put(start_city, prev_pairs);
            } else {
                ArrayList<Pair> fresh_pair = new ArrayList<>();
                fresh_pair.add(new_pair);
                adj_list.put(start_city, fresh_pair);
            }
            // we also add edge from end to source
            Pair new_pair_reverse = new Pair(start_city, distance);
            if (adj_list.containsKey(end_city)){
                ArrayList<Pair> prev_pairs = adj_list.get(end_city);
                prev_pairs.add(new_pair_reverse);
                adj_list.put(end_city, prev_pairs);
            } else {
                ArrayList<Pair> fresh_pair = new ArrayList<>();
                fresh_pair.add(new_pair_reverse);
                adj_list.put(end_city, fresh_pair);
            }
        }
        return adj_list;
    }
    // get a set of the distinct cities in our dataset
    public HashSet<String> num_nodes(ArrayList<ArrayList<String>> adj_list){
        HashSet<String> distict_nodes = new HashSet<>(); // does not store duplicate values
        for (ArrayList<String> entry: adj_list){
            distict_nodes.add(entry.get(0));
            distict_nodes.add(entry.get(1));
        }
        return distict_nodes;
    }

    static class Pair{
        String node; // u -> v
        Double weight; // weight of that edge
        Pair(String v, Double w){
            node = v; // u -> v
            weight = w; // weight of u to v
        }
        String getNode(){
            return node;
        }
        Double getWeight(){
            return weight;
        }
    }

    public Double getShortest_distance(){
        return shortest_distance;
    }


}