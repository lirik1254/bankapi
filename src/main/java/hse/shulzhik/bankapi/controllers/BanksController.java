package hse.shulzhik.bankapi.controllers;

import hse.shulzhik.bankapi.models.Banks;
import hse.shulzhik.bankapi.models.Clients;
import hse.shulzhik.bankapi.repos.BanksRepo;
import hse.shulzhik.bankapi.services.BanksService;
import hse.shulzhik.bankapi.util.NotFoundException.BankNotFoundException;
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
@RequestMapping("/banks")
public class BanksController {

    public final BanksService banksService;
    public final BanksRepo banksRepo;
    List<String> validSortFields = Arrays.asList("bankId", "bik");

    @Autowired
    public BanksController(BanksService bankService, BanksRepo banksRepo) {
        this.banksService = bankService;
        this.banksRepo = banksRepo;
    }

    @PostMapping
    public Banks createBank(@RequestBody @Valid Banks bank) {
        return banksRepo.save(bank);
    }

    @GetMapping("/{id}")
    public Banks getBankById(@PathVariable Long id) {
        return banksRepo.findById(id).orElseThrow(() -> new BankNotFoundException("Такого банка не существует"));
    }

    @PutMapping("/{id}")
    public Banks updateBank(@PathVariable Long id, @Valid @RequestBody Banks bankDetails) {
        return banksService.updateBank(id, bankDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBank(@PathVariable Long id) {
        if (banksRepo.existsById(id)) {
            banksRepo.deleteById(id);
            return ResponseEntity.ok("Банк успешно удален");
        }
        throw new BankNotFoundException("Такого банка не существует");
    }

    @GetMapping()
    public List<Banks> getAllBanks(@RequestParam(required = false) String[] sortBy) {
        if (sortBy != null && sortBy.length > 0) {
            if (SortUtil.CheckSort(sortBy, validSortFields)) {
                return banksRepo.findAll(Sort.by(sortBy));
            } else {
                throw new IllegalArgumentException("Неправильные параметры сортировки");
            }
        } else {
            return banksRepo.findAll();
        }
    }



}
