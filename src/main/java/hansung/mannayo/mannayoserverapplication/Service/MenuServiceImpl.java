package hansung.mannayo.mannayoserverapplication.Service;

import hansung.mannayo.mannayoserverapplication.Model.Entity.Menu;
import hansung.mannayo.mannayoserverapplication.Repository.MenuRepository;
import hansung.mannayo.mannayoserverapplication.dto.MenuResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService{

    @Autowired
    MenuRepository menuRepository;

    @Override
    public Optional<List<Menu>> findMenuByRestaurantId(Long id) {
        return menuRepository.findByRestaurantId(id);
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }
}