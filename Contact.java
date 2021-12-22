
//Class specifically method to store contacts as per our need
//As we need three entities present together in single element of
//arraylist and along with two method to set and get the data
public class Contact {

    //Three varaibles to store information
    String individual;
    int date;
    int duration;

    //set Contact method to pass the given values to particular variable
    void setContact(String individual, int date, int duration)
    {
        this.individual = individual;
        this.date = date;
        this.duration = duration;
    }

    //getContact method to return contacts
    String getContact()
    {
        return this.individual+":"+this.date+":"+this.duration;
    }
}
