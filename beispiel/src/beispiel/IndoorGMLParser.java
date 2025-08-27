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
 * Parses an IndoorGML file and produces cell spaces, transitions and an adjacency map.
 */
public class IndoorGMLParser {

    /**
     * Container for parsed data.
     */
    public static class ParsedData {
        private final List<CellSpace> cellSpaces;
        private final List<Transition> transitions;
        private final Map<String, List<String>> adjacencyGraph;

        public ParsedData(List<CellSpace> cellSpaces,
                          List<Transition> transitions,
                          Map<String, List<String>> adjacencyGraph) {
            this.cellSpaces = cellSpaces;
            this.transitions = transitions;
            this.adjacencyGraph = adjacencyGraph;
        }

        public List<CellSpace> getCellSpaces() { return cellSpaces; }
        public List<Transition> getTransitions() { return transitions; }
        public Map<String, List<String>> getAdjacencyGraph() { return adjacencyGraph; }
    }

    /**
     * Parses an IndoorGML file and builds cell spaces, transitions and adjacency relations.
     */
    public static ParsedData parse(File file) throws Exception {
        List<CellSpace> cellSpaces = new ArrayList<>();
        List<Transition> transitions = new ArrayList<>();
        Map<String, List<String>> adjacencyGraph = new HashMap<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(file);

        String gmlNs = "http://www.opengis.net/gml/3.2";
        String xlinkNs = "http://www.w3.org/1999/xlink";

        // Parse cell spaces
        Map<String, CellSpace> cellMap = new HashMap<>();
        NodeList cellSpaceNodes = doc.getElementsByTagNameNS("*", "CellSpace");
        for (int i = 0; i < cellSpaceNodes.getLength(); i++) {
            Element cs = (Element) cellSpaceNodes.item(i);
            String id = cs.getAttributeNS(gmlNs, "id");
            if (id == null || id.isEmpty()) {
                id = cs.getAttribute("id");
            }
            CellSpace cell = new CellSpace();
            // override generated id with GML id if present
            if (id != null && !id.isEmpty()) {
                try {
                    java.lang.reflect.Field f = CellSpace.class.getDeclaredField("id");
                    f.setAccessible(true);
                    f.set(cell, id);
                } catch (Exception ignored) {}
            }
            cellSpaces.add(cell);
            cellMap.put(id, cell);
            adjacencyGraph.put(cell.getId(), new ArrayList<>());
        }

        // Map states to cell spaces
        Map<String, StatePoint> stateMap = new HashMap<>();
        Map<String, String> stateToCellId = new HashMap<>();
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
                CellSpace cell = cellMap.get(href);
                if (cell != null) {
                    StatePoint sp = new StatePoint();
                    cell.setState(sp);
                    cell.setStateRef(sid);
                    stateMap.put(sid, sp);
                    stateToCellId.put(sid, cell.getId());
                }
            }
        }

        // Parse transitions and build adjacency
        NodeList transitionNodes = doc.getElementsByTagNameNS("*", "Transition");
        for (int i = 0; i < transitionNodes.getLength(); i++) {
            Element tr = (Element) transitionNodes.item(i);
            NodeList connects = tr.getElementsByTagNameNS("*", "connects");
            String sidA = null;
            String sidB = null;
            for (int j = 0; j < connects.getLength(); j++) {
                Element c = (Element) connects.item(j);
                String href = c.getAttributeNS(xlinkNs, "href");
                if (href.startsWith("#")) {
                    href = href.substring(1);
                }
                if (sidA == null) {
                    sidA = href;
                } else if (sidB == null) {
                    sidB = href;
                    break;
                }
            }
            if (sidA != null && sidB != null) {
                StatePoint a = stateMap.get(sidA);
                StatePoint b = stateMap.get(sidB);
                if (a != null && b != null) {
                    transitions.add(new Transition(a, b));
                }
                String cellIdA = stateToCellId.get(sidA);
                String cellIdB = stateToCellId.get(sidB);
                if (cellIdA != null && cellIdB != null) {
                    addNeighbor(adjacencyGraph, cellIdA, cellIdB);
                    addNeighbor(adjacencyGraph, cellIdB, cellIdA);
                }
            }
        }

        return new ParsedData(cellSpaces, transitions, adjacencyGraph);
    }

    private static void addNeighbor(Map<String, List<String>> map, String a, String b) {
        List<String> list = map.computeIfAbsent(a, k -> new ArrayList<>());
        if (!list.contains(b)) {
            list.add(b);
        }
    }
}

