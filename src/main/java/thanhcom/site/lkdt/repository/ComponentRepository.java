package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Component;

import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component,Long> {
}
