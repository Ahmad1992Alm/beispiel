package beispiel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple representation of a graph derived from an IndoorGML file.
 */
public class IndoorGMLGraph {

    /** Representation of a cell space node in the graph. */
    public static class GraphNode {
        public final String id;
        public final String label;

        public GraphNode(String id, String label) {
            this.id = id;
            this.label = label;
        }
    }

    /** Representation of an edge between two cell spaces. */
    public static class GraphEdge {
        public final GraphNode from;
        public final GraphNode to;

        public GraphEdge(GraphNode from, GraphNode to) {
            this.from = from;
            this.to = to;
        }
    }

    private final List<GraphNode> nodes = new ArrayList<>();
    private final List<GraphEdge> edges = new ArrayList<>();

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    /**
     * Parses an IndoorGML file and builds a graph consisting of cell spaces and transitions.
     */
    public static IndoorGMLGraph fromFile(File file) throws Exception {
        IndoorGMLGraph graph = new IndoorGMLGraph();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(file);

        String gmlNs = "http://www.opengis.net/gml/3.2";
        String xlinkNs = "http://www.w3.org/1999/xlink";

        // Parse cell spaces and create graph nodes
        Map<String, GraphNode> cellMap = new HashMap<>();
        NodeList cellSpaces = doc.getElementsByTagNameNS("*", "CellSpace");
        for (int i = 0; i < cellSpaces.getLength(); i++) {
            Element cs = (Element) cellSpaces.item(i);
            String id = cs.getAttributeNS(gmlNs, "id");
            if (id == null || id.isEmpty()) {
                id = cs.getAttribute("id");
            }
            String label = id;
            NodeList names = cs.getElementsByTagNameNS(gmlNs, "name");
            if (names.getLength() > 0) {
                label = names.item(0).getTextContent();
            }
            GraphNode node = new GraphNode(id, label);
            graph.nodes.add(node);
            cellMap.put(id, node);
        }

        // Map states to cell space nodes
        Map<String, GraphNode> stateToNode = new HashMap<>();
        NodeList states = doc.getElementsByTagNameNS("*", "State");
        for (int i = 0; i < states.getLength(); i++) {
            Element st = (Element) states.item(i);
            String sid = st.getAttributeNS(gmlNs, "id");
            if (sid == null || sid.isEmpty()) {
                sid = st.getAttribute("id");
            }
            NodeList duals = st.getElementsByTagNameNS("*", "duality");
            if (duals.getLength() > 0) {
                Element dual = (Element) duals.item(0);
                String href = dual.getAttributeNS(xlinkNs, "href");
                if (href.startsWith("#")) {
                    href = href.substring(1);
                }
                GraphNode node = cellMap.get(href);
                if (node != null) {
                    stateToNode.put(sid, node);
                }
            }
        }

        // Parse transitions and create edges between the connected cell spaces
        NodeList transitions = doc.getElementsByTagNameNS("*", "Transition");
        for (int i = 0; i < transitions.getLength(); i++) {
            Element tr = (Element) transitions.item(i);
            NodeList connects = tr.getElementsByTagNameNS("*", "connects");
            GraphNode a = null;
            GraphNode b = null;
            for (int j = 0; j < connects.getLength(); j++) {
                Element c = (Element) connects.item(j);
                String href = c.getAttributeNS(xlinkNs, "href");
                if (href.startsWith("#")) {
                    href = href.substring(1);
                }
                GraphNode node = stateToNode.get(href);
                if (node != null) {
                    if (a == null) {
                        a = node;
                    } else if (b == null) {
                        b = node;
                        break;
                    }
                }
            }
            if (a != null && b != null) {
                graph.edges.add(new GraphEdge(a, b));
            }
        }
        return graph;
    }
}
