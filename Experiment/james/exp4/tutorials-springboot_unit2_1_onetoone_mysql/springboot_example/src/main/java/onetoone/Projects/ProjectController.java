package onetoone.Projects;

import onetoone.Laptops.Laptop;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/projects")
    List<Project> getAllProjects(){return projectRepository.findAll();}

    @GetMapping(path = "/projects/{id}")
    Project getProjectById(@PathVariable int id){
        return projectRepository.findById(id);
    }

    @PostMapping(path = "/projects")
    String createProject(@RequestBody Project project){
        if (project == null)
            return failure;
        projectRepository.save(project);
        return success;
    }

    @PutMapping("/projects/{id}")
    Project updateProject(@PathVariable int id, @RequestBody Project request){
        Project project = projectRepository.findById(id);
        if(project == null)
            return null;
        projectRepository.save(request);
        return projectRepository.findById(id);
    }

    @DeleteMapping(path = "/projects/{id}")
    String deleteProject(@PathVariable int id){
        projectRepository.deleteById(id);
        return success;
    }

}
