package hse.shulzhik.bankapi.repos;

import hse.shulzhik.bankapi.models.Banks;
import hse.shulzhik.bankapi.models.Deposits;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositsRepo extends JpaRepository<Deposits, Long> {
    List<Deposits> findAll(Sort sort);
}
