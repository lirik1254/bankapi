package hse.shulzhik.bankapi.controllers;

import hse.shulzhik.bankapi.models.OrganisationLegalForms;
import hse.shulzhik.bankapi.repos.OrganisationLegalFormsRepo;
import hse.shulzhik.bankapi.services.OrganisationLegalFormsService;
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
@WebMvcTest(OrganisationsLegalFormController.class)
public class OrganisationLegalFormTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    OrganisationLegalFormsRepo organisationLegalFormsRepo;

    @MockBean
    OrganisationLegalFormsService organisationLegalFormsService;

    @Test
    public void createOrganisationLegalFormTest() throws Exception {
        OrganisationLegalForms form = new OrganisationLegalForms(1L, "ООО");

        when(organisationLegalFormsRepo.save(any(OrganisationLegalForms.class))).thenReturn(form);

        mockMvc.perform(post("/forms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"ООО\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organisationLegalFormId").value(1L))
                .andExpect(jsonPath("$.name").value("ООО"));
    }

    @Test
    public void createOrganisationLegalFormWithEmptyNameTest() throws Exception {
        mockMvc.perform(post("/forms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Поле должно быть заполнено!"));
    }

    @Test
    public void createOrganisationLegalFormWithTooLongNameTest() throws Exception {
        String longName = "A".repeat(256); // Строка длиной больше 255 символов

        mockMvc.perform(post("/forms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"" + longName + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Имя не должно превышать 255 символов"));
    }

    @Test
    public void getOrganisationLegalFormByIdTest() throws Exception {
        OrganisationLegalForms form = new OrganisationLegalForms(1L, "ОАО");

        when(organisationLegalFormsRepo.findById(1L)).thenReturn(Optional.of(form));

        mockMvc.perform(get("/forms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organisationLegalFormId").value(1L))
                .andExpect(jsonPath("$.name").value("ОАО"));
    }

    @Test
    public void getOrganisationLegalFormByWrongIdTest() throws Exception {
        mockMvc.perform(get("/forms/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такой формы не существует"));
    }

    @Test
    public void updateOrganisationLegalFormTest() throws Exception {
        OrganisationLegalForms existingForm = new OrganisationLegalForms(1L, "ЗАО");
        OrganisationLegalForms updatedForm = new OrganisationLegalForms(1L, "ПАО");

        when(organisationLegalFormsService.updateOrganisationLegalForm(anyLong(), any(OrganisationLegalForms.class))).thenReturn(updatedForm);
        when(organisationLegalFormsRepo.findById(1L)).thenReturn(Optional.of(existingForm));

        mockMvc.perform(put("/forms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"ПАО\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organisationLegalFormId").value(1L))
                .andExpect(jsonPath("$.name").value("ПАО"));
    }

    @Test
    public void updateOrganisationLegalFormWithTooLongNameTest() throws Exception {
        OrganisationLegalForms existingForm = new OrganisationLegalForms(1L, "ООО");

        when(organisationLegalFormsRepo.findById(1L)).thenReturn(Optional.of(existingForm));

        // Создаем имя длиной более 255 символов
        String longName = "A".repeat(256); // Строка длиной больше 255 символов

        mockMvc.perform(put("/forms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"" + longName + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Имя не должно превышать 255 символов"));
    }

    @Test
    public void deleteOrganisationLegalFormTest() throws Exception {
        when(organisationLegalFormsRepo.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/forms/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Форма была успешно удалена"));
    }

    @Test
    public void deleteWrongOrganisationLegalFormTest() throws Exception {
        mockMvc.perform(delete("/forms/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Такой формы не существует"));
    }

    @Test
    public void getAllFormsSortedTest() throws Exception {
        OrganisationLegalForms form1 = new OrganisationLegalForms(1L, "ЗАО");
        OrganisationLegalForms form2 = new OrganisationLegalForms(2L, "ООО");
        OrganisationLegalForms form3 = new OrganisationLegalForms(3L, "ПАО");

        List<OrganisationLegalForms> formsList = Arrays.asList(form1, form2, form3);

        List<OrganisationLegalForms> expectedSortedForms = Arrays.asList(form3, form1, form2);

        when(organisationLegalFormsRepo.findAll()).thenReturn(formsList);
        when(organisationLegalFormsRepo.findAll(Sort.by("name"))).thenReturn(expectedSortedForms);

        mockMvc.perform(get("/forms")
                        .param("sortBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].organisationLegalFormId").value(3))
                .andExpect(jsonPath("$[0].name").value("ПАО"))
                .andExpect(jsonPath("$[1].organisationLegalFormId").value(1))
                .andExpect(jsonPath("$[1].name").value("ЗАО"))
                .andExpect(jsonPath("$[2].organisationLegalFormId").value(2))
                .andExpect(jsonPath("$[2].name").value("ООО"));
    }

    @Test
    public void badParamsSortTest() throws Exception {
        OrganisationLegalForms form1 = new OrganisationLegalForms(1L, "ЗАО");
        OrganisationLegalForms form2 = new OrganisationLegalForms(2L, "ООО");
        OrganisationLegalForms form3 = new OrganisationLegalForms(3L, "ПАО");

        List<OrganisationLegalForms> formsList = Arrays.asList(form1, form2, form3);

        List<OrganisationLegalForms> expectedSortedForms = Arrays.asList(form3, form1, form2);

        when(organisationLegalFormsRepo.findAll()).thenReturn(formsList);
        when(organisationLegalFormsRepo.findAll(Sort.by("name"))).thenReturn(expectedSortedForms);

        mockMvc.perform(get("/forms")
                        .param("sortBy", "namsdfe"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Неправильные параметры сортировки"));
    }
}
