package hse.shulzhik.bankapi.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import hse.shulzhik.bankapi.models.Clients;
import hse.shulzhik.bankapi.repos.ClientsRepo;
import hse.shulzhik.bankapi.repos.DepositsRepo;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import hse.shulzhik.bankapi.services.ClientsService;
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
@WebMvcTest(ClientsController.class)
public class ClientsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientsService clientsService;

    @MockBean
    private ClientsRepo clientsRepo;

    @MockBean
    private OrganisationLegalFormsRepo organisationLegalFormsRepo;


    @Test
    public void createClientTest() throws Exception {

        Clients client = new Clients(1L, "INCOM", "INC", "Ул. Запорожская, д.13", 2L);
        when(clientsRepo.save(any(Clients.class))).thenReturn(client);

        String clientJson = new ObjectMapper().writeValueAsString(client);

        when(organisationLegalFormsRepo.existsById(any(Long.class))).thenReturn(true);

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.name").value("INCOM"))
                .andExpect(jsonPath("$.shortName").value("INC"))
                .andExpect(jsonPath("$.address").value("Ул. Запорожская, д.13"))
                .andExpect(jsonPath("$.organisationLegalFormId").value(2L));
    }

    @Test
    public void createClientWithNonExistingForm() throws Exception {
        Clients client = new Clients(1L, "INCOM", "INC", "Ул. Запорожская, д.13", 2L);
        when(clientsRepo.save(any(Clients.class))).thenReturn(client);

        String clientJson = new ObjectMapper().writeValueAsString(client);


        when(organisationLegalFormsRepo.existsById(any(Long.class))).thenReturn(false);

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Не существует формы с id 2"));
    }

    @Test
    public void createClientWithSomeEmptyField() throws Exception {
        when(organisationLegalFormsRepo.existsById(any(Long.class))).thenReturn(true);

        String clientJson = "{\"shortName\":\"INC\", \"address\":\"Запорожская 15\", \"organisationLegalFormId\": 2}";

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Поле должно быть заполнено!"));
    }

    @Test
    public void createClientWithMoreThanOneEmptyField() throws Exception {
        when(organisationLegalFormsRepo.existsById(any(Long.class))).thenReturn(true);

        String clientJson = "{\"address\":\"Запорожская 15\", \"organisationLegalFormId\": 2}";

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Поле должно быть заполнено!"))
                .andExpect(jsonPath("$.shortName").value("Поле должно быть заполнено!"));
    }

    @Test
    public void getClientByIdTest() throws Exception {

        Clients client = new Clients(1L, "INCOM", "INC", "Ул. Запорожская, д.13", 2L);

        when(clientsRepo.findById(1L)).thenReturn(Optional.of(client));

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.name").value("INCOM"))
                .andExpect(jsonPath("$.shortName").value("INC"))
                .andExpect(jsonPath("$.address").value("Ул. Запорожская, д.13"))
                .andExpect(jsonPath("$.organisationLegalFormId").value(2L));
    }

    @Test
    public void getClientByWrongIdTest() throws Exception {

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такого клиента не существует"));
    }

    @Test
    public void updateClientTest() throws Exception {

        Clients existingClient = new Clients(1L, "INCOM", "INC", "Ул. Запорожская, д.13", 2L);
        Clients updateClient = new Clients(1L, "KartoshkaK", "INC", "Ул. Запорожская, д.13", 2L);

        when(clientsService.updateClient(anyLong(), any(Clients.class))).thenReturn(updateClient);
        when(clientsRepo.findById(1L)).thenReturn(Optional.of(existingClient));

        String clientJson = new ObjectMapper().writeValueAsString(updateClient);

        mockMvc.perform(put("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.name").value("KartoshkaK"))
                .andExpect(jsonPath("$.shortName").value("INC"))
                .andExpect(jsonPath("$.address").value("Ул. Запорожская, д.13"))
                .andExpect(jsonPath("$.organisationLegalFormId").value(2L));
    }

    @Test
    public void updateClientWithSomeFieldEmpty() throws Exception {
        Clients existingClient = new Clients(1L, "INCOM", "INC", "Ул. Запорожская, д.13", 2L);

        when(clientsRepo.findById(1L)).thenReturn(Optional.of(existingClient));

        String clientJson = "{\"id\":1,\"shortName\":\"INC\",\"address\":\"Ул. Запорожская, д.13\",\"organisationLegalFormId\":2}";

        mockMvc.perform(put("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Поле должно быть заполнено!"));
    }

    @Test
    public void updateClientWithMoreThanOneEmptyField() throws Exception {
        Clients existingClient = new Clients(1L, "INCOM", "INC", "Ул. Запорожская, д.13", 2L);

        when(clientsRepo.findById(1L)).thenReturn(Optional.of(existingClient));

        String clientJson = "{\"id\":1,\"address\":\"Ул. Запорожская, д.13\",\"organisationLegalFormId\":2}";

        mockMvc.perform(put("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Поле должно быть заполнено!"))
                .andExpect(jsonPath("$.shortName").value("Поле должно быть заполнено!"));
    }

    @Test
    public void deleteClientTest() throws Exception {

        when(clientsRepo.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Клиент успешно удалён"));
    }

    @Test
    public void deleteWrongClientTest() throws Exception {

        mockMvc.perform(delete("/clients/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такого клиента не существует"));
    }

    @Test
    public void getAllClientsNotEmptyTest() throws Exception {

        Clients client1 = new Clients(1L, "PermskiyZavod", "PZ", "Ул. Запорожская, д.13", 2L);
        Clients client2 = new Clients(2L, "ZavodLusva", "ZL", "Ул. Подводников, д.13", 5L);

        when(clientsRepo.findAll()).thenReturn(Arrays.asList(client1, client2));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value(1L))
                .andExpect(jsonPath("$[0].name").value("PermskiyZavod"))
                .andExpect(jsonPath("$[0].shortName").value("PZ"))
                .andExpect(jsonPath("$[0].address").value("Ул. Запорожская, д.13"))
                .andExpect(jsonPath("$[0].organisationLegalFormId").value(2L))
                .andExpect(jsonPath("$[1].clientId").value(2L))
                .andExpect(jsonPath("$[1].name").value("ZavodLusva"))
                .andExpect(jsonPath("$[1].shortName").value("ZL"))
                .andExpect(jsonPath("$[1].address").value("Ул. Подводников, д.13"))
                .andExpect(jsonPath("$[1].organisationLegalFormId").value(5L));
    }

    @Test
    public void getAllClientsEmptyTest() throws Exception {

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllClientsSortedTest() throws Exception {
        Clients client1 = new Clients(1L, "John", "J", "123 Main St", 2L);
        Clients client2 = new Clients(2L, "Alice", "A", "456 Elm St", 1L);
        Clients client3 = new Clients(3L, "Bob", "B", "789 Oak St", 3L);

        List<Clients> clientsList = Arrays.asList(client1, client2, client3);

        List<Clients> expectedSortedClients = Arrays.asList(client2, client3, client1);

        when(clientsRepo.findAll()).thenReturn(clientsList);
        when(clientsRepo.findAll(Sort.by("name"))).thenReturn(expectedSortedClients);

        mockMvc.perform(get("/clients")
                        .param("sortBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[2].name").value("John"));
    }

    @Test
    public void badParamSortedTest() throws Exception {
        Clients client1 = new Clients(1L, "John", "J", "123 Main St", 2L);
        Clients client2 = new Clients(2L, "Alice", "A", "456 Elm St", 1L);
        Clients client3 = new Clients(3L, "Bob", "B", "789 Oak St", 3L);

        List<Clients> clientsList = Arrays.asList(client1, client2, client3);

        List<Clients> expectedSortedClients = Arrays.asList(client2, client3, client1);

        when(clientsRepo.findAll()).thenReturn(clientsList);
        when(clientsRepo.findAll(Sort.by("name"))).thenReturn(expectedSortedClients);

        mockMvc.perform(get("/clients")
                        .param("sortBy", "nameasd"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Неправильные параметры сортировки"));
    }
}
