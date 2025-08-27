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

    private final List<CellSpace> cellSpaces = new ArrayList<>();
    private final List<Transition> transitions = new ArrayList<>();

    public List<CellSpace> getCellSpaces() {
        return cellSpaces;
    }

    public List<Transition> getTransitions() {
        return transitions;
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

        // Parse cell spaces
        Map<String, CellSpace> cellMap = new HashMap<>();
        NodeList cellSpaces = doc.getElementsByTagNameNS("*", "CellSpace");
        for (int i = 0; i < cellSpaces.getLength(); i++) {
            Element cs = (Element) cellSpaces.item(i);
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
            graph.cellSpaces.add(cell);
            cellMap.put(id, cell);
        }

        // Map states to cell spaces
        Map<String, StatePoint> stateMap = new HashMap<>();
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
                }
            }
        }

        // Parse transitions and create edges between states
        NodeList transitions = doc.getElementsByTagNameNS("*", "Transition");
        for (int i = 0; i < transitions.getLength(); i++) {
            Element tr = (Element) transitions.item(i);
            NodeList connects = tr.getElementsByTagNameNS("*", "connects");
            StatePoint a = null;
            StatePoint b = null;
            for (int j = 0; j < connects.getLength(); j++) {
                Element c = (Element) connects.item(j);
                String href = c.getAttributeNS(xlinkNs, "href");
                if (href.startsWith("#")) {
                    href = href.substring(1);
                }
                StatePoint sp = stateMap.get(href);
                if (sp != null) {
                    if (a == null) {
                        a = sp;
                    } else if (b == null) {
                        b = sp;
                        break;
                    }
                }
            }
            if (a != null && b != null) {
                graph.transitions.add(new Transition(a, b));
            }
        }
        return graph;
    }
}
