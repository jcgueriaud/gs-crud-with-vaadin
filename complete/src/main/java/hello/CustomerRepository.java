package hello;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	List<Customer> findByLastNameStartsWithIgnoreCase(String lastName);
	List<Customer> findByLastNameStartsWithIgnoreCase(String lastName, Pageable pageable);

	int countByLastNameStartsWithIgnoreCase(String lastName);

	Customer findById(Long id);
}
