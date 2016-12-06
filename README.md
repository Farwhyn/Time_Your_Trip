#Time Your Trip Application
This application is still in development. It will be improved and added more features.


##What is Time Your Trip Application
This Android mobile application notifies bus passengers when they are arriving at their destination stop. It allows the user to spend the bus ride doing other activities, such as studying and sleeping, without having to constantly check the current stop and without fear of missing their destination stop. 
The app operates such that when the user gets on a bus, he or she will enter the starting stop he/she at, then the app will allow the user to choose the bus number and the destination stop. After that, it accesses real-time TransLink information to retrieve the estimated time it will take for the user to reach the chosen stop. Then, the app will set an alarm based on the retrieved time. 


##How to find our source code:
Our source code is on a  public github repository, the following is link to Time Your Trip Application:
https://github.com/Farwhyn/cpen321project 


##How to check it out:
This procedure assumes you have already created a repository on GitHub, or have an existing repository owned by someone else you'd like to contribute to.

1. On GitHub, navigate to the main page of the repository.  
<img width="221" alt="capture1" src="https://cloud.githubusercontent.com/assets/14351017/20193981/55658162-a745-11e6-8a77-985bfa181e80.PNG">
4. Under your repository name, click Clone or download.  
<img width="288" alt="captureafter" src="https://cloud.githubusercontent.com/assets/14351017/20193903/f07e48f6-a744-11e6-8cb4-513606f855a0.PNG">
5. In the Clone with HTTPs section, click to copy the clone URL for the repository.
6. Open Git Bash.
7. Change the current working directory to the location where you want the cloned directory to be made.
8. Type git clone, and then paste the URL you copied in Step 2.
9. git clone https://github.com/YOUR-USERNAME/YOUR-REPOSITORY
10. Press Enter. Your local clone will be created.
11. `git clone https://github.com/YOUR-USERNAME/YOUR-REPOSITORYCloning into "Spoon-Knife"...`  
`remote: Counting objects: 10, done.`  
`remote: Compressing objects: 100% (8/8), done.`  
`remove: Total 10 (delta 1), reused 10 (delta 1)`  
`Unpacking objects: 100% (10/10), done.`  
12. The code is under “PlanMyTrip” folder.

##How to build the project:

There are two options for building the project:

* Through an emulator:
This procedure assumes that you have already installed Android Studio.
To start an Android emulator such as the default emulator installed in RAD Studio:
   1. Tools->android ->Start the Android SDK Manager (select Start. ...
   2. In the Android SDK Manager, click the Tools menu and select Manage AVDs.
   3. In the Android Virtual Device Manager, select the emulator and click Start.
The app will be uploaded automatically and it can be tested starting by providing whatever expected.


* Through an actual android phone

You can download our app from [here](https://play.google.com/apps/testing/com.planmytrip.johan.planmytrip), or use Google Play and search "Time My Trip"

##How to run tests:
After open the project, switch to the Project first.  
<img width="136" alt="ctest" src="https://cloud.githubusercontent.com/assets/14351017/20193584/7bd44696-a743-11e6-9e4b-7abfa46acb5f.PNG">  

There are two folders that contain different tests for our application.

####Application Tests
1. In the project source code, go to PlanMyTrip\app\src;
2. Under the “andoridTest” folder, click on “java”, then the “com.example ….” folder, where the Application tests are;
3. To run the application tests, click “Run” or right click on the file and select “run ‘ApplicationTest’”. Then Android Studio should open the emulator;
4. Choose a virtual device. If don’t have any 
*  Click on the “create new virtual device” button;
*  Under the “Category”, select “Phone”; 
*  Choose a device then click on “Next”;
*  Click on “Next” on the following windows then, at last, click on “Finish”;
*  Wait for it to be created then it should be seen on the window.
5. Click “OK”
6. The result will show at the bottom “Run:” console.


####Unit Tests
1.In the project source code, go to PlanMyTrip\app\src;  
2.Under the “test” folder, click on the “java” then “com.example...”;  
3.To run the unit tests, right click on the file and select “run ‘NameofTheTests’”;  
4.The result should also shown on the bottom “Run:” console.  
You can always switch back to test the application by clicking on the dropdown list and choose app as shown in the following picture:  
<img width="228" alt="capturebs" src="https://cloud.githubusercontent.com/assets/14351017/20193743/43ebab7e-a744-11e6-9eda-f73a2843e996.PNG">

##Structure of the source code directory:
Open the project on Android Studio, then:  
On the top left side, click on the app folder  
Select the java subfolder  
Select com.example.johan.planmytrip where all the activities are:  

<img width="272" alt="captureg" src="https://cloud.githubusercontent.com/assets/14351017/20193841/aafd9692-a744-11e6-99b9-a6addbb9f61a.PNG">

**alarmTimer**: this activity is responsible for turning on/off the alarm  
**AllRoutes**:this is used when the offline feature is selected so that it returns a list view of all the buses with an autocomplete option   
**Bus**: this activity is providing the user with infos regarding the bus(route, bus number,estimated leave time and the destination)  
**ConnectDatabase**: generate the list of the upcoming bus stops provided to the user  
**DatabaseAccess**: using the created database to send queries so that a listView can be generated  
**DatabaseOpenHelper**: creating the database  
**Favourite**: this is responsible for the favourite list where the user can save their frequent bus stops.  
**GPSchecker**: this activity is for checking whether the GPS mobile is enabled or not     
**GPSHandler**: is for checking how far the user is from their destination stop     
**MainActivity**: this activity is responsible for the launcher page, where the user is expected to enter the bus stop number he/she is at  
**NextBusesAdapter**: this activity is to generate the list view of the buses provided to the user after he entered the starting bus stop  
**NextStopsAdapter**: this activity is for returning the upcoming bus stops  
**OfflineAlarm**:this activity is for setting the alarm when the offline mode is selected.  
**OfflineService**:this is for generating the bus stops for the offline mode.   
**OfflineStops**:this is for generating the bus stops for the offline mode.  
**Stop**: this activity will provide the location of the selected stop  
**SwipeDetector**: this activity is for deleting a bus stop from the favourite list by swipping it left.  
**TimerService**: this activity contains the algorithm of the time, check the dest, and update the time in the online mode
**TranslinkHandler**: this activity is where we are extracting the infos from Translink API and parsing them to a JSON object or JSON array  
**TranslinkUI**: this activity is for providing the user with the buses that they can take from their starting location


##Design patterns used: 
* Model-View-Controller pattern   
As for the whole system, UI reflects the change of the states of the APP. Every time the state changes, it updates.
*Singleton pattern 
Is used to design the database helper class.
