/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.EventManagement;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mygame.GameManager;
import mygame.Player;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author alais
 */
public class EventSystem {
    private List<Event> events;
    
    // Specific Event Fields
    private Player player;
    private BoundingBox bathroomBounds;
    private Vector3f respawnPosition;

    private boolean active = false;
    
    public EventSystem(Player player, BoundingBox bathroomBounds, Vector3f respawnPosition) {
        events = new ArrayList<>();
        
        // Specific event field initialization
        this.player = player;
        this.bathroomBounds = bathroomBounds;
        this.respawnPosition = respawnPosition;
    }

    public void startListening() {
        active = true;
    }

    public void stopListening() {
        active = false;
    }
    
    public void loadEvents() {
        try {
            String filePath = "src/mygame/EventManagement/EventList.xml";
            
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
                        case "BathroomLightFlickering":
                            // Run flashback sequence
                            System.out.println("The lights are flickering");
                            break;
                        case "RespawnNormal":
                            // Respawn the player
                            System.out.println("Respawn in normal bathroom");
                            this.respawnPlayer(this.respawnPosition);
                            break;
                        case "RespawnAltered":
                            // Respawn the player
                            System.out.println("Respawn in altered bathroom");
                            break;
                        default:
                            System.out.println("No event.");
                    }
                });

                // Add the event to the list
                events.add(event);
                
            }
            
            System.out.println("[EventSystem.java] Event loading completed");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Error loading events: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
                System.out.println("Cannot load events");
                e.printStackTrace();
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
            this.triggerEvent(event.getName());
        }
    }
    
    public void triggerEvent(String eventName) {
        if (eventName.equals("RespawnNormal")) {
            this.respawnPlayer(this.respawnPosition);
        }
    }
    
    // For testing purposes
    public void displayEvents() {
        for (Event event:events) {
            System.out.println(event.getName());
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
    public void respawnPlayer(Vector3f spawnPosition) {
        if (this.bathroomBounds != null) {
//            // Get the center of the bathroom bounding box
//            Vector3f bathroomCenter = this.bathroomBounds.getCenter();
//            
//            // Offset slightly to ensure the player is above the ground
//            float offsetHeight = this.bathroomBounds.getYExtent() / 2 + player.getPlayerHeight() / 2;
//            
//            // A custom offset to place the player inside
//            float yOffset = -this.bathroomBounds.getYExtent() / 4;  // TODO: Adjust to place player inside
//            float xOffset = 0; // TODO: Adjust if necessary to avoid walls
//            float zOffset = 0; // TODO: Adjust if necessary to avoid walls
//            
//            // Create a position vector inside the bathroom bounds
//            Vector3f spawnPosition = new Vector3f(
//                    bathroomCenter.x + xOffset,
//                    bathroomCenter.y + offsetHeight + yOffset,
//                    bathroomCenter.z + zOffset
//            );
            
            // Set the player's position to the spawn location
            player.setPosition(spawnPosition);
            
            System.out.println("Respawned player in bathroom.");
        } else {
            System.out.println("Error: Bathroom bounds not found. Unable to teleport player.");
        }
        
    }
    
    
    
    
}