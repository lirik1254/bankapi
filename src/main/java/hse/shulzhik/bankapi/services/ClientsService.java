package hse.shulzhik.bankapi.services;

import hse.shulzhik.bankapi.models.Clients;
import hse.shulzhik.bankapi.repos.ClientsRepo;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import hse.shulzhik.bankapi.util.NotFoundException.OrganisationLegalFormNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientsService {

    private ClientsRepo clientRepo;
    private OrganisationLegalFormsRepo organisationLegalFormsRepo;

    public ClientsService(ClientsRepo clientRepo, OrganisationLegalFormsRepo organisationLegalFormsRepo) {
        this.clientRepo = clientRepo;
        this.organisationLegalFormsRepo = organisationLegalFormsRepo;
    }

    public Clients updateClient(Long id, Clients newClientData) {
        if (!organisationLegalFormsRepo.existsById(newClientData.getOrganisationLegalFormId())) {
            throw new OrganisationLegalFormNotFoundException("Не существует формы с id " + newClientData.getClientId());
        }
        return clientRepo.findById(id)
                .map(client -> {
                    client.setAddress(newClientData.getAddress());
                    client.setOrganisationLegalFormId(newClientData.getOrganisationLegalFormId());
                    client.setShortName(newClientData.getShortName());
                    client.setName(newClientData.getName());
                    return clientRepo.save(client);
                })
                .orElseThrow(() -> new RuntimeException("Не получилось найти формы с id " + id));
    }
}
