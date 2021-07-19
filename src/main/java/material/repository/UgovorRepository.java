package material.repository;

import material.domain.Ugovor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ugovor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UgovorRepository extends JpaRepository<Ugovor, Long> {}
