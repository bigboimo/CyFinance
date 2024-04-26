package myapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import com.example.demo.earnings.Earnings;
import com.example.demo.earnings.EarningsController;
import com.example.demo.earnings.EarningsRepository;
import com.example.demo.users.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;
import com.example.demo.CyFinanceApplication;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EarningsController.class)
@ContextConfiguration(classes = CyFinanceApplication.class)
public class EarningsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EarningsRepository earningsRepository;

    private Earnings exampleEarnings;

    @Before
    public void setup() {
        User user = new User("Test User", "example@example.com", "password123", "admin");
        exampleEarnings = new Earnings(5000.0f, 3000.0f, user);
        exampleEarnings.setId(1);
    }

    @Test
    public void testGetAllEarnings() throws Exception {
        List<Earnings> allEarnings = Arrays.asList(exampleEarnings);
        when(earningsRepository.findAll()).thenReturn(allEarnings);

        mockMvc.perform(get("/earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(exampleEarnings.getId())))
                .andExpect(jsonPath("$[0].primaryMonthlyIncome", is((double) exampleEarnings.getPrimaryMonthlyIncome())))
                .andExpect(jsonPath("$[0].secondaryMonthlyIncome", is((double) exampleEarnings.getSecondaryMonthlyIncome())));
        verify(earningsRepository, times(1)).findAll();
    }

    @Test
    public void testGetEarningsById_Found() throws Exception {
        when(earningsRepository.findById(1L)).thenReturn(Optional.of(exampleEarnings));

        mockMvc.perform(get("/earnings/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.primaryMonthlyIncome", is((double) exampleEarnings.getPrimaryMonthlyIncome())))
                .andExpect(jsonPath("$.secondaryMonthlyIncome", is((double) exampleEarnings.getSecondaryMonthlyIncome())));
        verify(earningsRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetEarningsById_NotFound() throws Exception {
        when(earningsRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/earnings/{id}", 1))
                .andExpect(status().isNotFound());
        verify(earningsRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateEarnings_Valid() throws Exception {
        when(earningsRepository.save(any(Earnings.class))).thenReturn(exampleEarnings);

        mockMvc.perform(post("/earnings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exampleEarnings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.primaryMonthlyIncome", is((double) exampleEarnings.getPrimaryMonthlyIncome())))
                .andExpect(jsonPath("$.secondaryMonthlyIncome", is((double) exampleEarnings.getSecondaryMonthlyIncome())));
        verify(earningsRepository, times(1)).save(any(Earnings.class));
    }

    @Test
    public void testUpdateEarnings_Found() throws Exception {
        when(earningsRepository.findById(1L)).thenReturn(Optional.of(exampleEarnings));
        when(earningsRepository.save(any(Earnings.class))).thenReturn(exampleEarnings);

        mockMvc.perform(put("/earnings/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exampleEarnings)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"message\":\"success\"}")));
        verify(earningsRepository, times(1)).findById(1L);
        verify(earningsRepository, times(1)).save(any(Earnings.class));
    }

    @Test
    public void testDeleteEarnings_Found() throws Exception {
        when(earningsRepository.findById(1L)).thenReturn(Optional.of(exampleEarnings));

        mockMvc.perform(delete("/earnings/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"message\":\"success\"}")));
        verify(earningsRepository, times(1)).findById(1L);
        verify(earningsRepository, times(1)).deleteById(1L);
    }
}
