package hse.shulzhik.bankapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organisation_legal_forms")
public class OrganisationLegalForms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organisation_legal_form_id")
    private Long organisationLegalFormId;

    @NotBlank(message = "Поле должно быть заполнено!")
    @Size(max = 255, message = "Имя не должно превышать 255 символов")
    @Column(name = "name")
    private String name;
}
