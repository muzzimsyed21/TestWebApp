package models;

import java.util.ArrayList;

/**
 * Created by ka9mse on 1/28/2016.
 */
public class Star {
    public String name, dob, picture, id;
    public ArrayList<Movie> movies;

    public Star(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Star(String name, String dob, String picURL, String id, ArrayList<Movie> movies) {
        this.name = name;
        this.dob = dob;
        this.picture = picURL;
        this.id = id;
        this.movies = movies;
    }
}
