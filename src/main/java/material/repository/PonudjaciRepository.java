package material.repository;

import material.domain.Ponudjaci;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ponudjaci entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PonudjaciRepository extends JpaRepository<Ponudjaci, Long> {}
