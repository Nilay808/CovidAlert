# CovidAlert
# Simple Covid Alert Console Application in Java
# Design of Solution:
* I have Created in total Four classes named MobileDevice.java, Government.java, Contact.java and main.java. Methods in given classes:
# Mobile Device class:
## MobileDevice(String configFile, Government contactTracer):
* This method will do the work of getting the credentials from the config file for particular mobile device object and converting the combined string of address and devicename to hash value.
* recordContact(String individual, int date, int duration):
* This method is used to store the contacts that have been with the particular mobiledevice, here we have internally used set contact method of Contact class to store data in the manner that we can keep three important information of contact which are Individual mobile id hash of the contact mobile hash, date of contact and date of duration
## String getMobileid():
* This method return mobile of the given of object of mobileDevice class
## positiveTest(String testHash):
* This method supposedly will be called by the lab to inform particular individual that he/she has been positive and by providing them there testHash, where test hash is just a unique string
* boolean synchronizeData():
* This method will be used to send data to the government class method named mobile contact, the data will be consist of all the information of mobile devices that a particular individual have come in contact with regard to particular date and duration. And then further the mobile contact will check if the sent data by the individual consist of any covid positive person or not, if they have come in contact then mobile contact method will return true otherwise false. This method will return true only for the first encounter after that it will return false.


# Government Class:
## Government(String configFile):
* This is a constructor that will do the work of getting credentials from a text file to establish connection with the database
## void connectDatabase():
* This method will be called in constructor where it will receive the credentials for establishing the connection with database where, particularly this method will establish the connection and create tables if already not made in the database. The table names are contactinfo table to store the contact information, testResult table to store testHash, sent by the lab, deviceTestresult to link test hash with Mobileidhash , and notified table which stores information regarding the individuals who have been notified after they have come in contact with covid positive person
* boolean mobileContact(String initiator, String contactInfo):
* This method will receive the information regarding the individual contact information with other individuals where their mobile hash id, date and duration are being sent from the synchronize method of mobilecontact method.

* Where this method will first check if their contact has been added in the database table contactinfo or not, if not then it will add this contact with in the contact info table and after this it will check if the individual who has sent the data has been in contact with any covid-19 positive person or not, if they have come in contact then mobile contact method will return true otherwise false to synchronize method.
## recordTestResult(String testHash, int date, boolean result):
* This method will be called to store the testhash of a particular individual in the database table named testResult which will consist of TestHash, data and result. The result will be true if the covid result is positive and false if the result is negative.
## public void linkDeviceID(String tHash, String DeviceHash):
* This method will be called by the positiveTest method in mobileDevice class, in order to link unique test hash with the mobiledevice hash id(Device Hash)and store it in the database table named deviceTestresult which will help to send the notification to the other individuals who have come in contact with this person.
## int findGatherings(int date, int minSize, int minTime, float density):
* This method is used to find gathering of a group of people on a particular date for a given amount time and report if it satisfy the density requirement . This method will work in such a way first it will check for given amount of pair of individuals who have come in contact with each other after that it will form a set S which will satisfy the minimum number individuals required (minSize) and then it will form another set C which will consist number of pairs of individuals who satisfy the minimum time of contact. And from the first set we have find total number of distinct individuals and store in a variable n and using that we will find m, which m = (n(n-2))/2, if C/M >= density then that gathering will be reported. And this same gathering will not be reported again.
## Note:- The pair is taken in such a manner that all the member of set has come in contact which each for example: Suppose on a particular day we have set like :
* (A,B), (A,C),(B,C),(C,D),(D,E)
* So here we take for pair A,B and so for that S ={(A,B),(A,C),(B,C)}
* n=3(because A,B,C total 3 individuals)
* c={(A,B),(A,C),(B,C)} // Suppose all of them satisfy time and size constraint
# Contact Class:
## void setContact(String individual, int date, int duration):
* This method will store the contact information in a predefined manner such that all the three different values of the contact can be stored together
## String getContact():
* This method is use to return contact which have been stored.
## Functional flow of the system(Suggested):
* 1)First the object of Government class is made and passed to mobile device class that it can access the methods of mobile device class.
* 2)Mobile Device class object will be created in the main class, its address and device name will be taken from a config file and there is one config file per mobile device object. So can create as many as you like but a different config file is required.
* 3)Now the contacts among the mobile devices will be created (this can also be done afterwards).

* 4)Now lab will send test result to the government which will consist testhash, date of test and result (true or false) both the test will be stored. Here lab will call recordTestresult method of government using the government class object from the main method.
* 5)Lab will report testHash to the individual who have been found positive, but if test result is negative lab report will not be sent to the individuals. To perform this task here lab will call positivetest method of mobileDevice class from main method using the mobileDevice class object of that particular individual.
* 6)Now in positivetest method , first it will store the test hash and then call linkDeviceID method in government to link unique test Hash with the mobile device hash(Device Hash) and store it in a table name deviceTestresult.(Which will be used in future to send notification to the individuals who have come in contact with this one).
* 7)Now when a synchronize method will be called which will send all the information of contacts from mobileDevice class to mobileContact method of Government class. Where it will new add the contact to the database (it will update the same contacts duration of meeting if being called again in contactinfo table of database) and also check devicetestresult table in database if the initiator individual has come in contact with the covid positive person.
* 8)Now you can call for gathering as most of the data has been added to the database we find if there is gathering or not on particular date. This method will be called using the object of the government class.
## Note: In my project the database table are automatically created In the database so no need to create table externally, but then also I have provided the sql queries to create tables
