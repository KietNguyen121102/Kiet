import java.util.*;

// class for handling graph
public class Graph {
    public HashMap<Integer, Vertex> map = new HashMap<>(); // hashmap for G(V,E)
    private int radiusExtra = 2; // misc for use later
    private boolean removeNodeUsingCI; // in case of -d

    // class init
    public Graph(int radiusExtra, boolean removeNodeUsingCI) {
        this.radiusExtra = radiusExtra;
        this.removeNodeUsingCI = removeNodeUsingCI;
    }

    // add to graph
    public void add(Integer v1, Integer v2) {
        // check if already exists, if not create new one, if yes, add to current node
        if(!map.containsKey(v1)) {
            Vertex v = new Vertex();
            v.edge.add(v2);
            map.put(v1, v);
        }
        else { 
            map.get(v1).edge.add(v2);
        }

        if(!map.containsKey(v2)) {
            Vertex v = new Vertex();
            v.edge.add(v1);
            map.put(v2, v);
        }
        else { 
            map.get(v2).edge.add(v1);
        }
    }

    // debug method: print out the adjacency list of the graph
    public void print() { 
        for(Map.Entry graphElement : map.entrySet()) {
            System.out.print(graphElement.getKey()  + " -> ");
            Vertex value = (Vertex) graphElement.getValue();
            for(int i : value.edge) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    // get degree of desired node with number v
    public int getDegree(int v) {
        return map.get(v).edge.size();
    }

    // remove node using the degree algo specified in the handout
    public void removeNodeUsingDegree() {
        // flags used to pick out the highest degree
        int highestDegree = 0;
        int highestDegreeVertex = 0;

        // loop through to find highest degree
        for(Map.Entry graphElement : map.entrySet()) {
            int v = (int) graphElement.getKey();
            int currentDegree = getDegree(v);
            if(getDegree(v) > highestDegree) {
                highestDegree = currentDegree;
                highestDegreeVertex = v;
            }
        }

        // remove the node that has the highest degree and update the graph
        update(highestDegreeVertex);

        // print out removed nodes and its degree
        System.out.println(highestDegreeVertex + " " + highestDegree);
    }

    // method to handle removing and updating the graph
    public void update(int v) {
        Vertex node = map.get(v);
        ArrayList<Integer> adjList = node.edge;

        for(int adj : adjList) {
            map.get(adj).edge.remove(new Integer(v));
        }

        // special case when using CI algo
        if(removeNodeUsingCI) {
            for(VertexWithDistance ballVertex : node.ballArray) {
                map.get(ballVertex.v).reset();  // reset ball of every node in ball r + 1 of v
                ball(ballVertex.v, radiusExtra); // recalculate the CI of affected nodes
            }
        }
        
        map.remove(v); //remove node v from graph
    }
 
    // method to remove node using collective influence algo
    public void removeNode() {

        if(!removeNodeUsingCI) {
            removeNodeUsingDegree();
            return;
        }

        // flages used to pick out highest CI
        int highestCI = 0;
        int highestCIVertex = 0;

        // loop through the map
        for(HashMap.Entry graphElement : map.entrySet()) {
            Vertex v = (Vertex) graphElement.getValue();
            int vNum = (int) graphElement.getKey();
            int currentCI = v.collectiveInfluence;
            if(currentCI > highestCI) {
                highestCI = currentCI;
                highestCIVertex = vNum;
            }
        }
        
        // handle cases where can't compute highest CI
        if(highestCIVertex == 0) {
            System.out.println("The current nodes's collective influence cannot be calculated since the choosen radius is too big for thetaBall()");
            return;
        }
        Vertex node = map.get(highestCIVertex);
        ArrayList<Integer> adjList = node.edge;

        // remove node with highest CI and update the graph
        update(highestCIVertex);
        
        // print out removed nodes
        System.out.println(highestCIVertex + " " + highestCI);

    }

    // method to calculate CI of desired node
    public int calculateCI(int v) {
        int degree = getDegree(v); // get degree
        int sumOfThetalBallDegree = 0; // for later
        Vertex node = map.get(v);
        for(int adj : node.thetaBallArray) {
            // System.out.println(adj);
            sumOfThetalBallDegree += getDegree(adj) -1; 
        }
        // according to formula, CI = (k-i) sigma_{j in thetaball of i} (k_j-1)
        node.collectiveInfluence = (degree - 1) * sumOfThetalBallDegree;

        return (degree - 1) * sumOfThetalBallDegree;
        
    }

    // find ball of desired node and in given radius
    public void ball(int v, int radius) {
        Vertex node = map.get(v);
        ArrayList<VertexWithDistance> resultBall = new ArrayList<>();
        int r = 3;
        // actually find the ball of r+1 then add theta ball later
        r = radius + 1;
        
        // make all nodes white
        resetAll();

        // change color of source node
        node.color = 1;
        node.dist = 0;
        node.parent = null;

        ArrayList<Integer> queue = new ArrayList<>();
        queue.add(v);

        // begin BFS to find ball
        while(queue.size() > 0) {
            int remove = queue.remove(0);
            Vertex removeNode = map.get(remove);
            for(int adj : removeNode.edge) {
                Vertex adjNode = map.get(adj);
                if(adjNode.color == 0 && removeNode.dist + 1 <= r) {
                    adjNode.color = 1;
                    adjNode.dist = removeNode.dist + 1;
                    adjNode.parent = removeNode;
                    queue.add(adj);
                    resultBall.add(new VertexWithDistance(adj, adjNode.dist));
                }
            }
            removeNode.color = 2;
        }
        
        // change the ballArray of node v
        node.setBallArray(resultBall, radius);
        calculateCI(v);
    }

    // calculate ball of every nodes in the graph
    public void calculateAll() {
        for(Map.Entry graphElement : map.entrySet()) {
            ball((int) graphElement.getKey(), radiusExtra);
        }
    }

    // reset every node's color to white, distance to infinity, parent to null
    public void resetAll () {
        for(Map.Entry graphElement : map.entrySet()) {
            Vertex vertex = (Vertex) graphElement.getValue();
            vertex.color = 0; // set color to white
            vertex.dist  = -1; // set distance to infinity
            vertex.parent = null; // set parent to null;
        }
    }

    // DFS to search for connected component
    public void DFS(int v, ArrayList<Integer> component) {
        Vertex node = map.get(v);
        node.color = 1;
        for(int adj : node.edge) {
            Vertex adjNode = map.get(adj);
            if(adjNode.color == 0) {
                adjNode.parent = node;
                DFS(adj, component);
            }
        }
        node.color = 2;
        component.add(v);

    }


    // to handle -t cases: print out all connected components of the graph
    public void trace() {
        resetAll();
        int numberOfComp = 0;
        for(Map.Entry graphElement : map.entrySet()) {
            Vertex node = (Vertex) graphElement.getValue();
            if(node.color == 0) {
                ArrayList<Integer> newComp = new ArrayList<>();
                DFS((int) graphElement.getKey(), newComp);
                numberOfComp++;
                Collections.sort(newComp);

                System.out.print("Connected component #" + numberOfComp + ": ");
                
                for(int v : newComp) {
                    System.out.print(v + " ");
                }
                System.out.println();

                
            }

        }

    }
}