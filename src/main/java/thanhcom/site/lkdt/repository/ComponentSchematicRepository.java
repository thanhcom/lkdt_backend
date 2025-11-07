package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.ComponentSchematic;

import java.util.List;

@Repository
public interface ComponentSchematicRepository extends JpaRepository<ComponentSchematic , Long> {
    List<ComponentSchematic> findByComponentId(Long componentId);
}
