package onetoone.Cars;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import onetoone.Users.User;

/**
 *
 * @author Vivek Bengre
 */

@Entity
public class Cars {

    /*
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present, The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int Speed;
    private int Mpg;


    /*
     * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User)
     * @JsonIgnore is to assure that there is no infinite loop while returning either user/laptop objects (laptop->user->laptop->...)
     */
    @OneToOne
    @JsonIgnore
    private User user;

    public Cars( int Speed, int mpg) {
        this.Speed = Speed;
        this.Mpg = mpg;
    }

    public Cars() {

    }



    // =============================== Getters and Setters for each field ================================== //

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }


    public void setSpeed(int Speed){
        this.Speed = Speed;
    }

    public int getSpeed(){
        return this.Speed;
    }

    public void setMpg(int Mpg) {
        this.Mpg = Mpg;
    }

    public int getMpg() {
        return this.Mpg;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }



}
