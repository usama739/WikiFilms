package com.sajawal.imdb.user;

public class UserCredentials {
    //This is Model Class for User Credentials
    public String username,email,password,confirmpassword;

    public UserCredentials(){
        //empty constructor to get access to members of class
    }
    public UserCredentials(String username,String email, String password, String confirmpassword){
        this.username=username;
        this.email=email;
        this.password=password;
        this.confirmpassword=confirmpassword;
    }
}
