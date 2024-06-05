package hse.shulzhik.bankapi.repos;

import hse.shulzhik.bankapi.models.Banks;
import hse.shulzhik.bankapi.models.OrganisationLegalForms;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganisationLegalFormsRepo extends JpaRepository<OrganisationLegalForms, Long> {
    List<OrganisationLegalForms> findAll(Sort sort);
}
