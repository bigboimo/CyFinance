package onetoone.Users;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import onetoone.Laptops.Laptop;
import onetoone.Projects.Project;

import java.util.*;

/**
 * 
 * @author Vivek Bengre
 * 
 */ 

@Entity
@Table(name = "user")
public class User {

     /* 
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present, The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String emailId;
    private boolean ifActive;

    /*
     * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User)
     * cascade is responsible propagating all changes, even to children of the class Eg: changes made to laptop within a user object will be reflected
     * in the database (more info : https://www.baeldung.com/jpa-cascade-types)
     * @JoinColumn defines the ownership of the foreign key i.e. the user table will have a field called laptop_id
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "laptop_id")
    private Laptop laptop;

    @OneToMany
    private List<Project> projects;

    public User(String name, String emailId) {
        this.name = name;
        this.emailId = emailId;
        this.ifActive = true;
        this.projects = new ArrayList<>();
    }

    public User() {
    }

    // =============================== Getters and Setters for each field ================================== //

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmailId(){
        return emailId;
    }

    public void setEmailId(String emailId){
        this.emailId = emailId;
    }

    public boolean getIsActive(){
        return ifActive;
    }

    public void setIfActive(boolean ifActive){
        this.ifActive = ifActive;
    }

    public Laptop getLaptop(){
        return laptop;
    }

    public void setLaptop(Laptop laptop){
        this.laptop = laptop;
    }

    public List<Project> getProjects() {return this.projects;}

    public void setProjects(List<Project> projects) {this.projects = projects;}

    public void addProject(Project project) {this.projects.add(project);}
}
