package hse.shulzhik.bankapi.controllers;

import hse.shulzhik.bankapi.models.Deposits;
import hse.shulzhik.bankapi.repos.BanksRepo;
import hse.shulzhik.bankapi.repos.ClientsRepo;
import hse.shulzhik.bankapi.repos.DepositsRepo;
import hse.shulzhik.bankapi.services.DepositsService;
import hse.shulzhik.bankapi.util.NotFoundException.BankNotFoundException;
import hse.shulzhik.bankapi.util.NotFoundException.ClientNotFoundException;
import hse.shulzhik.bankapi.util.NotFoundException.DepositNotFoundException;
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
@RequestMapping("/deposits")
public class DepositsController {

    private final DepositsService depositsService;
    private final DepositsRepo depositsRepo;
    private final BanksRepo banksRepo;
    private final ClientsRepo clientsRepo;
    final List<String> validSortFields = Arrays.asList("clientId", "bankId", "depositId", "openDate", "percent", "retentionPeriod");

    public DepositsController(DepositsService depositsService, DepositsRepo depositsRepo, BanksRepo banksRepo, ClientsRepo clientsRepo) {
        this.depositsService = depositsService;
        this.depositsRepo = depositsRepo;
        this.banksRepo = banksRepo;
        this.clientsRepo = clientsRepo;
    }

    @PostMapping
    public Deposits createDeposit(@Valid @RequestBody Deposits deposit) {
        if (!clientsRepo.existsById(deposit.getClientId()))
            throw new ClientNotFoundException("Не существует клиента с id " + deposit.getClientId());
        if (!banksRepo.existsById(deposit.getBankId()))
            throw new BankNotFoundException("Не существует банка с id " + deposit.getBankId());
        return depositsRepo.save(deposit);
    }

    @GetMapping("/{id}")
    public Deposits getDepositById(@PathVariable Long id) {
        return depositsRepo.findById(id).orElseThrow(() -> new DepositNotFoundException("Такого депозита не существует"));
    }

    @PutMapping("/{id}")
    public Deposits updateDeposit(@PathVariable Long id, @Valid @RequestBody Deposits depositDetails) {
        return depositsService.updateDeposit(id, depositDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeposit(@PathVariable Long id) {
        if (depositsRepo.existsById(id)) {
            depositsRepo.deleteById(id);
            return ResponseEntity.ok("Депозит успешно удалён");
        }
        throw new DepositNotFoundException("Такого депозита не существует");
    }

    @GetMapping()
    public List<Deposits> getAllDeposits(@RequestParam(required = false) String[] sortBy) {
        if (sortBy != null && sortBy.length > 0) {
            if (SortUtil.CheckSort(sortBy, validSortFields)) {
                return depositsRepo.findAll(Sort.by(sortBy));
            } else {
                throw new IllegalArgumentException("Неправильные параметры сортировки");
            }
        } else {
            return depositsRepo.findAll();
        }
    }




}
