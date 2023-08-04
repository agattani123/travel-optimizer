
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Interactive {
    public static void main(String[] args) throws IOException {

        WebScraper ws = new WebScraper();
        ArrayList<ArrayList<String>> scraped_data = ws.getCityDistances(); // gets major cities
        ArrayList<ArrayList<String>> scraped_data2 = ws.getCityData2(); // get their combinations from python script
        ArrayList<ArrayList<String>> final_data = ws.finalCityData(scraped_data, scraped_data2); // merges this data together
        HashSet<String> all_cities = ws.num_nodes(final_data); // provides a set of all the cities with no duplicates
        HashMap<String, ArrayList<WebScraper.Pair>> city_adj_list =  ws.makeAdjList(final_data); // creates adjacency list for graph

        System.out.println("Welcome to Arnav's Indian Travel Optimizer!\n" +
                "Out of the 100 major cities outlines in the user manual, \n" +
                "please indicate which Indian city you are going to leave from:");
        Scanner sc = new Scanner(System.in);
        String start_city = sc.nextLine(); // get start city
        start_city = start_city.toLowerCase(); // make string all lowercase
        start_city = start_city.substring(0, 1).toUpperCase() + start_city.substring(1); // capitalize first letter
        while (!all_cities.contains(start_city)){
            System.out.println("This city is not in our database, \n" +
                    "please again choose the start city from those provided: ");
            start_city = sc.nextLine(); // get start city
            start_city = start_city.toLowerCase(); // make string all lowercase
            start_city = start_city.substring(0, 1).toUpperCase() + start_city.substring(1); // capitalize first letter
        }
        System.out.println("Please enter the end city in your journey");
        String end_city = sc.nextLine(); // get start city
        end_city = end_city.toLowerCase(); // make string all lowercase
        end_city = end_city.substring(0, 1).toUpperCase() + end_city.substring(1); // capitalize first letter
        while (!all_cities.contains(end_city)){
            System.out.println("This city is not in our database, \n" +
                    "please again choose the end city from those provided: ");
            end_city = sc.nextLine(); // get start city
            end_city = end_city.toLowerCase(); // make string all lowercase
            end_city = end_city.substring(0, 1).toUpperCase() + end_city.substring(1); // capitalize first letter
        }

        if (start_city.equals(end_city)){
            System.out.println("Since you are already in " + start_city + ", you should explore local tourist attractions here!");
        } else {
            System.out.println("Please enter the minimum number of cities you want to explore in between your journey!");
            String stops = sc.nextLine(); // get user input for number of stops
            while (Integer.parseInt(stops) < 0 || Integer.parseInt(stops) > 50) {
                System.out.println("You must choose a number with at least 0 stops \n" +
                        "(0 being you want a direct path between " + start_city + " and " + end_city + ") \n" +
                        "Please re-enter a valid minimum number of stops between these two cities: ");
                stops = sc.nextLine(); // reprompt due to invalid input
            }
            int min_stops = Integer.parseInt(stops) + 2; // this value includes the start and end node
            ArrayList<String> shortest_path;
            try {
                shortest_path = ws.dijkstra(min_stops, city_adj_list, start_city, end_city);
            } catch (IndexOutOfBoundsException e){
                System.out.println("Could not find any path between " + start_city + " and " + end_city +
                        " with a minimum of " + Integer.parseInt(stops) + " stops in between");
                System.out.println("Please rerun the program with different parameters");
                return;
            }
            Double shortest_distance = ws.getShortest_distance();

            System.out.println("The shortest path between " + start_city + " and " + end_city + " with a minimum " +
                    "of " + Integer.parseInt(stops) + " stops is:");

            String listToString = shortest_path.toString();
            listToString = listToString.replace("[", "").replace("]", "").replace(",", " -->");
            System.out.println(listToString.trim()); // output the shortest path in a readable manner

            System.out.println(""); // next line
            Double shortest_dist_miles = shortest_distance * 0.621371; // convert km to miles
            String truncated_short_mile = String.format("%.2f", shortest_dist_miles);
            shortest_dist_miles = Double.parseDouble(truncated_short_mile); // want to truncate to fewer decimal places (rounded)
            System.out.println("The total driving distance along this route will be: " + shortest_distance + " km" + " or " +
                    shortest_dist_miles + " mi");
            System.out.println(" ");
            System.out.println("Your itinerary is as follows: ");
            for (int i = 0; i < shortest_path.size() - 1; i++) { // get optimized distance between each stop along the way
                String city_one = shortest_path.get(i);
                String city_two = shortest_path.get(i + 1);
                ArrayList<String> new_shortest_path = ws.dijkstra(2, city_adj_list, city_one, city_two);
                Double new_shortest_distance = ws.getShortest_distance();
                System.out.println("Driving distance between " + city_one + " and " + city_two + " is: " + new_shortest_distance + " km");
            }

            System.out.println(" ");
            System.out.println("We are one step closer to planning your ideal trip! \n" +
                    "Do you want some more information about these cities? \n" +
                    "Type \"all\" to get information about all these cities, or else just type in the city of your choice!");
            String ans = sc.nextLine(); // get user input for number of stops
            ans = ans.toLowerCase(); // make string all lowercase
            ans = ans.substring(0, 1).toUpperCase() + ans.substring(1).trim(); // capitalize first letter
            while (!all_cities.contains(ans) && !ans.equals("All")){
                System.out.println("This is an invalid city name (not in our database) \n" +
                        "Please reenter a valid input below, or type \"all\" for information on all cities \n" +
                        "in your calculated travel path: ");
                ans = sc.nextLine(); // get user input for number of stops
                ans = ans.toLowerCase(); // make string all lowercase
                ans = ans.substring(0, 1).toUpperCase() + start_city.substring(1); // capitalize first letter
            }
            if (ans.equals("All")) { // want infromation for all the cities
                // for all cities
                for (String city_info : shortest_path) {
                    System.out.println("Here's some info about " + city_info + ":");
                    String fileName = "src/cityTouristInfo/" + city_info + ".txt"; // enter the file name
                    RouteReader reader = new RouteReader(fileName); // use reader to read the file (check RouteReader class)
                    List<String> arr = reader.readLines();
                    for (String x : arr) {
                        System.out.println(x);
                    }
                    System.out.println(" ");
                }
            } else {
                if (all_cities.contains(ans)){ // want information specific to that city
                    System.out.println("Here's some info about " + ans + ":");
                    String fileName = "src/cityTouristInfo/" + ans + ".txt"; // enter the file name
                    RouteReader reader = new RouteReader(fileName); // use reader to read the file (check RouteReader class)
                    List<String> arr = reader.readLines();
                    for (String x : arr) {
                        System.out.println(x);
                    }
                }

            }
        }
    }

}
