Project Title: Arnav's Indian Travel Optimizer!

Could run two different programs:


**(1) Interactive.java:** We want to plan a fun yet efficient trip for the user of this optimizer.
This program asks the user to input which of the 100 cities they are starting from and want to end at
(check last page of the user manual for list of these 100 cities). The goal is to get the user to explore cities
outside of the current city, so the start and end city should be different. The inputs and outputs are not case-sensitive,
however the minimum number of stops have to be more than 0 and less than the number of cities in our database.
The output of our program will be the path the traveller should take from city 1 to city 2, which is optimized for the lowest
driving distance (mileage not time), and having at least as many stops as teh user specified.
Lastly, the user can also request the program for tourist attractions and weather information for the city they are going to.

**(2) Analysis.java:** As the the Indian government is focusing on developing its infrastructure, they want to focus on increase the number
of travellers pouring into the country. They realize this can be done by primarily improving the infrastructure of major cities that tourists will transit through and visit as they plan trips across India. This is where such an optimizer comes in handy as it can generally expect
which cities would be the best commuter cities geographically, and hence where the Indian government should spend their resources for maximal economic benefit.
The frequencies of the number of times these cities were including in a travel route between all the combination of cities,
are included for minimum stops of 1, 2, and 3. Further analysis and functionality information is provided in the user manual.
This code takes approximately 10 minutes to run due to it traversing and running the Dijkstra graph algorithm on approximately 30 thousand pairs of data.

Technical Information: 
Used jsoup to find 100 major cities that travellers frequently commute on. parsed html to find driving distance between
cities. However, not every combination of cities had a driving distance (assuming there is a road in between cities), so
I created a script on python using a google API and a distance matrix to calculate the distances (set to driving mode)
between each city or node. These were the edges that I copy pasted form teh python output into the text file (data/distanceData), and I useda bufferReader to read each line and create a graph with edge weights.
The data in data/distanceData.txt is directly from the python script in the data folder. My Google API key should work as it is unrestricted.

**Graph and graph algorithms/Social networks:** using this retrieved data, I built a directed graph G = (V, E), where V is the set of all 100 cities (|V| = 100) and E is the set of
edges between each pair of cities with the weight of each edge being actual distance between cities.
Used a modified Dijkstra's algorithm to find optimized path between user inputs (start city and end city)
Added additional user input asking for minimum stops wanted by user within their journey from start -> end.
Dijkstra's then runs with those parameters and gives an optimized route with at least those many stops in between, returning the
path and the driving distance for the total journey and each segment of the journey.

