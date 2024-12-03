# Perpetuality
Genre: Psychological Horror, Puzzle

## Game Summary
In Perpetuality, players navigate a surreal and unsettling labyrinth within a mysterious university building, representing the fractured psyche of a computer science student. Each level or phase is a different dream or psychological state, mirroring the student's academic pressures, fears, and emotional turmoil. To escape this labyrinth, players must interact with various NPCs, including faculty members, fellow students, and the school mascot. The journey is both a literal and metaphorical exploration of internal struggles, focusing on maintaining sanity amidst an ever-shifting, nightmarish environment.

## Narrative Overview
You play as an unnamed computer science student trapped within the labyrinthine corridors of a university that distorts and manifests their mental state. Each dream phase evokes different aspects of academic life and psychological stress, creating a unique and eerie setting with every progression. The goal is to escape by solving puzzles, interacting with characters, and managing sanity—all while avoiding the malevolent presence of the school mascot, who embodies the student's deepest fears.

## Story Elements
- Protagonist: An unnamed computer science student struggling with the stress and pressures of university life. Their experiences within the labyrinth reflect their internal conflicts and emotional struggles.
- Antagonist: The university's school mascot represents the student's fears and insecurities. It acts as both a guide and a threat, manipulating the labyrinth and the student's perceptions.
- Setting: A university campus that transforms into a surreal and disorienting labyrinth. Familiar locations become distorted, representing the protagonist's mental state.
- Objective: Escape the labyrinth by navigating through various dream phases, interacting with NPCs, solving puzzles, and managing the protagonist's sanity.

## Inspiration
Perpetuality draws inspiration from psychological horror games that emphasize atmosphere, sanity management, and surreal environments:
- Amnesia: The Dark Descent: Influenced our sanity mechanics and atmospheric tension, focusing on maintaining sanity through environmental interactions and evasion rather than combat.
- Shinkansen 0, Exit 8, Platform 8: Inspired our sense of disorientation and anomaly detection mechanics, utilizing repetitive and unsettling environments with the concept of resets upon failure.
- Rusty Lake: Roots: Contributed to our game's eerie atmosphere and dark narrative style, incorporating disturbing imagery and surreal puzzles.
<img width="622" alt="Screenshot 2024-11-06 at 8 24 52 AM" src="https://github.com/user-attachments/assets/7f9938a8-1917-4754-be3d-0e55741abe4b">
<img width="622" alt="Screenshot 2024-11-06 at 8 25 08 AM" src="https://github.com/user-attachments/assets/d298b2ce-0cd5-495d-9e8b-2c161ab576c1">


## Gameplay Mechanics
### Sanity Management
- Increasing Sanity: Interact with objects that trigger calming memories, such as notes, photos, or music. Successfully figuring out the puzzle also increases sanity.
- Decreasing Sanity: First, student is not in a safe room (the current saferoom is the bathroom). Second, if student interacts with objects in an incorrect way (or with an incorrect object). Third, if student encounters the monster.

### Progression
- Navigate through multiple dream phases, each representing different psychological states. Different dream phases likely correspond to different scenes.
- Each phase may have unique mechanics affecting the player's sanity level.

### Special Events and Triggers
- Certain events or objects may trigger the appearance of a monster, adding urgency to maintain sanity.
- Lower sanity increases the frequency of these threats (potentially).

### User Interface and Controls
- Sanity Bar: Displayed on-screen, updating in real-time based on player actions and in-game events.
- Interactions: Use standard controls to interact with the environment, items, and NPCs.
- Exploration: Objectives are discovered through exploration and interaction rather than explicit instructions.

**As development progresses, additional mechanics and story elements will be introduced to enhance gameplay depth and narrative cohesion.**

## Development First Deliverable
 ### User Input
    - Maps out the controls of the game
        - WASD: move
        - Left click: interact
        - Right click: inspect
 ### Game Manager
    - Renders the scenes we would want to display in our game
        - Bathroom (Reset, Rest point) 
            - A possible save point for the user to stay in
            - A custom asset that has a light source in the scene
    - Loads in cubes that would soon be interactable objects which would increase or decrease sanity
 ### Objects
    - Interactable cubes
        - Red and Green: collectable
            - They will disappear once you collect them
        - Yellow and Pink:
            - Change color or shape
 <img width="620" alt="Screenshot 2024-11-06 at 8 25 22 AM" src="https://github.com/user-attachments/assets/e6c90fcf-01bf-4291-b6d6-57a6a0820b05">

 ### Scene Loader
    - Loads a scene in the game
 ### Game State
    - Keep track of the sanity bar
    - Will keep track if the game is over or not
        - Game will be over when sanity reaches 0
            - So far it would only print game over in the console, need to work on making a game over state
        - 1st iteration: sanity bar implemented, but UI is not implemented yet
 ### Game Control
    - Press using cursor to examine interactable object, selected object will rotate.
    - Clicks on object to interact (object turns gray once interacted), this action 
     will affect sanity value based on the type of object.
    - Green Box as an example of collectable object: disappears upon click 
     (collection)
