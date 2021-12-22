import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String args[]) throws SQLException, ClassNotFoundException, InterruptedException {

        boolean test;

        Government go = new Government("C:\\Users\\ASUS\\Desktop\\Project Demo\\src\\GovernmentConfig.txt");

        MobileDevice m1 = new MobileDevice("C:\\Users\\ASUS\\Desktop\\Project Demo\\src\\MobileConfig.txt",go);
//        m1.start();
        MobileDevice m2 = new MobileDevice("C:\\Users\\ASUS\\Desktop\\Project Demo\\src\\MD2.txt",go);
//        m2.start();
        MobileDevice m3 = new MobileDevice("C:\\Users\\ASUS\\Desktop\\Project Demo\\src\\MD3.txt",go);
//        m3.start();
        MobileDevice m4 = new MobileDevice("C:\\Users\\ASUS\\Desktop\\Project Demo\\src\\MD4.txt",go);
//        m4.start();
        MobileDevice m5 = new MobileDevice("C:\\Users\\ASUS\\Desktop\\Project Demo\\src\\MD5.txt",go);
//        m5.start();
        MobileDevice m6 = new MobileDevice("C:\\Users\\ASUS\\Desktop\\Project Demo\\src\\MD6.txt",go);
        

        m1.recordContact(m2.getMobileid(),30,40);
        m2.recordContact(m1.getMobileid(),30,40);

        m1.recordContact(m2.getMobileid(),31,50);
        m1.recordContact(m3.getMobileid(),31,20);
        m1.recordContact(m4.getMobileid(),31,15);
        m2.recordContact(m1.getMobileid(),31,20);
        m2.recordContact(m3.getMobileid(),31,20);
        m2.recordContact(m4.getMobileid(),31,15);


        m1.recordContact(m2.getMobileid(),32,50);
        m1.recordContact(m3.getMobileid(),32,20);
        m1.recordContact(m4.getMobileid(),32,15);
        m2.recordContact(m1.getMobileid(),32,20);
        m2.recordContact(m3.getMobileid(),32,20);
        m2.recordContact(m4.getMobileid(),32,15);

        go.findGatherings(31,2,20,(float) 0.25);
        go.findGatherings(32,2,10,(float) 0.1);
        go.recordTestResult("covid1",29,true);
        m1.positiveTest("covid1");

        go.recordTestResult("covid2",33,false);
        m2.positiveTest("covid2");

        go.recordTestResult("covid3",33,true);
        m3.positiveTest("covid3");

        go.recordTestResult("covid4",40,false);
        m3.positiveTest("covid4");

        m1.synchronizeData();
        m2.synchronizeData();
        m3.synchronizeData();
        m4.synchronizeData();
        m5.synchronizeData();

        //m1.join();
        //m2.join();
        //m3.join();
        //m4.join();
        //m5.join();

    }
}
