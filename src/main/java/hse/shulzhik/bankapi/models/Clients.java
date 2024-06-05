package hse.shulzhik.bankapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @NotBlank(message = "Поле должно быть заполнено!")
    @Size(max = 255, message = "Имя не должно превышать 255 символов")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Поле должно быть заполнено!")
    @Size(max = 255, message = "Короткое имя не должно превышать 255 символов")
    @Column(name = "shortname")
    private String shortName;

    @NotBlank(message = "Поле должно быть заполнено!")
    @Size(max = 255, message = "Короткое имя не должно превышать 255 символов")
    @Column(name = "address")
    private String address;

    @NotNull(message = "Поле должно быть заполнено!")
    @Column(name = "organisation_legal_form_id")
    @Min(value = 1, message = "Id должно быть больше нуля!")
    private Long organisationLegalFormId;


}
