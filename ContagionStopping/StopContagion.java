import java.io.*;
import java.util.*;


public class StopContagion {



 
 
    public static void main(String[] args) {
        // default init
        boolean removeNodeUsingCI = true;
        boolean doTracing = false;
        int radius = 2;
        int num_nodes = 0;
        String input_file = "";

        // handle input args
        for(int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "-t":
                    doTracing = true;
                    break;
                case "-d": 
                    removeNodeUsingCI = false;
                    break;
                case "-r":
                    radius = Integer.valueOf(args[1 + i]);
                    i++;
                    break;
                default:
                    num_nodes = Integer.valueOf(args[i]);
                    input_file = args[i+1];
                    i++;
            }
        }

        // init new graph
        Graph graph = new Graph(radius, removeNodeUsingCI);

        // scan for input
        try {
            File input = new File(input_file);
            Scanner reader = new Scanner(input);

            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] vertexData = data.split("\\s+");
                graph.add(Integer.valueOf(vertexData[0]), Integer.valueOf(vertexData[1]));
            }
            reader.close();
        } catch (FileNotFoundException e) {
        e.printStackTrace();
        }


        // graph calculate all to find the ball of all nodes
        graph.calculateAll();
        for(int i = 0; i < num_nodes; i++) {
            graph.removeNode(); 
            // graph.print();

        }

        // to handle tracing
        if(doTracing) {
            System.out.println();
            graph.trace();
        }
        
        

    }
}