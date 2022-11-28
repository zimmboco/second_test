import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class FirstTest {

    public void refactorNameXml(String fileName) {
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader =
                    xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter writer =
                    xmlOutputFactory.createXMLEventWriter(new FileOutputStream("resultTestRefactorName.xml"));
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("person")) {
                        String surname =
                                startElement.getAttributeByName(new QName("surname")).getValue();
                        String name = startElement.getAttributeByName(new QName("name")).getValue();
                        Iterator<Attribute> attributes = startElement.getAttributes();
                        List<Attribute> newAttributes = new ArrayList<>();
                        while (attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            if (attribute.getName().getLocalPart().equals("name")) {
                                Attribute attribute1 =
                                        eventFactory.createAttribute(attribute.getName(),
                                                name + " " + surname);
                                newAttributes.add(attribute1);
                            } else if (attribute.getName().getLocalPart().equals("surname")) {
                                continue;
                            } else {
                                newAttributes.add(attribute);
                            }
                        }
                        nextEvent = eventFactory.createStartElement(new QName("person"),
                                newAttributes.iterator(),
                                startElement.getNamespaces());
                    }
                }
                writer.add(nextEvent);
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

        public void refactorNameAndSaveFormatXml(String fileName) {
        FileInputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(fileName);
            fileOutputStream = new FileOutputStream("resultRefactorNameAndSaveFormat.xml");
            sc = new Scanner(inputStream, "UTF-8");
            String result = null;
            boolean personStart = false;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (result == null) {
                    result = line;
                } else {
                    result = result + "\n" + line;
                }

                if (line.contains("<person")) {
                    personStart = true;
                }
                if (personStart && line.contains("/>")) {
                    personStart = false;
                    int name = result.indexOf(" name");
                    String surnameString = getSurnameString(result, " surname");
                    if (name >= 0) {
                        int indexNameStart = result.indexOf("\"", name) + 1;
                        int indexNameEnd = result.indexOf("\"", indexNameStart);
                        result = String.format("%s %s%s", result.substring(0, indexNameEnd), surnameString, result.substring(indexNameEnd));
                        result = removeSurname(result, " surname");
                    }
                }
                if (!personStart) {

                    assert result != null;
                    byte[] bytes = result.getBytes();
                    fileOutputStream.write(bytes);
                    result = "";
                }
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    assert fileOutputStream != null;
                    fileOutputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (sc != null) {
                sc.close();
            }
        }
    }

    private static String getSurnameString(String result, String target) {
        int surname = result.indexOf(target);
        if (surname >= 0) {
            int indexSurnameStart = result.indexOf("\"", surname) + 1;
            String substring = result.substring(indexSurnameStart,
                    result.indexOf("\"", indexSurnameStart));
            return substring;
        } else {
            return null;
        }
    }

    private static String removeSurname(String result, String target) {
        int surname = result.indexOf(target);
        if (surname >= 0) {
            int indexSurnameStart = result.indexOf("\"", surname) + 1;
            int indexSurnameFinish = result.indexOf("\"", indexSurnameStart) + 1;
            result =
                    result.substring(0, surname) + result.substring(indexSurnameFinish);
            return result;
        } else {
            return null;
        }
    }
}
