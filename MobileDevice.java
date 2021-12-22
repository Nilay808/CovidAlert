import java.io.File;
import java.util.Random;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MobileDevice extends Thread{
    //Declared Arraylist to store the contacts of a given mobile
    //Here I have use generic class to store the contact according to the project requirement
    //Where each element of arraylist in contact contains individual devicename, date of contact
    //And Duration
    private ArrayList<Contact> contacts=new ArrayList<Contact>();
    //Variables to store the data
    private String MobileID ;
    private String add;
    private String deviceName;
    private String combine;
    private String testHash;

    // Object of class random
    private Random rand = new Random();

    //Object of Government Class
    Government contactTracer;

    //Constructor of Class Mobile Device
    public MobileDevice(String configFile, Government contactTracer) {

        this.contactTracer = contactTracer;
        try {
            // Object of file class, used to access config file for a given Mobile device
            File fileObj = new File(configFile);

            //Object of scanner class to read the data in the file
            Scanner myReader = new Scanner(fileObj);

            //Reading the data in config file to get address and deviceName
            while (myReader.hasNextLine()) {

                //reading data and spliting by "="
                String[] data = myReader.nextLine().split("=");

                switch (data[0]) {
                    //switch case to store data seperately in different variables
                    case "address":
                        add = data[1];
                        break;
                    case "deviceName":
                        deviceName = data[1];
                        break;
                }
            }
            myReader.close();

            System.out.println("addition:---"+add);
            System.out.println("devicename:---"+deviceName);
            //Combining both the address as (add) and deviceName in to one variable
            combine = add + deviceName;
            System.out.println("combine address and devicename:----"+combine);

            //object of Message digest in order to convert the final string of address and devicename into Hash value
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] sha = md.digest(combine.getBytes(StandardCharsets.UTF_8));

            BigInteger number = new BigInteger(1, sha);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32)
            {
                hexString.insert(0, '0');
            }

            MobileID = hexString.toString();
            System.out.println("Mobileid:----"+MobileID);

        } catch (FileNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
    //method to record contacts which have been encountered by a given device
    public void recordContact(String individual, int date , int duration)
    {
        Contact temp = new Contact();
        //Using the set contact method of Contact class which
        // I have created to store according to the defined structure
        temp.setContact(individual,date,duration);

        //Adding the contacts to the defined arraylist to keep track of contacts
        contacts.add(temp);
//        System.out.println("contact in the data:"+MobileID+":"+temp.getContact());
    }


    //Method called by the main class to share positive test hash for a particular individual
    //This method will only called when the result of the given individual is positive
    public void positiveTest(String testHash) throws SQLException {

        //Storing the teshhash shared by the supposedly LAB(main class)
        //here tesh hash is not actually a hast but just a unique string
        this.testHash = testHash;

        //Using the government class object "contactTracer to link the shared teshHash with the particular
        // Mobile ID Hash
        contactTracer.linkDeviceID(testHash,MobileID);

        //update deviceTestresult
    }

    //This method is used to pass information about the contacts with other mobiledevices
    //To the method named mobileContact to store data in database, where it is also check
    // if the information sender has been in contact with a covid positive person or not
    //Return true if contact with covid positive person otherwise false
    public boolean synchronizeData() throws SQLException, ClassNotFoundException {
        //boolean variable
        boolean testResult = false;
        //for loop to send contacts from Arraylist one by one to c
        for(Contact c : contacts)
        {
            //Here all the contact data along with the sender mobileID hash is being sent to mobile contact class using
            //contact Tracer object of Government
            boolean temp = contactTracer.mobileContact(MobileID,c.getContact());
            if (temp)
                //if true is returned
                testResult = true;
                System.out.println("You have been in contact with a covid-19 positive person");
        }
        System.out.println(testResult);

        return testResult;
    }

    //Method to get the MobileID hash of the given mobile device
    String getMobileid()
    {
        return MobileID;
    }

    public void run()
    {
        while (true)
        {
            int delay = rand.nextInt(1*60*100);
            try {
                Thread.sleep(delay);
                System.out.println("Mobile : "+MobileID+" is Synchronized");
                boolean res = synchronizeData();
                if(res)
                {
                    System.out.println("Mobile : "+MobileID+" has been in contact with covid-19 positive person!");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }


}
