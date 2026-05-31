package ec.espe.hardware.repository;

import ec.espe.hardware.entity.HardwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HardwareRepository extends JpaRepository<HardwareEntity, Long> {
}