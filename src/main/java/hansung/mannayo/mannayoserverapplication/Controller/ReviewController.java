package hansung.mannayo.mannayoserverapplication.Controller;

import hansung.mannayo.mannayoserverapplication.Model.Entity.Member;
import hansung.mannayo.mannayoserverapplication.Model.Entity.Restaurant;
import hansung.mannayo.mannayoserverapplication.Model.Entity.Review;
import hansung.mannayo.mannayoserverapplication.Repository.RestaurantRepository;
import hansung.mannayo.mannayoserverapplication.Service.MemberService;
import hansung.mannayo.mannayoserverapplication.Service.ResponseService;
import hansung.mannayo.mannayoserverapplication.Service.RestaurantService;
import hansung.mannayo.mannayoserverapplication.Service.ReviewService;
import hansung.mannayo.mannayoserverapplication.dto.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ResponseService responseService;

    @Autowired
    MemberService memberService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    RestaurantRepository restaurantRepository;

    String AWSfilepath = "/home/ec2-user/images/";

    String localfilepath = "C://images/review/";

    @GetMapping
    public ResponseEntity<List<ReviewDto>> findAll(){
        List<Review> list = reviewService.findAll();
        List<ReviewDto> reviewDtoList = new ArrayList<>();
        return ResponseEntity.ok().body(reviewDtoList);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "????????? ??? ?????? ??????",required = true,dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "id??? ????????????(1???)" ,notes = "Id??? ????????? ????????????")
    @GetMapping(value = "/{id}")
    public ResponseEntity<List<ReviewDto>> findbyRestaurantId(@PathVariable Long id){ //restaurant id
        List<ReviewDto> obj = reviewService.findByRestaurantId(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<CommonResult> insert(@RequestBody ReviewRequestDto reviewRequestDto){
        CommonResult commonResult = new CommonResult();
        Review review = reviewService.insert(reviewRequestDto); // ????????????
        Restaurant restaurant = restaurantRepository.findById(reviewRequestDto.getRestaurantId()).get();
        Long count = reviewService.getCountReviewsByRestaurantId(restaurant.getId());
        String c = count.toString();
        Float fc = Float.parseFloat(c); //?????? ??????
        Float starpoint = restaurant.getStarPointInfo(); // ??????

        System.out.println(starpoint + " before");
        System.out.println("count = " + fc);
        System.out.println("reviewRequestDto = " + reviewRequestDto.getStarPoint());

        starpoint = (((fc-1) * starpoint) + reviewRequestDto.getStarPoint()) / (count);
        System.out.println(starpoint + " after");
        restaurant.setStarPointInfo(starpoint);

        restaurantRepository.save(restaurant);

        if(review != null) {
            commonResult = responseService.getSuccessResult();
            commonResult.setMsg(review.getId().toString());
            return ResponseEntity.ok().body(commonResult);
        }

        commonResult = responseService.getFailResult();
        return  ResponseEntity.ok().body(commonResult);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Review> update(@PathVariable Long id, @RequestBody ReviewDto obj){
        return ResponseEntity.ok().body(reviewService.update(id,obj));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Review> delete(@PathVariable Long id, @RequestBody ReviewDto obj){
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "feed image ?????? ", notes = "feed Image??? ???????????????. ??????????????? ?????? image??? ???????????????.")
    @GetMapping(value = "image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getReviewImage(@PathVariable("id") Long id) throws IOException {
        Optional<Review> review = reviewService.findimagebyId(id);
        String imagename = review.get().getImage();
        InputStream imageStream = new FileInputStream(imagename);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
    }

    @ApiOperation(value = "?????? ?????? ??????")
    @PostMapping("/reviewimage")
    public ResponseEntity<CommonResult> registerProfileImage(@RequestParam Long id, @RequestPart MultipartFile multipartFile) {
        Date date = new Date(); // ???????????? ??????????????? ?????? ?????? ?????? ???????????? ??????????????? ??????
        StringBuilder sb = new StringBuilder(); // ????????? ????????? ??????
        Review review = new Review(); // ???????????? ??????
        CommonResult commonResult = new CommonResult();
        if(multipartFile.isEmpty()) { // request??? ????????? ???????????? ??????
            sb.append("none");
            commonResult = responseService.getFailResult();
        } else {
            sb.append(date.getTime());
            sb.append(multipartFile.getOriginalFilename());
        }

        if(!multipartFile.isEmpty()) { // request??? ????????? ???????????????

            File dest = new File(localfilepath + sb.toString()); // ?????? ??????
            try {
                review = reviewService.findById(id).get();// id??? Entity ?????????
                if(review.getImage() == null) { // ?????? ????????? ????????? ????????? (????????? ???????????? ???????????? ?????????)
                    review.setImage(localfilepath + sb.toString()); // member Entity??? ??????????????? ??????
                    reviewService.updateImageAddress(review); // ????????????
                    multipartFile.transferTo(dest); // ?????? ??????
                    System.out.println("?????? ?????? ?????? 1");
                }else {
                    File file = new File(review.getImage()); // ????????? ????????? ?????? ?????? DB?????? ????????? ??? ?????? ???????????? ??????
                    if(file.exists()) { // file??? ???????????????
                        file.delete(); // ??????
                    }

                    review.setImage(localfilepath + sb.toString()); // ????????? ????????? ?????? DB??? ??????
                    reviewService.updateImageAddress(review); // Entity ????????????
                    multipartFile.transferTo(dest); // ?????? ??????
                    System.out.println("?????? ?????? ?????? 2");
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        commonResult = responseService.getSuccessResult();
        return ResponseEntity.ok().body(commonResult);
    }

    private List<ReviewDto> toConvertDto(List<Review> list) {
        List<ReviewDto> reviewDtoList = new ArrayList<>();
        for(Review r : list) {
            ReviewDto reviewDto= ReviewDto.builder()
                    .content(r.getContent())
                    .memberId(r.getMember().getId())
                    .image(r.getImage())
                    .isDeleted(r.getIsDeleted())
                    .isModifoed(r.getIsModified())
                    .starPoint(r.getStarPoint())
                    .writeDate(r.getWriteDate())
                    .memberImage(r.getMember().getImageAddress())
                    .memberNickname(r.getMember().getNickName())
                    .build();
            reviewDtoList.add(reviewDto);
        }

        return reviewDtoList;
    }
}