### Inventory System
    - Inventory that is used to maintain sanity by consuming items
 ### Ways to increase sanity
    - Interacting with objects
        - 1st iteration: interacting with good boxes
    - Standing on a light source
        - 1st iteration: light source implemented, but the sanity bar increasing not implemented
    - Consuming item in the inventory system
 ### Ways to decrease sanity
    - Interacting with objects
        - 1st iteration: interacting with bad boxes
    - Standing in darkness
        - 1st iteration: user will lose sanity by default for now

## Development Second Deliverable
 ### Sanity UI Updated (guidNode)
    - Player can now visualize the live changes in the health bar status
 ### Dialog Box UI (guiNode)
    - Informs the user of what event has occurred and provides guidance on navigation (general instructions + hints on finding items)
        - Triggered by an event system
        - Triggered by consuming an item in the inventory system (working in progress)
![Example of our dialog](./Images/DialogExample.png)
 ### Safe Areas
    - Player can now increase their sanity by going into certain safe areas like the Bathroom
    - Bathroom and other safe areas have trigger box that cause sanity to increase
![Bathroom_1](https://github.com/user-attachments/assets/154d27c3-2320-488b-b8a4-38af5f23e8fe)
![Bathroom_2](https://github.com/user-attachments/assets/d4b94bd3-08b3-47d0-acff-abcf8692a70c)
![Bathroom_Flashlight](https://github.com/user-attachments/assets/25bec851-0c5a-4eec-b431-b754c41e67e8)
 ### Event System
    - An observer class that handles event that occurs in-game
    - For now, informs the player of their respawn status
    - If an event triggers, a functionality will fire off
        - If the event respawn occurs, the player will respawn in the bathroom
    - This event system will make it easier to add more functionality to our game
        - Easily adding new custom events by adding more functionality in the function trigger events
        - Can easily be used with the Inventory System (working in progress)
 ### Puzzles
    - Not yet certain about what type of puzzles to implement.
        - Currently focused on mechanics that help to make more puzzles (moving, dialog box, flashlight, sanity...)
    - The current core mechanics:
        - Find batteries in the scene & recharge the flashlight
        - Collect certain "key" items to push the plot forward using DialogBox
        - Sanity Management
    
 ### Flashlight (SpotLight + DirectionalLight)
    - The user has a spotlight attached to the camera as the primary light source 
        - Limit the visible range to control the difficulty of finding items
        - The scene should be dark most of the time, flashlight should be the only light source.
    - We will update more mechanics so that the flashlight distance (view range) fluctuates depending on the number of batteries collected (the main collectible). 
        - One of the player's main goals is to continue searching for batteries to maintain flashlight status.
    - Plan: Lower battery status will lead to a quicker decrease in health (sanity) + trigger monster chasing once belows a certain threshold.
 <img width="626" alt="Screenshot 2024-11-06 at 8 25 32 AM" src="https://github.com/user-attachments/assets/b87519fc-0343-4c10-acf3-79a129796d79">

 ### Animation (Skinning Control + Texture) 
    - There is one animated "monster" in our game, it will chase the user once the battery status is low. 
    - The animated monster is currently using the Oto model, which might change for later submission.
 ![Robot_Idle](https://github.com/user-attachments/assets/f005c22c-3ec2-444f-96ba-8a1c9c49359b)
 ![Robot_Walking_2](https://github.com/user-attachments/assets/f93ce89b-2a7f-4081-a013-3702d6716de7)
 ![Robot_Walking_1](https://github.com/user-attachments/assets/df93e564-b1a2-4269-b56e-7b43f6bbf5e5)

    
 ### More Scene Models Created (AssetManager + Texture)
    - Created two new classroom scenes through Blender.
    - Connected the bathroom scene to the two classroom scenes. 
    - !!IMPORTANT!! Our models are created in Blender with textures already attached, so no need to explicitly use loadTexture method.
![Classroom_1_Flashlight](https://github.com/user-attachments/assets/7f8b353b-aa99-420c-8a5b-17c982157c35)
![Classroom_2_Flashlight](https://github.com/user-attachments/assets/d32bc0d4-2a60-4020-a5e7-35fd2564bd48)


 ### Moving Mechanism (Physics)
    - Limit the player's movement (WASD) to the ground level (simulation of gravity) 
    
 ### Inventory System (guiNode)
    - Now has two types of items
        - Key items
            - An item that can possibly be used for puzzles later in our development
            - If a player consumes this item, an event will trigger
        - Consumables 
            - Mostly used to increase or decrease sanity
    - Displays what type of items you have now
        - Updates InventoryUI when an item is gain or used.
![InventoryUI](./Images/InventoryUI.png)  

## Updates based on Feedback from Submission 3
    - Finalize Game Play Mechanics: to pass the game, player should search for “pages” in the scene under the guidance of dialog boxes while managing the health state and the flashlight status. 
    - Sanity Bar UI now reflects diverse game situations. 
    - Robot: added into cut scene, plan to add animation for final presentation. 

## Third Deliverable
### GameState
   - Contains inventory system
   - Contains three important fields
   - Health: reflect player’s health status with health bar UI. Health reaches 0 triggers Game Over End State.
   - Required Num Pages: decides how many “pages” (keys) the user needs to collect to win the game.
   - Game over state: indicator for losing or winning.

![image](https://github.com/user-attachments/assets/d8301b76-1874-480b-8646-b71af9455eea)



### Simplified Inventory System
 - Consumables: press button “E” to consume and increase health; consumable count decreases.
 - Pages: 
   - Collect three pages to pass the game.
   - Each page should trigger a dialog box telling a short story.
   - If player has the right number of pages, game state announces player has won.
 - Batteries: press the button “R” to charge the flashlight.

![image](https://github.com/user-attachments/assets/240feaa1-aa5f-4c08-a698-c62798024c74)



### FlashLight Mechanism: 
 - Player needs to collect batteries to charge the flashlight, and the flashlight affects health status.
 - Player can toggle the flashlight with “Right Click” only when there is battery
 - When the flashlight is on: health doesn’t decrease; charge status continues to decrease.
 - “Left click” on a battery to charge the flashlight
 - Flashlight turns off when charge is 0.

![image](https://github.com/user-attachments/assets/994ecf98-9c23-41a1-bd61-d4ecc73d276c)


### Transitions Mechanism
- Allows user to transition between different scenes 
- Added transition mechanism between bathroom, hallway, and classroom scenes: player can press “Q” to exit the bathroom if he/she is close to the exit point.
- Player can only transition if close to an exit point.
- Lots of re-factoring and cleaning were done to manage loading scenes and switching between three different scenes. 

### Added/Updated scenes
- Hallway scene and model are added.
- A second Japanese classroom scene and model are added along with associated textures; also added lights to the classroom.
- Added fade-in screen before the first scene.
- Created separate app states for each scene.

![image](https://github.com/user-attachments/assets/0208f7d7-72bc-4b16-a957-a9e65cb7c180)



### Added interactive components to different scenes
- Bathroom interactive components
  - Initial Dialog
  - Pick Up FlashLight action
- Classroom interactive components
  - Added a list of dialog-box-triggering events to tell the story. The dialog box events are triggered based on elapsed time. 
  - Featured camera rotation abilities to enhance game immersion.
  - Incorporated and set-up various audio effects (ambient, breathing, tension, yelling, pickup sound) for story-telling.
  - Jumpscare mechanisms were implemented; audio was added to enhance the effect; zoom-in effect also included.

![image](https://github.com/user-attachments/assets/40cfc6e0-28e8-462a-9569-e3e4da6bbcd8)



### DialogBox and UI Updates:
- Updated DialogBox Background to make it more interesting and “juicy”.
- Functions: show, hide, and clear dialog are updated.
- Shaking Dialogue is added for better visual effects. 

### Robot Interactions
- Added a beginning cutscene establishing the robot as an antagonist, establishing the story of the game.
- Plan to add in robot walking in hallway (while being animated).

![image](https://github.com/user-attachments/assets/68f126ad-c955-4752-b871-34e431bf0b1a)



### Sound: jumpscare audio + other audio effects
Ambient: 
Positional: jumpscare audio 

### SceneManager
Loads separate scenes to optimize the game


## Six Effects
- Skybox: Added the jmonkey provided lagoon skybox
- Terrain: Terrain was added to the hallway scene so user can see the outside terrain
- Shadow: Already implemented last time for flashlight
- Water: Not necessary for our game, but added a puddle for now
- Particles: smokes were added to certain parts of the scenes and on certain objects
- Fog: added to classrooms and hallway to enhance gloominess
- Juice:
  - Shaking Dialogue
  - Cutting Scenes
  - Jumpscare Zoom In Effect + camera shake
  ![image](https://github.com/user-attachments/assets/afd89696-4748-4aec-8c5b-055988124c63)
  ![image](https://github.com/user-attachments/assets/ff529cdf-b7ab-463d-b6b1-f81f1cb094a3)
  ![image](https://github.com/user-attachments/assets/76be5fcb-c3f3-4613-b17f-9e3ff83f6a70)
  ![image](https://github.com/user-attachments/assets/02743972-e70c-4b6b-ab37-44fd0ab72376)


## Member Contribution
- Alaisha Barber:
- Chenjia Zhang:
  - DialogBox Mechanism for events introduction and triggering.
  - Flashlight Mechanism and interaction with the health bar.
  - Animation for robot model.
  - Aspects of special effects: skybox, particles, shadow, and fog. 
  - Readme writing.
  - Intermediate rough-sketch functions for demo purposes (cube creation and selection)
- Michael Kim:
  - Implemented the GameState, InventorySystem, and Flashlight
- Collaborative:
  - Overall brainstorming game logic and mechanics.
  - Readme

We scheduled regular Zoom meetings to discuss implementation progress and the assignment of different tasks. We mainly worked on our own and then reconvened when we were done with our individual tasks. (Note: Due to some issues with model incompatibility, I (Chenjia) had to implement features and functionalities in separate files first before incorporating them into the actual game. Alaisha and Michael helped significantly with these laborious refactoring processes, and I am very very grateful for their help ).





