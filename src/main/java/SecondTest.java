import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class SecondTest {
    private static final String SAFETYBELT = "safetyBelt";
    private static final String SPEEDING = "speeding";
    private static final String DRIVINGONRED = "drivingOnRed";
    public static void calculate(String fileName) {
        Double safetyBelt = 0.0;
        Double speeding = 0.0;
        Double drivingOnRed = 0.0;
        try {
            List<File> file = Files.walk(Paths.get(fileName))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(it -> it.length() != 0)
                    .collect(Collectors.toList());
            JSONParser parser = new JSONParser();
            for (File value : file) {
                String name = "file/" + value.getName();
                JSONArray array = (JSONArray) parser.parse(new FileReader(name));
                for (Object o : array) {
                    JSONObject fines = (JSONObject) o;
                    String fine = (String) fines.get("type");
                    Double price = (Double) fines.get("fine_amount");
                    switch (fine) {
                        case ("SAFETYBELT"):
                            safetyBelt += price;
                            break;
                        case ("SPEEDING"):
                            speeding += price;
                            break;
                        case ("DRIVINGONRED"):
                            drivingOnRed += price;
                        default:
                            break;
                    }
                }
            }
            HashMap<String, Double> fines = new HashMap<>();
            fines.put(SAFETYBELT, safetyBelt);
            fines.put(SPEEDING, speeding);
            fines.put(DRIVINGONRED, drivingOnRed);
            writeToXML(fines, "resultTestCountAllPriceFine.xml");
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Can`t write file ", e);
        }
    }

    private static void writeToXML(HashMap<String, Double> fines, String fileName)
            throws IOException {
        Document doc = new Document();
        doc.setRootElement(new Element("Fines"));
        List<Element> collect = fines.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).map(it -> {
                    Element fineElement = new Element("fine");
                    fineElement.setAttribute("fine_name", it.getKey());
                    fineElement.setAttribute("count", it.getValue().toString());
                    return fineElement;
                })
                .collect(Collectors.toList());
        doc.getRootElement().addContent(collect);
        XMLOutputter xmlWriter = new XMLOutputter(Format.getPrettyFormat());
        xmlWriter.output(doc, new FileOutputStream(fileName));
    }
}
