/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.EventManagement;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mygame.GameManager;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author alais
 */
public class EventSystem extends GameManager {
    private List<Event> events;
    
    // Specific Event Fields
    
    public EventSystem() {
        events = new ArrayList<>();
    }
    
    public void loadEvents() {
        try {
            String filePath = "EventManagement/EventList.xml";
            
            // Create a DocumentBuilderFactory and DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Load the XML file and parse it into a Document
            Document document = builder.parse(new File(filePath));
            document.getDocumentElement().normalize();

            // Get the list of event nodes
            NodeList eventList = document.getElementsByTagName("event");

            for (int i = 0; i < eventList.getLength(); i++) {
                Element eventElement = (Element) eventList.item(i);

                // Read event data from XML
                String name = eventElement.getElementsByTagName("name").item(0).getTextContent();
                String description = eventElement.getElementsByTagName("description").item(0).getTextContent();
                String consequenceAction = eventElement.getElementsByTagName("action").item(0).getTextContent();

                // Create an Event object
                Event event = new Event(name, description, () -> {
                    // You can map actions here based on consequenceAction string
                    switch (consequenceAction) {
                        case "lightFlickering":
                            // Run flashback sequence
                            System.out.println("The lights are flickering");
                            break;
                        case "respawnNormal":
                            // Respawn the player
                            System.out.println("Respawn in normal bathroom");
                            Vector3f bathroomPosition = this.bathroomBounds.getCenter();
                            this.respawnPlayer(bathroomPosition);
                            break;
                        case "respawnAltered":
                            // Respawn the player
                            System.out.println("Respawn in altered bathroom");
                            break;
                        default:
                            System.out.println("");
                    }
                });

                // Add the event to the list
                events.add(event);
            }
        } catch (Exception e) {
            System.out.println("Cannot load events");
        }
    }
    
    public void removeEvent(String name) {
        events.removeIf(event -> event.getName().equals(name));
    }
    
    public Event getEventByName(String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return event;
            }
        }
        return null;
    }
    
    // For testing purposes
    public void triggerAllEvents() {
        for (Event event:events) {
            event.triggerEvent();
        }
    }
    
    public void resetAllEvents() {
        for (Event event : events) {
            event.resetEvent();
        }
    }
    
    public List<String> listEventNames() {
        List<String> eventNames = new ArrayList<>();
        for (Event event : events) {
            eventNames.add(event.getName());
        }
        return eventNames;
    }
    
    // Event Functions
    public void respawnPlayer(Vector3f bathroomPosition) {
        this.player.setPosition(bathroomPosition);
        System.out.println("Respawned player in bathroom.");
    }
}