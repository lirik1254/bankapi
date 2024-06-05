package hse.shulzhik.bankapi.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hse.shulzhik.bankapi.util.CustomLocalDateDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deposits")
public class Deposits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depositId;

    @Column(name = "client_id")
    @Min(value = 1, message = "Id должно быть больше нуля!")
    @NotNull(message = "Поле должно быть заполнено!")
    private Long clientId;

    @NotNull(message = "Поле должно быть заполнено!")
    @Min(value = 1, message = "Id должно быть больше нуля!")
    @Column(name = "bank_id")
    private Long bankId;

    @NotNull(message = "Поле должно быть заполнено!")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @Column(name = "open_date")
    private LocalDate openDate;

    @NotNull(message = "Поле должно быть заполнено!")
    @Min(value = 0, message = "Процент должен быть не меньше нуля!")
    @Column(name = "percent")
    private Double percent;

    @NotNull(message = "Поле должно быть заполнено!")
    @Column(name = "retention_period")
    @Min(value = 1, message = "Срок должен быть больше нуля!")
    private Long retentionPeriod;
}
