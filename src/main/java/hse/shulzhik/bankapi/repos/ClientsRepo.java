package hse.shulzhik.bankapi.repos;

import hse.shulzhik.bankapi.models.Banks;
import hse.shulzhik.bankapi.models.Clients;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientsRepo extends JpaRepository<Clients, Long> {
    List<Clients> findAll(Sort sort);
}
