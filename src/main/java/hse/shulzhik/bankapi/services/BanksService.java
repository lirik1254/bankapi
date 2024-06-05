package hse.shulzhik.bankapi.services;

import hse.shulzhik.bankapi.models.Banks;
import hse.shulzhik.bankapi.models.Clients;
import hse.shulzhik.bankapi.repos.BanksRepo;
import hse.shulzhik.bankapi.repos.ClientsRepo;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BanksService {

    private BanksRepo banksRepo;

    @Autowired
    public BanksService(BanksRepo banksRepo) {
        this.banksRepo = banksRepo;
    }

    public Banks updateBank(Long id, Banks newBank) {
        return banksRepo.findById(id)
                .map(bank -> {
                    bank.setBik(newBank.getBik());
                    return banksRepo.save(bank);
                })
                .orElseThrow(() -> new RuntimeException("Client not found with id " + id));
    }
}
