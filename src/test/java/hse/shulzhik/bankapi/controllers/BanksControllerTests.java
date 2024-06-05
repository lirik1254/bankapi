package hse.shulzhik.bankapi.controllers;

import hse.shulzhik.bankapi.models.Banks;
import hse.shulzhik.bankapi.repos.BanksRepo;
import hse.shulzhik.bankapi.services.BanksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BanksController.class)
public class BanksControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BanksService banksService;

    @MockBean
    private BanksRepo banksRepo;

    @Test
    public void createBankTest() throws Exception {

        Banks bank = new Banks(1L, "343434343");

        when(banksRepo.save(any(Banks.class))).thenReturn(bank);

        mockMvc.perform(post("/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\": \"343434343\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankId").value(1L))
                .andExpect(jsonPath("$.bik").value("343434343"));
    }

    @Test
    public void createWithWrongLengthTest() throws Exception {

        mockMvc.perform(post("/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\": \"34343\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.bik").value("Бик - девятизначное число"));
    }

    @Test
    public void createWithLettersTest() throws Exception {

        mockMvc.perform(post("/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\": \"asdffdfds\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.bik").value("Бик - девятизначное число"));
    }

    @Test
    public void getBankByIdTest() throws Exception {

        Banks bank = new Banks(1L, "111111111");

        when(banksRepo.findById(1L)).thenReturn(Optional.of(bank));

        mockMvc.perform(get("/banks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankId").value(1L))
                .andExpect(jsonPath("$.bik").value("111111111"));
    }

    @Test
    public void getBankByWrongIdTest() throws Exception {

        mockMvc.perform(get("/banks/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такого банка не существует"));
    }

    @Test
    public void updateBankTest() throws Exception {

        Banks existingBank = new Banks(1L, "123456789");

        Banks updatedBank = new Banks(1L, "987654321");

        when(banksService.updateBank(anyLong(), any(Banks.class))).thenReturn(updatedBank);
        when(banksRepo.findById(1L)).thenReturn(Optional.of(existingBank));

        mockMvc.perform(put("/banks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\": \"987654321\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankId").value(1L))
                .andExpect(jsonPath("$.bik").value("987654321"));
    }

    @Test
    public void wrongLengthUpdateBankTest() throws Exception {

        Banks existingBank = new Banks(1L, "123456789");

        Banks updatedBank = new Banks(1L, "111");

        when(banksService.updateBank(anyLong(), any(Banks.class))).thenReturn(updatedBank);
        when(banksRepo.findById(1L)).thenReturn(Optional.of(existingBank));

        mockMvc.perform(put("/banks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\": \"111\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.bik").value("Бик - девятизначное число"));
    }

    @Test
    public void letterInBikUpdateBankTest() throws Exception {
        Banks existingBank = new Banks(1L, "123456789");

        Banks updatedBank = new Banks(1L, "asdfdfdfd");

        when(banksService.updateBank(anyLong(), any(Banks.class))).thenReturn(updatedBank);
        when(banksRepo.findById(1L)).thenReturn(Optional.of(existingBank));

        mockMvc.perform(put("/banks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\": \"asdfdfdfd\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.bik").value("Бик - девятизначное число"));
    }

    @Test
    public void deleteBankTest() throws Exception {

        when(banksRepo.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/banks/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Банк успешно удален"));
    }

    @Test
    public void deleteWrongBankTest() throws Exception {

        mockMvc.perform(delete("/banks/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такого банка не существует"));
    }


    @Test
    public void getAllBanksNotEmptyTest() throws Exception {

        Banks bank1 = new Banks(1L, "111111111");

        Banks bank2 = new Banks(2L, "222222222");

        when(banksRepo.findAll()).thenReturn(Arrays.asList(bank1, bank2));

        mockMvc.perform(get("/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bankId").value(1L))
                .andExpect(jsonPath("$[0].bik").value("111111111"))
                .andExpect(jsonPath("$[1].bankId").value(2L))
                .andExpect(jsonPath("$[1].bik").value("222222222"));
    }

    @Test
    public void getAllBanksEmptyTest() throws Exception {


        mockMvc.perform(get("/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllBanksSortedTest() throws Exception {
        Banks bank1 = new Banks(1L, "123456789");
        Banks bank2 = new Banks(2L, "987654321");
        Banks bank3 = new Banks(3L, "555555555");

        List<Banks> banksList = Arrays.asList(bank1, bank2, bank3);

        List<Banks> expectedSortedBanks = Arrays.asList(bank3, bank1, bank2);

        when(banksRepo.findAll()).thenReturn(banksList);
        when(banksRepo.findAll(Sort.by("bik"))).thenReturn(expectedSortedBanks);

        mockMvc.perform(get("/banks")
                        .param("sortBy", "bik"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bik").value("555555555"))
                .andExpect(jsonPath("$[1].bik").value("123456789"))
                .andExpect(jsonPath("$[2].bik").value("987654321"));

    }

    @Test
    public void badParamSortedTest() throws Exception {
        Banks bank1 = new Banks(1L, "123456789");
        Banks bank2 = new Banks(2L, "987654321");
        Banks bank3 = new Banks(3L, "555555555");

        List<Banks> banksList = Arrays.asList(bank1, bank2, bank3);

        List<Banks> expectedSortedBanks = Arrays.asList(bank3, bank1, bank2);

        when(banksRepo.findAll()).thenReturn(banksList);
        when(banksRepo.findAll(Sort.by("bik"))).thenReturn(expectedSortedBanks);

        mockMvc.perform(get("/banks")
                        .param("sortBy", "bikd"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Неправильные параметры сортировки"));
    }

}
