package hansung.mannayo.mannayoserverapplication.Controller;

import hansung.mannayo.mannayoserverapplication.Model.Entity.Menu;
import hansung.mannayo.mannayoserverapplication.Model.Entity.Restaurant;
import hansung.mannayo.mannayoserverapplication.Model.Entity.Review;
import hansung.mannayo.mannayoserverapplication.Model.Type.Restaurant_Type;
import hansung.mannayo.mannayoserverapplication.Repository.MenuRepository;
import hansung.mannayo.mannayoserverapplication.Service.MenuService;
import hansung.mannayo.mannayoserverapplication.Service.ResponseService;
import hansung.mannayo.mannayoserverapplication.Service.RestaurantService;
import hansung.mannayo.mannayoserverapplication.dto.CommonResult;
import hansung.mannayo.mannayoserverapplication.dto.MenuResponse;
import hansung.mannayo.mannayoserverapplication.exceptions.NotFoundImageException;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class menuController {

    @Autowired
    MenuService menuService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    ResponseService responseService;

    String AWSfilepath = "/home/ec2-user/images/";

    String localfilepath = "C://images/review/";

    @GetMapping("/{id}")
    public ResponseEntity<List<MenuResponse>> findMenuByRestaurantId(@PathVariable Long id){
        Optional<List<Menu>> menuList = menuService.findMenuByRestaurantId(id);
        List<MenuResponse> menuResponses = new ArrayList<>();
        for(Menu menu : menuList.get()) {
            MenuResponse menuResponse = MenuResponse.builder()
                    .name(menu.getName())
                    .price(menu.getPrice())
                    .id(menu.getIdMenu())
                    .isbest(menu.isBest())
                    .image(menu.getImage())
                    .build();
            menuResponses.add(menuResponse);
        }

        return ResponseEntity.ok().body(menuResponses);
    }

    @ApiOperation(value = "feed image ?????? ", notes = "feed Image??? ???????????????. ??????????????? ?????? image??? ???????????????.")
    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getReviewImage(@PathVariable("id") Long id) throws IOException {
        Optional<Menu> menu = menuService.findById(id);
        String imagename = menu.get().getImage();
        InputStream imageStream = new FileInputStream(imagename);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
    }

    @ApiOperation(value = "menu data ??????")
    @PostMapping(value = "/input")
    public ResponseEntity<CommonResult> setMenuImage(@RequestParam String name, @RequestParam Integer price, @RequestParam Boolean isBest,
                                                     @RequestParam Long restaurantId,@RequestPart MultipartFile multipartFile) {
        Date date = new Date(); // ???????????? ??????????????? ?????? ?????? ?????? ???????????? ??????????????? ??????
        StringBuilder sb = new StringBuilder(); // ????????? ????????? ??????
        CommonResult commonResult = new CommonResult();

        Menu menu = Menu.builder()
                .Name(name)
                .Price(price)
                .restaurant(restaurantService.findbyId(restaurantId).get())
                .isBest(isBest)
                .build();

        Long menuId = menuService.insert(menu);

        if(multipartFile.isEmpty()) { // request??? ????????? ???????????? ??????
            sb.append("none");
            commonResult = responseService.getFailResult();
        } else {
            sb.append(date.getTime());
            sb.append(multipartFile.getOriginalFilename());
        }

        if(!multipartFile.isEmpty()) { // request??? ????????? ???????????????

            File dest = new File(AWSfilepath + sb.toString()); // ?????? ??????
            try {
                menu = menuService.findById(menuId).get(); // id??? Entity ?????????
                if(menu.getImage() == null) { // ?????? ????????? ????????? ????????? (????????? ???????????? ???????????? ?????????)
                    menu.setImage(AWSfilepath + sb.toString()); // member Entity??? ??????????????? ??????
                    menuService.updateImageAddress(menu); // ????????????
                    multipartFile.transferTo(dest); // ?????? ??????
                }else {
                    File file = new File(menu.getImage()); // ????????? ????????? ?????? ?????? DB?????? ????????? ??? ?????? ???????????? ??????
                    if(file.exists()) { // file??? ???????????????
                        file.delete(); // ??????
                    }

                    menu.setImage(AWSfilepath + sb.toString()); // ????????? ????????? ?????? DB??? ??????
                    menuService.updateImageAddress(menu); // Entity ????????????
                    multipartFile.transferTo(dest); // ?????? ??????
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

}
