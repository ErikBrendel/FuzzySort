package fuzzysort.solver;

import java.util.*;
import java.util.function.BiConsumer;

public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    public final int dimensions;
    private final float forceRelation;
    private final Random r;

    public Graph(int dimensions, float forceRelation, Random r) {
        this.dimensions = dimensions;
        this.forceRelation = forceRelation;
        this.r = r;
    }

    public synchronized Node getNode(String item) {
        for (Node n: nodes) {
            if (n.item.equals(item)) return n;
        }
        Node newNode = new Node(item, dimensions, r);
        nodes.add(newNode);
        return newNode;
    }

    public synchronized Edge connect(Node n1, Node n2, float desiredDiff, float strength) {
        Edge edge = new Edge(n1, n2, desiredDiff, strength);
        n1.edges.add(edge);
        n2.edges.add(edge);
        edges.add(edge);
        return edge;
    }

    public synchronized void solveForces(float amount) {
        Map<Node, ForceVector> forces = getForceMap();
        float maxSqrMagnitude = 0;
        for (ForceVector force: forces.values()) {
            maxSqrMagnitude = Math.max(maxSqrMagnitude, force.getSqrMagnitude());
        }
        float maxMagnitude = (float) Math.sqrt(maxSqrMagnitude);

        float multiplier = amount / maxMagnitude;

        for (Node node: nodes) {
            node.applyForce(forces.get(node).scale(multiplier));
        }
    }

    private Map<Node, ForceVector> getForceMap() {
        Map<Node, ForceVector> forces = new HashMap<>();
        for (Node node: nodes) {
            ForceVector force = ForceVector.nullFor(node);
            for (Edge edge: node.edges) {
                force.add(edge.getForceFor(node));
            }
            forces.put(node, force);
        }
        return forces;
    }

    public synchronized List<String> getItemOrder() {
        nodes.sort((n1, n2) -> (int)Math.signum(n2.mainCoordinate - n1.mainCoordinate));

        List<String> items = new ArrayList<>(nodes.size());
        for (Node node: nodes) {
            items.add(node.item);
        }
        return items;
    }

    public synchronized boolean areConnected(Node n1, Node n2) {
        if (n1 == n2) return true;
        Set<Node> foundNodes = new HashSet<>();
        Queue<Node> openNodes = new LinkedList<>();
        openNodes.add(n1);
        foundNodes.add(n1);
        while (!openNodes.isEmpty()) {
            Node current = openNodes.remove();
            for (Edge edge: current.edges) {
                Node next = edge.getOtherNode(current);
                if (next == n2) return true;
                if (!foundNodes.contains(next)) {
                    foundNodes.add(next);
                    openNodes.add(next);
                }
            }
        }
        return false;
    }

    public synchronized void search(Node source, BiConsumer<Node, Integer> handler) {
        Set<Node> found = new HashSet<>(nodes.size(), 1);
        Queue<Node> open = new LinkedList<>();
        int depth = 0;
        open.add(source);
        found.add(source);
        open.add(null);
        while (open.size() > 1) {
            Node current = open.remove();
            if (current == null) {
                open.add(null);
                depth++;
                continue;
            }
            handler.accept(current, depth);
            for (Edge edge: current.edges) {
                Node next = edge.getOtherNode(current);
                if (!found.contains(next)) {
                    found.add(next);
                    open.add(next);
                }
            }
        }
    }

    public List<Node> getAllNodes() {
        return nodes;
    }

    public List<Edge> getAllEdges() {
        return edges;
    }

    public synchronized void disturb(float strength) {
        for (Node node: nodes) {
            node.applyForce(ForceVector.randomDirection(r, node).scale(strength));
        }
    }

    public static class Node {
        private final List<Edge> edges = new ArrayList<>();

        public final String item;
        public float mainCoordinate;
        public float[] otherCoordinates;

        public Node(String item, int dimensions, Random r) {
            this.item = item;
            mainCoordinate = r.nextFloat() * 100;
            otherCoordinates = new float[dimensions];
            for (int i = 0; i < dimensions; i++) {
                otherCoordinates[i] = r.nextFloat() * 100;
            }
        }

        public float getDiffTo(Node other) {
            return other.mainCoordinate - mainCoordinate;
        }

        public void applyForce(ForceVector force) {
            mainCoordinate += force.mainCoordinate;
            for (int i = 0; i < otherCoordinates.length; i++) {
                otherCoordinates[i] += force.otherCoordinates[i];
            }
        }

        public float getDistanceTo(Node other) {
            float mainDiff = mainCoordinate - other.mainCoordinate;
            float sum = mainDiff * mainDiff;
            for (int i = 0; i < otherCoordinates.length; i++) {
                float diff = otherCoordinates[i] - other.otherCoordinates[i];
                sum += diff * diff;
            }
            return (float)Math.sqrt(sum);
        }

        public int getEdgeCount() {
            return edges.size();
        }
    }

    public static class NodePair {
        public final Node n1;
        public final Node n2;

        NodePair(Node n1, Node n2) {
            this.n1 = n1;
            this.n2 = n2;
        }
    }

    public class Edge {
        public final Node n1;
        public final Node n2;
        private final float desiredDiff;
        private final float strength;

        Edge(Node n1, Node n2, float desiredDiff, float strength) {
            this.n1 = n1;
            this.n2 = n2;
            this.desiredDiff = desiredDiff;
            this.strength = strength;
        }

        public float getLength() {
            return n1.getDistanceTo(n2);
        }

        public float getLengthForce() {
            float desiredLength = Math.abs(desiredDiff);
            float actualLength = getLength();
            float delta = desiredLength - actualLength;
            return delta * strength * forceRelation;
        }

        public float getOrderForceFor(Node node) {
            float sign = node == n1 ? 1 : -1;
            float actualDiff = n1.getDiffTo(n2);
            float delta = desiredDiff - actualDiff;
            return sign * delta * strength * (1 - forceRelation);
        }

        public ForceVector getForceFor(Node node) {
            Node other = getOtherNode(node);
            ForceVector lengthForceVec = ForceVector.between(other, node).scale(getLengthForce());
            ForceVector orderForceVec = ForceVector.upFor(node).scale(getOrderForceFor(node));
            return lengthForceVec.add(orderForceVec);
        }

        public Node getOtherNode(Node oneEnd) {
            if (oneEnd == n1) {
                return n2;
            } else if (oneEnd == n2) {
                return n1;
            } else {
                throw new RuntimeException("Wrong node!");
            }
        }
    }
}
