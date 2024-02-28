package onetoone.Cars;

import onetoone.Laptops.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
    public interface CarsRepository extends JpaRepository <Cars, Long> {
        Cars findById(int id);

        @Transactional
        void deleteById(int id);
    }
