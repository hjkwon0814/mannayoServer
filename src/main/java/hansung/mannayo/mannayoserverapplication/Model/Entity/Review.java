package hansung.mannayo.mannayoserverapplication.Model.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id @GeneratedValue
    private Integer id;

    String title;

    String content;

    LocalDateTime writeDate;

    String image;

    Float starPoint;

    @Column(columnDefinition = "boolean default false")
    Boolean isDeleted;

    @Column(columnDefinition = "boolean default false")
    Boolean isModified;

    LocalDateTime ModifiedDate;
    LocalDateTime DeletedDate;

    @JoinColumn(name = "member_id")
    @ManyToOne(targetEntity = Member.class) @JsonManagedReference
    Member member;

    @ManyToOne(targetEntity = Restaurant.class) @JsonManagedReference @JoinColumn(name = "restaurant_Id")
    Restaurant restaurant;

    @PrePersist
    public void createAt(){
        this.writeDate = LocalDateTime.now();
    }


}
