import java.util.*;

public class Vertex {
    public int color = 0;  //0 is white, 1 is gray, 2 is black
    public int dist = -1; // -1 is infinity
    public Vertex parent = null;
    public int collectiveInfluence;
    public ArrayList<Integer> edge;
    public ArrayList<VertexWithDistance> ballArray; // ball of r+1
    public ArrayList<Integer> thetaBallArray; // thetaball of r

    public Vertex() {
        edge = new ArrayList<>();
        ballArray = new ArrayList<>();
        thetaBallArray = new ArrayList<>();
    }


    // change ballArray of vertex, also to pick out the suitable ball for thetaBall
    public void setBallArray(ArrayList<VertexWithDistance> ballArray, int r) {
        this.ballArray = ballArray;
        setThetaBallArray(r);
    }

    // pick out suitable node for thetaBall
    public void setThetaBallArray(int r) {
        for(VertexWithDistance vertex : ballArray) {
            if(vertex.getDistance() == r) {
                thetaBallArray.add(vertex.v);
            }

        }
    }

    // reset for used when update()
    public void reset() {
        ballArray = new ArrayList<>();
        thetaBallArray = new ArrayList<>();
    }

}


        // public Vertex(int num) {
        //     this.num = num;
        // }

        // public void setCI(int CI) {
        //     collectiveInfluence = CI;
        // }

        // public int getCI() {
        //     return collectiveInfluence;
        // }

        // public void setColor(int color) {
        //     this.color = color;
        // }

        // public int getColor() {
        //     return color;
        // }

        // public void setDist(int dist) {
        //     this.dist = dist;
        // }

        // public int getDist() {
        //     return dist;
        // }
        
        // public void setParent(Vertex parent) {
        //     this.parent = parent;
        // }