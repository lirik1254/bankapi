package hse.shulzhik.bankapi.repos;

import hse.shulzhik.bankapi.models.Banks;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanksRepo extends JpaRepository<Banks, Long> {
    List<Banks> findAll(Sort sort);
}