package hse.shulzhik.bankapi.services;

import hse.shulzhik.bankapi.models.Clients;
import hse.shulzhik.bankapi.models.Deposits;
import hse.shulzhik.bankapi.repos.BanksRepo;
import hse.shulzhik.bankapi.repos.ClientsRepo;
import hse.shulzhik.bankapi.repos.DepositsRepo;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import hse.shulzhik.bankapi.util.NotFoundException.BankNotFoundException;
import hse.shulzhik.bankapi.util.NotFoundException.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepositsService {

    private DepositsRepo depositsRepo;
    private BanksRepo banksRepo;
    private ClientsRepo clientsRepo;

    public DepositsService(DepositsRepo depositsRepo, BanksRepo banksRepo, ClientsRepo clientsRepo) {
        this.depositsRepo = depositsRepo;
        this.banksRepo = banksRepo;
        this.clientsRepo = clientsRepo;
    }

    public Deposits updateDeposit(Long id, Deposits newDepositData) {
        if (!clientsRepo.existsById(newDepositData.getClientId()))
            throw new ClientNotFoundException("Не существует клиента с id " + newDepositData.getClientId());
        if (!banksRepo.existsById(newDepositData.getBankId()))
            throw new BankNotFoundException("Не существует банка с id " + newDepositData.getBankId());
        return depositsRepo.findById(id)
                .map(deposit -> {
                    deposit.setBankId(newDepositData.getBankId());
                    deposit.setPercent(newDepositData.getPercent());
                    deposit.setOpenDate(newDepositData.getOpenDate());
                    deposit.setRetentionPeriod(newDepositData.getRetentionPeriod());
                    deposit.setClientId(newDepositData.getClientId());
                    return depositsRepo.save(deposit);
                })
                .orElseThrow(() -> new RuntimeException("Client not found with id " + id));
    }
}
