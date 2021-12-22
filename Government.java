import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.sql.*;
import java.util.ArrayList;

public class Government {
    //Variables to establish connection
    //with database and carry out queries
    private String address;
    private String database;
    private String userName;
    private String password;
    private Connection con;
    private PreparedStatement stmt = null;
    private String sql;

    //Constructor of Government Class
    public Government(String configFile)
    {
        try {
            //Reading the config file to get credential to create connect with database
            File fileObj = new File(configFile);
            Scanner myReader = new Scanner(fileObj);
            while (myReader.hasNextLine()) {

                //Spliting data with "="
                String[] data = myReader.nextLine().split("=");
                switch(data[0])
                {
                    //Switch case to store all the read data to particular required variables
                    case "address":address = data[1];break;
                    case "database":database = data[1];break;
                    case "user":userName = data[1];break;
                    case "password":password = data[1];break;
                }

                System.out.println(data[1]);
            }
            myReader.close();
            connectDatabase();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
    //Method to create connect with the database
    private void connectDatabase()
    {
        Connection connection = null;
        Statement ps = null;
        try
        {
            //Driver for the Database
            Class.forName("com.mysql.cj.jdbc.Driver");

            //Passing the Address and Credentials to connect with database
            con=DriverManager.getConnection("jdbc:mysql://"+address+"/"+database,userName,password);

            System.out.println("Connection Success !");

            //Sql Query to Create ContactInfo Table which consist of columns like Mobile ID, Contact Mobile ID ,
            // date, and Duration and here primary key is id (Auto Increment)
            sql = "CREATE TABLE IF NOT EXISTS contactInfo (id INTEGER not NULL AUTO_INCREMENT, mobileid text, " +
                    "contactmobileid text, date integer, duration integer , PRIMARY KEY (id))" ;

            //Using prepare Statment for generating the sql query
            stmt = con.prepareStatement(sql);

            //Execute the sql Prepare Statment
            stmt.executeUpdate();

            //Sql Query to Create testresult Table which is used to store all the test result of individuals sent
            // to government  by Lab here the table consist of columns like testhash, date(date of test),
            // resutl(True/false), here primary key is id (Auto Increment)
            sql = "CREATE TABLE IF NOT EXISTS testResult (id INTEGER not NULL AUTO_INCREMENT, testhash text unique,  " +
                    "date integer,result text , PRIMARY KEY (id))" ;
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();

            //Sql Query to Create deviceTestresult Table which is used to link tesh hash and mobile id hash
            //Here the primary key is id and foriegn key is testResultID from the testResult table to match testhash
            //The table consist of columns like mobileid text, testResultid
            sql = "CREATE TABLE IF NOT EXISTS deviceTestresult (id INTEGER not NULL AUTO_INCREMENT, mobileid text," +
                    "testResultid integer , PRIMARY KEY (id), CONSTRAINT testResultid FOREIGN KEY (testResultid) " +
                    "REFERENCES testResult(id))" ;
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();

            //Sql Query to Create Table named notified which is used to store all mobileid hash of the devices which have been
            //Notified by the government that they have been in contact with a covid positive person
            //The table consist of columns like mobileid(Hash), date(date of test), notified
            //And Here primary key is id (Auto Increment)
            sql = "CREATE TABLE IF NOT EXISTS notified (id INTEGER not NULL AUTO_INCREMENT, mobileid text ," +
                    " date integer ,notified text , PRIMARY KEY (id))" ;
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();

        }
        catch(Exception e)
        {
            System.out.println("Connection Failed !");
            System.out.println(e);
        }

    }

    //MobielContact method, This method is called by the synchronizedata method from MobileDevice Class to share
    //Contact information of the given device with the government to store in database, along with that this method
    //return true if the initiator has been in contact with a covid positive person and false otherwise
    public boolean mobileContact(String initiator, String contactInfo) throws SQLException {
        try {

            String[] info = contactInfo.split(":");
            //sql query to to
            sql = "SELECT id,duration FROM contactInfo where mobileid = ? AND contactmobileid=? AND date=?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1,initiator);
            stmt.setString(2,info[0]);
            stmt.setInt(3,Integer.valueOf(info[1]));
            ResultSet rs=stmt.executeQuery();

            if(!rs.next())
            {
                sql = "SELECT id,duration FROM contactInfo where mobileid = ? AND contactmobileid=? AND date=?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1,info[0]);
                stmt.setString(2,initiator);
                stmt.setInt(3,Integer.valueOf(info[1]));
                ResultSet rs1=stmt.executeQuery();

                if(!rs1.next())
                {
                    sql = "INSERT INTO contactInfo (mobileid,contactmobileid,date,duration) VALUES(?,?,?,?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1,initiator);
                    stmt.setString(2,info[0]);
                    stmt.setInt(3,Integer.valueOf(info[1]));
                    stmt.setInt(4,Integer.valueOf(info[2]));
                }
                else
                {
                    int id = rs1.getInt("id");
                    int duration = rs1.getInt("duration");
                    System.out.println("ID = "+id);
                    sql = "UPDATE  contactInfo SET mobileid=?,contactmobileid=?,date=?,duration=? WHERE id=?";
                    stmt=con.prepareStatement(sql);
                    stmt.setString(1,info[0]);
                    stmt.setString(2,initiator);
                    stmt.setInt(3,Integer.valueOf(info[1]));
                    stmt.setInt(4,Integer.valueOf(info[2])+duration);
                    stmt.setInt(5,id);
                }
                stmt.executeUpdate();

            }
            else
            {
                int id = rs.getInt("id");
                int duration = rs.getInt("duration");
                sql = "UPDATE  contactInfo SET mobileid=?,contactmobileid=?,date=?,duration=? WHERE id=?";
                stmt=con.prepareStatement(sql);
                stmt.setString(1,initiator);
                stmt.setString(2,info[0]);
                stmt.setInt(3,Integer.valueOf(info[1]));
                stmt.setInt(4,Integer.valueOf(info[2])+duration);
                stmt.setInt(5,id);
                stmt.executeUpdate();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        //setting notification to false
        boolean notification = false;
        sql = "SELECT * FROM contactInfo where mobileid = ? or contactmobileid = ?";
        stmt = con.prepareStatement(sql);
        stmt.setString(1,initiator);
        stmt.setString(2,initiator);
        ResultSet rs=stmt.executeQuery();
        while(rs.next())
        {
            String mid = rs.getString("mobileid");
            String cmid = rs.getString("contactmobileid");
            int date = rs.getInt("date");
            int duration = rs.getInt("duration");
            String contactDevice;
            if(mid.equals(initiator))
            {
                contactDevice = cmid;
            }
            else
            {
                contactDevice = mid;
            }
            //Sql query to find if the initiator has been in contact with a covid-19 positive person in last 14 days of
            //contacts
            sql = "SELECT deviceTestresult.mobileid,testresult.testhash,testresult.date,testresult.result " +
                    "FROM deviceTestresult "+
                    "INNER JOIN testresult ON deviceTestresult.testResultid = testresult.id Where mobileid = ? AND " +
                    "(testresult.date-?) <= 14 "+
                    "ORDER BY testresult.date DESC LIMIT 1";

            stmt = con.prepareStatement(sql);
            stmt.setString(1,contactDevice);
            stmt.setInt(2,date);
            ResultSet rs1=stmt.executeQuery();
            if (rs1.next())
            {
                String testhash = rs1.getString("testhash");
                int testDate = rs1.getInt("date");
                sql = "SELECT * FROM notified where mobileid = ? AND (?-date) < 14 ORDER BY date DESC LIMIT 1";
                stmt = con.prepareStatement(sql);
                stmt.setString(1,initiator);
                stmt.setInt(2,testDate);
                ResultSet rs2=stmt.executeQuery();
                if( ! rs2.next())
                {
                    System.out.println("mobileid = "+initiator+": contactmobileid = "+
                            contactDevice+": testhash = "+testhash+": Date = "+testDate);
                    sql = "INSERT INTO notified (mobileid,date,notified) VALUES(?,?,?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1,initiator);
                    stmt.setInt(2,testDate);
                    stmt.setString(3,"true");

                    stmt.executeUpdate();
                    notification =  true;


                }

            }


        }

        return notification;
    }
    //This method is used to store the testhash, date of test and result of a test sent by lab from main Class
    public void recordTestResult(String testHash, int date, boolean result) throws ClassNotFoundException, SQLException
    {
        try
        {
            //Sql query to store data sent by lab to government in testResult table
            sql = "INSERT INTO testResult (testhash,date,result) VALUES(?,?,?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1,testHash);
            stmt.setInt(2,date);
            stmt.setString(3,Boolean.toString(result));
            stmt.executeUpdate();

        }
        catch (Exception e)
        {

        }


    }
    //This method is used to link teshHash with mobiledeviceHash
    public void linkDeviceID(String tHash, String DeviceHash) throws SQLException {
        //Sql query
        sql = "SELECT id,result FROM testResult where testhash = ?";
        stmt = con.prepareStatement(sql);
        stmt.setString(1,tHash);
        ResultSet rs=stmt.executeQuery();

        if(!rs.next())
        {
            System.out.println("No report found !");
        }
        else
        {
            int resultid = rs.getInt("id");
            String res = rs.getString("result");
            //
            sql = "SELECT id FROM deviceTestresult where mobileid = ? AND testResultid=?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1,DeviceHash);
            stmt.setInt(2,resultid);
            ResultSet rs1 = stmt.executeQuery();
            if(!rs1.next())
            {
                System.out.println("ID: " + resultid);
                if(res.equals("true") )
                {
                    sql = "INSERT INTO deviceTestresult (mobileid,testResultid) VALUES(?,?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1,DeviceHash);
                    stmt.setInt(2,resultid);
                    stmt.executeUpdate();
                }
                else if(res.equals("false"))
                {
                    System.out.println("You are tested negative !");
                }
            }
        }

        rs.close();

    }

    //Find Gathering method to return number of gathering on a particular date for a given amount of time among given
    //number of people
    public int findGatherings(int date, int minSize, int minTime, float density) throws SQLException {

        //Sql query to select the contactinfo table and get information on pairs which were in contact for a given date
        sql = "SELECT * FROM contactInfo where date=? ";
        stmt = con.prepareStatement(sql);
        stmt.setInt(1,date);

        ResultSet rs=stmt.executeQuery();

        //Arraylist to store the group
        ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String> >();

        //Arraylist which will consist the already found gathering
        ArrayList<Integer> removedGroups = new ArrayList<Integer> ();
        int totalGroup = 0;


        while (rs.next())
        {
            //Getting information from the columns one by one of contact info table
            String mobileid = rs.getString("mobileid");
            String contactid = rs.getString("contactmobileid");
            int duration = rs.getInt("duration");

            //temporary arraylist to store the groups of gathering
            ArrayList<String> temp = new ArrayList<String>();
            //adding the element in the temp arraylist
            temp.add(mobileid);
            temp.add(contactid);
            temp.add(String.valueOf(duration));
            groups.add(temp);

        }
        System.out.println(groups);
        int len = groups.size();
        // for loop that pick pair turn by turn check for other contacts of individuals in pair
        for(int i=0; i<len;i++)
        {
            //Arraylist to store pair
            ArrayList<String> pair = groups.get(i);

            //Arraylist for temporary storing pairs
            ArrayList<String> temp = new ArrayList<String>() ;

            //Arraylist to store groups that already been found to check for future reference
            ArrayList<Integer> tempRemove = new ArrayList<Integer>() ;

            //Arraylist to store the indexes of the pair of individuals retrieve from the database
            ArrayList<Integer> indexes = new ArrayList<Integer>() ;

            //Arraylist to store individuals instead of storing pair as mentioned in the project file
            //Because by this we can easily calculate vaule of "n"
            ArrayList<String> S = new ArrayList<String>();

            //Arraylist to store indexes of the final pairs that both satisfy the condition of minimum size and
            //minimum time, this arraylist element count will help to calculate c for C/M to find if the gathering
            //is large enough or not
            ArrayList<Integer> C = new ArrayList<Integer>();

            //Condition to check already formed groups in past
            if(removedGroups.contains(i))
                continue;

                String m1 = pair.get(0);
                String m2 = pair.get(1);
                int time = Integer.parseInt(pair.get(2));
                if(time >= minTime)
                {
                    C.add(i);

                }
                S.add(m1);
                S.add(m2);
                tempRemove.add(i);

                //For loop that create S and C if the conditions are satisfied then the group is formed
                for(int j=0;j<len;j++)
                {
                    //it check for pair that already used with other group
                    if(i==j || removedGroups.contains(j))
                        continue;

                    ArrayList<String> tempPair = groups.get(j);
                    int tempTime = Integer.parseInt(tempPair.get(2));
                    //condition that checks first mobile hash in pair
                    if(tempPair.contains(m1))
                    {
                        int id = tempPair.indexOf(m1);
                        //then it find the id of contact device hash
                        switch (id)
                        {
                            case 0:id=1;break;
                            case 1:id=0;break;
                        }
                        String contact = tempPair.get(id);
                        if(temp.contains(contact))
                        {
                            int cid = temp.indexOf(contact);
                            int gid = indexes.get(cid);

                            S.add(contact);
                            if(tempTime >= minTime && Integer.parseInt(groups.get(gid).get(2)) >= minTime)
                            {
                                C.add(gid);
                                C.add(j);

                            }
                            tempRemove.add(gid);
                            tempRemove.add(j);

                        }
                        else
                        {
                            temp.add(contact);
                            indexes.add(j);
                        }
                    }
                    //same as above
                    else if(tempPair.contains(m2))
                    {
                        int id = tempPair.indexOf(m2);
                        switch (id)
                        {
                            case 0:id=1;break;
                            case 1:id=0;break;
                        }
                        String contact = tempPair.get(id);
                        if(temp.contains(contact))
                        {
                            int cid = temp.indexOf(contact);
                            int gid = indexes.get(cid);

                            S.add(contact);
                            if(tempTime >= minTime && Integer.parseInt(groups.get(gid).get(2)) >= minTime)
                            {
                                C.add(gid);
                                C.add(j);
                            }
                            tempRemove.add(gid);
                            tempRemove.add(j);

                        }
                        else
                        {
                            temp.add(contact);
                            indexes.add(j);
                        }
                    }

                }
                int n = S.size();
                boolean flag=true;
                float cm = 0;
                //Condition for finding minimum size and density
                if(n >= minSize)
                {
                    float m = (float)(n*(n-1))/2;
                    int c = C.size();
                    cm = (float)(c/m);

                    if(cm>= density)
                    {
                        totalGroup++;
                    }
                    else
                    {
                        flag=false;
                    }

                }
                else
                {
                    flag=false;
                }


                if(flag == true)
                {
;
                    for(int k=0;k<tempRemove.size();k++)
                    {
                        int T = tempRemove.get(k);

                        removedGroups.add(T);
                    }
                }
                System.out.println("tempRemove : "+tempRemove);

                System.out.println("RemovedGroup : "+removedGroups);
                System.out.println("S : "+S);
                System.out.println("C : "+C);
                System.out.println("C/M : "+cm);

                System.out.println("Total Group : "+totalGroup);



        }




        return totalGroup;
    }





}




