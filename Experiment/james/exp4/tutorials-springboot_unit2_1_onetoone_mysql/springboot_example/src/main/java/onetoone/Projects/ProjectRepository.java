package onetoone.Projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findById(int id);

    @Transactional
    void deleteById(int id);
}
