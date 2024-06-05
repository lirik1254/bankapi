package hse.shulzhik.bankapi.services;

import hse.shulzhik.bankapi.models.OrganisationLegalForms;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganisationLegalFormsService {
    private final OrganisationLegalFormsRepo organisationLegalFormRepo;

    @Autowired
    public OrganisationLegalFormsService(OrganisationLegalFormsRepo organisationLegalFormRepo) {
        this.organisationLegalFormRepo = organisationLegalFormRepo;
    }

    public OrganisationLegalForms updateOrganisationLegalForm(Long id, OrganisationLegalForms newFormData) {
        return organisationLegalFormRepo.findById(id)
                .map(form -> {
                    form.setName(newFormData.getName());
                    return organisationLegalFormRepo.save(form);
                })
                .orElseThrow(() -> new RuntimeException("OrganisationLegalForm not found with id " + id));
    }
}
