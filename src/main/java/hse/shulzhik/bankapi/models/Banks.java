package hse.shulzhik.bankapi.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "banks")
public class Banks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private Long bankId;


    @NotBlank(message = "Поле должно быть заполнено!")
    @Pattern(regexp = "^[0-9]{9}$", message = "Бик - девятизначное число")
    @Column(name = "bik")
    private String bik;

}
