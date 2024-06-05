package hse.shulzhik.bankapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.shulzhik.bankapi.models.Deposits;
import hse.shulzhik.bankapi.repos.BanksRepo;
import hse.shulzhik.bankapi.repos.ClientsRepo;
import hse.shulzhik.bankapi.repos.DepositsRepo;
import hse.shulzhik.bankapi.services.DepositsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
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
@WebMvcTest(DepositsController.class)
public class DepositControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepositsService depositsService;

    @MockBean
    private DepositsRepo depositsRepo;

    @MockBean
    private BanksRepo banksRepo;

    @MockBean
    private ClientsRepo clientsRepo;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void createDepositTest() throws Exception {
        String depositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        Deposits deposit = objectMapper.readValue(depositJson, Deposits.class);

        when(depositsRepo.save(any(Deposits.class))).thenReturn(deposit);

        when(clientsRepo.existsById(any(Long.class))).thenReturn(true);
        when(banksRepo.existsById(any(Long.class))).thenReturn(true);

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depositId").value(1L))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.bankId").value(1L))
                .andExpect(jsonPath("$.openDate").value("2022-10-10"))
                .andExpect(jsonPath("$.percent").value(6.5))
                .andExpect(jsonPath("$.retentionPeriod").value(12L));
    }

    @Test
    public void createDepositWithNonExistingClientTest() throws Exception {
        String depositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        Deposits deposit = objectMapper.readValue(depositJson, Deposits.class);

        when(depositsRepo.save(any(Deposits.class))).thenReturn(deposit);
        when(clientsRepo.existsById(any(Long.class))).thenReturn(false);

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Не существует клиента с id 1"));
    }

    @Test
    public void createDepositWithNonExistingBankTest() throws Exception {
        String depositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        Deposits deposit = objectMapper.readValue(depositJson, Deposits.class);

        when(depositsRepo.save(any(Deposits.class))).thenReturn(deposit);
        when(banksRepo.existsById(any(Long.class))).thenReturn(false);

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Не существует клиента с id 1"));
    }

    @Test
    public void createDepositWithSomeEmptyFieldTest() throws Exception {
        when(clientsRepo.existsById(any(Long.class))).thenReturn(true);
        when(banksRepo.existsById(any(Long.class))).thenReturn(true);

        String depositJson = "{\"clientId\": 1, \"bankId\": 1, \"openDate\": \"2024-06-05\", \"percent\": 6.5}";

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.retentionPeriod").value("Поле должно быть заполнено!"));
    }

    @Test
    public void createDepositWithMoreThanOneEmptyFieldTest() throws Exception {
        when(clientsRepo.existsById(any(Long.class))).thenReturn(true);
        when(banksRepo.existsById(any(Long.class))).thenReturn(true);

        String depositJson = "{\"clientId\": 1, \"bankId\": 1, \"percent\": 6.5}";

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.openDate").value("Поле должно быть заполнено!"))
                .andExpect(jsonPath("$.retentionPeriod").value("Поле должно быть заполнено!"));
    }

    @Test
    public void createDepositWithInvalidClientIdTest() throws Exception {
        String depositJson = "{\"clientId\": 0, \"bankId\": 1, \"openDate\": \"2024-06-05\", \"percent\": 6.5, \"retentionPeriod\": 12}";

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.clientId").value("Id должно быть больше нуля!"));
    }

    @Test
    public void createDepositWithInvalidBankIdTest() throws Exception {
        String depositJson = "{\"clientId\": 1, \"bankId\": 0, \"openDate\": \"2024-06-05\", \"percent\": 6.5, \"retentionPeriod\": 12}";

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.bankId").value("Id должно быть больше нуля!"));
    }

    @Test
    public void createDepositWithNegativePercentTest() throws Exception {
        String depositJson = "{\"clientId\": 1, \"bankId\": 1, \"openDate\": \"2024-06-05\", \"percent\": -6.5, \"retentionPeriod\": 12}";

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.percent").value("Процент должен быть не меньше нуля!"));
    }

    @Test
    public void createDepositWithNegativeRetentionPeriodTest() throws Exception {
        String depositJson = "{\"clientId\": 1, \"bankId\": 1, \"openDate\": \"2024-06-05\", \"percent\": 6.5, \"retentionPeriod\": -12}";

        mockMvc.perform(post("/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.retentionPeriod").value("Срок должен быть больше нуля!"));
    }

    @Test
    public void getDepositByIdTest() throws Exception {
        Deposits deposit = new Deposits(1L, 1L, 1L, LocalDate.now(), 6.5, 12L);
        when(depositsRepo.findById(1L)).thenReturn(Optional.of(deposit));

        mockMvc.perform(get("/deposits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depositId").value(1L))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.bankId").value(1L))
                .andExpect(jsonPath("$.openDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.percent").value(6.5))
                .andExpect(jsonPath("$.retentionPeriod").value(12L));
    }

    @Test
    public void getDepositByWrongIdTest() throws Exception {
        mockMvc.perform(get("/deposits/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такого депозита не существует"));
    }

    @Test
    public void updateDepositTest() throws Exception {
        String existingDepositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        String updateDepositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":7.0,\"retentionPeriod\":24}";

        Deposits existingDeposit = objectMapper.readValue(existingDepositJson, Deposits.class);
        Deposits updateDeposit = objectMapper.readValue(updateDepositJson, Deposits.class);

        when(depositsService.updateDeposit(anyLong(), any(Deposits.class))).thenReturn(updateDeposit);
        when(depositsRepo.findById(1L)).thenReturn(Optional.of(existingDeposit));

        mockMvc.perform(put("/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDepositJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depositId").value(1L))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.bankId").value(1L))
                .andExpect(jsonPath("$.openDate").value("2022-10-10"))
                .andExpect(jsonPath("$.percent").value(7.0))
                .andExpect(jsonPath("$.retentionPeriod").value(24L));
    }

    @Test
    public void updateDepositWithSomeFieldEmpty() throws Exception {
        String existingDepositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        Deposits existingDeposit = objectMapper.readValue(existingDepositJson, Deposits.class);

        when(depositsRepo.findById(1L)).thenReturn(Optional.of(existingDeposit));

        String depositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":7.0}";

        mockMvc.perform(put("/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.retentionPeriod").value("Поле должно быть заполнено!"));
    }

    @Test
    public void updateDepositWithMoreThanOneEmptyField() throws Exception {
        String existingDepositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        Deposits existingDeposit = objectMapper.readValue(existingDepositJson, Deposits.class);

        when(depositsRepo.findById(1L)).thenReturn(Optional.of(existingDeposit));

        String depositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"percent\":7.0}";

        mockMvc.perform(put("/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.openDate").value("Поле должно быть заполнено!"))
                .andExpect(jsonPath("$.retentionPeriod").value("Поле должно быть заполнено!"));
    }

    @Test
    public void updateDepositWithInvalidClientIdTest() throws Exception {
        String depositJson = "{\"clientId\": 0, \"bankId\": 1, \"openDate\": \"2024-06-05\", \"percent\": 6.5, \"retentionPeriod\": 12}";

        mockMvc.perform(put("/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.clientId").value("Id должно быть больше нуля!"));
    }

    @Test
    public void updateDepositWithInvalidBankIdTest() throws Exception {
        String depositJson = "{\"clientId\": 1, \"bankId\": 0, \"openDate\": \"2024-06-05\", \"percent\": 6.5, \"retentionPeriod\": 12}";

        mockMvc.perform(put("/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.bankId").value("Id должно быть больше нуля!"));
    }

    @Test
    public void updateDepositWithNegativePercentTest() throws Exception {
        String depositJson = "{\"clientId\": 1, \"bankId\": 1, \"openDate\": \"2024-06-05\", \"percent\": -6.5, \"retentionPeriod\": 12}";

        mockMvc.perform(put("/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.percent").value("Процент должен быть не меньше нуля!"));
    }

    @Test
    public void updateDepositWithNegativeRetentionPeriodTest() throws Exception {
        String depositJson = "{\"clientId\": 1, \"bankId\": 1, \"openDate\": \"2024-06-05\", \"percent\": 6.5, \"retentionPeriod\": -12}";

        mockMvc.perform(put("/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.retentionPeriod").value("Срок должен быть больше нуля!"));
    }

    @Test
    public void deleteDepositTest() throws Exception {
        when(depositsRepo.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/deposits/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Депозит успешно удалён"));
    }

    @Test
    public void deleteWrongDepositTest() throws Exception {
        mockMvc.perform(delete("/deposits/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такого депозита не существует"));
    }

    @Test
    public void getAllDepositsNotEmptyTest() throws Exception {
        Deposits deposit1 = new Deposits(1L, 1L, 1L, LocalDate.now(), 6.5, 12L);
        Deposits deposit2 = new Deposits(2L, 1L, 1L, LocalDate.now(), 7.0, 24L);

        when(depositsRepo.findAll()).thenReturn(Arrays.asList(deposit1, deposit2));

        mockMvc.perform(get("/deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].depositId").value(1L))
                .andExpect(jsonPath("$[0].clientId").value(1L))
                .andExpect(jsonPath("$[0].bankId").value(1L))
                .andExpect(jsonPath("$[0].openDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].percent").value(6.5))
                .andExpect(jsonPath("$[0].retentionPeriod").value(12L))
                .andExpect(jsonPath("$[1].depositId").value(2L))
                .andExpect(jsonPath("$[1].clientId").value(1L))
                .andExpect(jsonPath("$[1].bankId").value(1L))
                .andExpect(jsonPath("$[1].openDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[1].percent").value(7.0))
                .andExpect(jsonPath("$[1].retentionPeriod").value(24L));
    }

    @Test
    public void getAllDepositsEmptyTest() throws Exception {
        mockMvc.perform(get("/deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllDepositsSortedTest() throws Exception {

        String existingDepositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        Deposits deposit1 = objectMapper.readValue(existingDepositJson, Deposits.class);

        String deposit2Json = "{\"depositId\":2,\"clientId\":1,\"bankId\":1,\"openDate\":\"2023-01-15\",\"percent\":7.0,\"retentionPeriod\":24}";
        Deposits deposit2 = objectMapper.readValue(deposit2Json, Deposits.class);

        String deposit3Json = "{\"depositId\":3,\"clientId\":1,\"bankId\":1,\"openDate\":\"2023-05-20\",\"percent\":5.0,\"retentionPeriod\":6}";
        Deposits deposit3 = objectMapper.readValue(deposit3Json, Deposits.class);

        List<Deposits> depositsList = Arrays.asList(deposit1, deposit2, deposit3);

        List<Deposits> expectedSortedDeposits = Arrays.asList(deposit3, deposit1, deposit2);

        when(depositsRepo.findAll()).thenReturn(depositsList);
        when(depositsRepo.findAll(Sort.by("percent"))).thenReturn(expectedSortedDeposits);

        mockMvc.perform(get("/deposits")
                        .param("sortBy", "percent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percent").value(5.0))
                .andExpect(jsonPath("$[1].percent").value(6.5))
                .andExpect(jsonPath("$[2].percent").value(7.0));
    }

    @Test
    public void badParamsSortTest() throws Exception {

        String existingDepositJson = "{\"depositId\":1,\"clientId\":1,\"bankId\":1,\"openDate\":\"2022-10-10\",\"percent\":6.5,\"retentionPeriod\":12}";
        Deposits deposit1 = objectMapper.readValue(existingDepositJson, Deposits.class);

        String deposit2Json = "{\"depositId\":2,\"clientId\":1,\"bankId\":1,\"openDate\":\"2023-01-15\",\"percent\":7.0,\"retentionPeriod\":24}";
        Deposits deposit2 = objectMapper.readValue(deposit2Json, Deposits.class);

        String deposit3Json = "{\"depositId\":3,\"clientId\":1,\"bankId\":1,\"openDate\":\"2023-05-20\",\"percent\":5.0,\"retentionPeriod\":6}";
        Deposits deposit3 = objectMapper.readValue(deposit3Json, Deposits.class);

        List<Deposits> depositsList = Arrays.asList(deposit1, deposit2, deposit3);

        List<Deposits> expectedSortedDeposits = Arrays.asList(deposit3, deposit1, deposit2);

        when(depositsRepo.findAll()).thenReturn(depositsList);
        when(depositsRepo.findAll(Sort.by("percent"))).thenReturn(expectedSortedDeposits);

        mockMvc.perform(get("/deposits")
                        .param("sortBy", "perasdcent"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Неправильные параметры сортировки"));
    }
}
