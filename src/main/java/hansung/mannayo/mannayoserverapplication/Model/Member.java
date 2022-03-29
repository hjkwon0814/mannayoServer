package hansung.mannayo.mannayoserverapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    private String NickName;

    @NotNull
    private String Email;

    @NotNull
    @JsonIgnore
    private String Password;

    @Enumerated(EnumType.STRING)
    private AccountType accountTypeEnum;

    @NotNull
    private String PhoneNumber;

    @NotNull
    private LocalDate Birth;

    @Enumerated(EnumType.STRING)
    private LoginType loginTypeEnum;

    private String ImageAddress;

    @NotNull()
    @Column(columnDefinition = "integer default 0")
    private Integer ReportCount;


}

//CREATE TABLE Member (
//  NICKNAME VARCHAR(20)  NOT NULL   AUTO_INCREMENT,
//  Email VARCHAR(255)  NOT NULL  ,
//  Password_2 VARCHAR(255)  NOT NULL  ,
//  AccountType ENUM  NOT NULL  ,
//  PhoneNumber VARCHAR(20)  NOT NULL  ,
//  Birth DATE  NOT NULL  ,
//  LoginType ENUM  NOT NULL  ,
//  Image VARCHAR(100)  NULL  ,
//  ReportCount INTEGER UNSIGNED  NOT NULL DEFAULT 0   ,
//PRIMARY KEY(NICKNAME));