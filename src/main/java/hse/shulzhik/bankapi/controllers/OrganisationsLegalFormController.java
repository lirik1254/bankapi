package hse.shulzhik.bankapi.controllers;
import hse.shulzhik.bankapi.models.OrganisationLegalForms;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import hse.shulzhik.bankapi.services.OrganisationLegalFormsService;
import hse.shulzhik.bankapi.util.NotFoundException.DepositNotFoundException;
import hse.shulzhik.bankapi.util.NotFoundException.OrganisationLegalFormNotFoundException;
import hse.shulzhik.bankapi.util.SortUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@Validated
@RestController
@RequestMapping("/forms")
public class OrganisationsLegalFormController {
    private final OrganisationLegalFormsService organisationLegalFormsService;
    private final OrganisationLegalFormsRepo organisationLegalFormsRepo;
    final List<String> validSortFields = Arrays.asList("organisationLegalFormId", "name");

    @Autowired
    public OrganisationsLegalFormController(OrganisationLegalFormsService organisationLegalFormService, OrganisationLegalFormsRepo organisationLegalFormRepo) {
        this.organisationLegalFormsService = organisationLegalFormService;
        this.organisationLegalFormsRepo = organisationLegalFormRepo;
    }

    @PostMapping
    public OrganisationLegalForms createForm(@Valid @RequestBody OrganisationLegalForms organisationLegalForm) {
        return organisationLegalFormsRepo.save(organisationLegalForm);
    }

    @GetMapping("/{id}")
    public OrganisationLegalForms getForm(@PathVariable Long id) {
        return organisationLegalFormsRepo.findById(id).orElseThrow(() -> new DepositNotFoundException("Такой формы не существует"));
    }

    @PutMapping("/{id}")
    public OrganisationLegalForms updateForm(@PathVariable Long id, @Valid @RequestBody OrganisationLegalForms organisationLegalForm) {
        return organisationLegalFormsService.updateOrganisationLegalForm(id, organisationLegalForm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteForm(@PathVariable Long id) {
        if (organisationLegalFormsRepo.existsById(id)) {
            organisationLegalFormsRepo.deleteById(id);
            return ResponseEntity.ok("Форма была успешно удалена");
        }
        throw new OrganisationLegalFormNotFoundException("Такой формы не существует");
    }

    @GetMapping
    public List<OrganisationLegalForms> getAllForms(@RequestParam(required = false) String[] sortBy) {
        if (sortBy != null && sortBy.length > 0) {
            if (SortUtil.CheckSort(sortBy, validSortFields)) {
                return organisationLegalFormsRepo.findAll(Sort.by(sortBy));
            } else {
                throw new IllegalArgumentException("Неправильные параметры сортировки");
            }
        } else {
            return organisationLegalFormsRepo.findAll();
        }
    }



}
