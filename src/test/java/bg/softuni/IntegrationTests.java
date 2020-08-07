package bg.softuni;


import bg.softuni.model.entity.Item;
import bg.softuni.model.entity.Offer;
import bg.softuni.model.entity.Photo;
import bg.softuni.model.entity.User;
import bg.softuni.model.enumeration.Category;
import bg.softuni.repository.ItemRepository;
import bg.softuni.repository.OfferRepository;
import bg.softuni.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc()
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void init() {
        User owner = this.userRepository.findByUsername("admin");
        Item item = new Item();
        item.setOwner(owner);
        item.setName("ITEM");
        item.setDescription("DESCRIPTION");
        item.setCategory(Category.AWARDS);
        item.setPhotos(Set.of(new Photo("LOCATION", item)));
        Item temp = this.itemRepository.saveAndFlush(item);
        this.offerRepository.saveAndFlush(new Offer(temp, owner, BigDecimal.TEN));
    }

    @AfterEach
    public void destroy() {
        this.offerRepository.deleteAll();
        this.itemRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void testEditRolesPageWithAdmin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/users/roles/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-role"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testEditRolesPageWithUser() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/users/roles/edit"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetOffers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/offers"))
                .andExpect(status().isOk())
                .andExpect(view().name("offers"))
                .andExpect(model().attributeExists("offers"))
                .andExpect(model().attributeExists("max_index"))
                .andExpect(model().attributeExists("current"))
                .andExpect(model().attribute("current", 0))
                .andExpect(model().attribute("max_index", 0));
    }

    @Test
    public void testGetOffersWithTooLargePage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/offers").queryParam("page", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("offers"))
                .andExpect(model().attributeExists("offers"))
                .andExpect(model().attributeExists("max_index"))
                .andExpect(model().attributeExists("current"))
                .andExpect(model().attribute("current", 0))
                .andExpect(model().attribute("max_index", 0));
    }

    @Test
    public void testGetOffersWithNegativePage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/offers").queryParam("page", "-55"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/error"));
    }

    @Test
    public void testGetOffersWithAwardsCategory() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/offers/categories/{cat}", "awards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].itemName", is("ITEM")))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    public void testGetOffersWithAwardsCategoryWithLargePage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/offers/categories/{cat}", "awards").param("page", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].itemName", is("ITEM")))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    public void testGetOffersWithAwardsCategoryWithNegativePage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/offers/categories/{cat}", "awards").param("page", "-50"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/error"))
                .andExpect(flash().attribute("flag", "Page index must not be less than zero!"));
    }

    @Test
    public void testGetOffersWithAwardsCategoryWithWrongCat() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/offers/categories/{cat}", "test"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/error"))
                .andExpect(flash().attribute("flag", "test is not a valid value!"));
    }
}