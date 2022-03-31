package hansung.mannayo.mannayoserverapplication.Model.Entity;

import hansung.mannayo.mannayoserverapplication.Model.Entity.Member;
import hansung.mannayo.mannayoserverapplication.Model.Entity.Restaurant;
import hansung.mannayo.mannayoserverapplication.Model.Entity.Review;
import hansung.mannayo.mannayoserverapplication.Model.Type.Restaurant_Type;
import hansung.mannayo.mannayoserverapplication.Repository.MemberRepository;
import hansung.mannayo.mannayoserverapplication.Repository.RestaurantRepository;
import hansung.mannayo.mannayoserverapplication.Repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    ReviewRepository reviewRepository;


    @Autowired
    EntityManager entityManager;

    @Test
    public void ReviewTest(){
        Member member = new Member();
        member.setNickName("aa");
        member.setEmail("tmdhk502@naver.com");
        member.setPassword("tmdghk9609!");
        member.setPhoneNumber("010-0202-0303");
        member.setBirth(LocalDate.now());
        member.setReportCount(1);

        Member savedMember = memberRepository.save(member);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("dsd");
        restaurant.setRestaurant_type(Restaurant_Type.BUNSIK);
        restaurant.setNumber("0000000010");
        restaurant.setOwner("owner");
        restaurant.setAddress("aasdadad");
        restaurant.setJJIMcount(10);
        restaurant.setBusinessStartHours(LocalTime.now());
        restaurant.setBusinessEndHours(LocalTime.now());
        restaurant.setBusinessDayOff(LocalDate.now());

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Review review = new Review();
        review.setMember(member);
        review.setRestaurant(restaurant);

        Review savedReview = reviewRepository.save(review);

        assertThat(savedReview.getRestaurant().getIdRestaurant()).isEqualTo(savedRestaurant.getIdRestaurant());
        assertThat(savedReview.getMember().getNickName()).isEqualTo(savedMember.getNickName());



    }

}