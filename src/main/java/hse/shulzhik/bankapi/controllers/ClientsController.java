package hse.shulzhik.bankapi.controllers;

import hse.shulzhik.bankapi.models.Clients;
import hse.shulzhik.bankapi.repos.ClientsRepo;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import hse.shulzhik.bankapi.services.ClientsService;
import hse.shulzhik.bankapi.util.NotFoundException.ClientNotFoundException;
import hse.shulzhik.bankapi.util.NotFoundException.OrganisationLegalFormNotFoundException;
import hse.shulzhik.bankapi.util.SortUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;




@Validated
@RestController
@RequestMapping("/clients")
public class ClientsController {

    private final ClientsService clientsService;
    private ClientsRepo clientsRepo;
    private OrganisationLegalFormsRepo organisationLegalFormsRepo;
    final List<String> validSortFields = Arrays.asList("clientId", "name", "shortName", "address", "organisationLegalFormId");

    public ClientsController(ClientsService clientsService, ClientsRepo clientsRepo, OrganisationLegalFormsRepo organisationLegalFormsRepo) {
        this.clientsService = clientsService;
        this.clientsRepo = clientsRepo;
        this.organisationLegalFormsRepo = organisationLegalFormsRepo;
    }

    @PostMapping
    public Clients createClient(@Valid @RequestBody Clients client) {
        if (!organisationLegalFormsRepo.existsById(client.getOrganisationLegalFormId())) {
            throw new OrganisationLegalFormNotFoundException("Не существует формы с id " + client.getOrganisationLegalFormId());
        }
            return clientsRepo.save(client);
    }

    @GetMapping("/{id}")
    public Clients getClientById(@PathVariable Long id) {
        return clientsRepo.findById(id).orElseThrow(() -> new ClientNotFoundException("Такого клиента не существует"));
    }

    @PutMapping("/{id}")
    public Clients updateClient(@PathVariable Long id, @Valid @RequestBody Clients clientDetails) {
        return clientsService.updateClient(id, clientDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        if (clientsRepo.existsById(id)) {
            clientsRepo.deleteById(id);
            return ResponseEntity.ok("Клиент успешно удалён");
        }
        throw new ClientNotFoundException("Такого клиента не существует");

    }

    @GetMapping()
    public List<Clients> getAllClients(@RequestParam(required = false) String[] sortBy) {
        if (sortBy != null && sortBy.length > 0) {
            if (SortUtil.CheckSort(sortBy, validSortFields)) {
                return clientsRepo.findAll(Sort.by(sortBy));
            } else {
                throw new IllegalArgumentException("Неправильные параметры сортировки");
            }
        } else {
            return clientsRepo.findAll();
        }
    }



}
