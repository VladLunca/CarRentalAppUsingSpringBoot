package com.carreantalapp.app.security;

import com.carreantalapp.app.configurations.SecurityConfig;
import com.carreantalapp.app.services.CarRentalCompanyService;
import com.carreantalapp.app.services.CustomUserDetailsService;
import com.carreantalapp.app.services.RentalService;
import com.carreantalapp.app.services.StaffService;
import com.carreantalapp.app.services.UserService;
import com.carreantalapp.app.services.car.CarModelService;
import com.carreantalapp.app.services.car.CarService;
import com.carreantalapp.app.services.car.components.CarBodyService;
import com.carreantalapp.app.services.car.components.CategoryService;
import com.carreantalapp.app.services.car.components.EngineService;
import com.carreantalapp.app.services.car.components.TransmissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.upload-dir=target/test-uploads")
class RoleAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private RentalService rentalService;
    @MockitoBean private CarService carService;
    @MockitoBean private StaffService staffService;
    @MockitoBean private UserService userService;
    @MockitoBean private CarModelService carModelService;
    @MockitoBean private CarBodyService carBodyService;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private EngineService engineService;
    @MockitoBean private TransmissionService transmissionService;
    @MockitoBean private CarRentalCompanyService carRentalCompanyService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Nested
    @DisplayName("Unauthenticated access")
    class Anonymous {

        @Test
        @WithAnonymousUser
        void protectedPagesRedirectToLogin() throws Exception {
            mockMvc.perform(get("/home")).andExpect(redirectedUrl("/login"));
            mockMvc.perform(get("/admin/staff")).andExpect(redirectedUrl("/login"));
            mockMvc.perform(get("/rentals/allRentals")).andExpect(redirectedUrl("/login"));
            mockMvc.perform(get("/employee/addCarsForm")).andExpect(redirectedUrl("/login"));
            mockMvc.perform(get("/cars/showCars")).andExpect(redirectedUrl("/login"));
        }

        @Test
        @WithAnonymousUser
        void loginPageIsPublic() throws Exception {
            mockMvc.perform(get("/login")).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void registrationIsPublic() throws Exception {
            mockMvc.perform(get("/registration/form")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("CUSTOMER restrictions")
    class CustomerRole {

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void cannotReachAdminPages() throws Exception {
            mockMvc.perform(get("/admin/staff")).andExpect(status().isForbidden());
            mockMvc.perform(get("/admin/rentalsCompanies/show")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void cannotReachEmployeePages() throws Exception {
            mockMvc.perform(get("/employee/addCarsForm")).andExpect(status().isForbidden());
            mockMvc.perform(get("/employee/addCarModelForm")).andExpect(status().isForbidden());
            mockMvc.perform(get("/employee/addEngine")).andExpect(status().isForbidden());
            mockMvc.perform(get("/employee/addCategory")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void cannotReachManagerPages() throws Exception {
            mockMvc.perform(get("/manager/staff")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void cannotBrowseTheCompanyFleet() throws Exception {
            mockMvc.perform(get("/cars/showCars")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void cannotApproveRentals() throws Exception {
            mockMvc.perform(post("/rentals/approveRental").param("rentalId", "1").with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("EMPLOYEE restrictions")
    class EmployeeRole {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        void cannotReachAdminPages() throws Exception {
            mockMvc.perform(get("/admin/staff")).andExpect(status().isForbidden());
            mockMvc.perform(get("/admin/rentalsCompanies/show")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        void cannotManageStaff() throws Exception {
            mockMvc.perform(get("/manager/staff")).andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("MANAGER restrictions")
    class ManagerRole {

        @Test
        @WithMockUser(roles = "MANAGER")
        void cannotReachAdminPages() throws Exception {
            mockMvc.perform(get("/admin/staff")).andExpect(status().isForbidden());
            mockMvc.perform(get("/admin/rentalsCompanies/show")).andExpect(status().isForbidden());
            mockMvc.perform(post("/admin/staff/toggle-status").param("userId", "1").with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("SUPER_ADMIN restrictions")
    class SuperAdminRole {

        @Test
        @WithMockUser(roles = "SUPER_ADMIN")
        void cannotManageFleetOrRentCars() throws Exception {
            mockMvc.perform(get("/employee/addCarsForm")).andExpect(status().isForbidden());
            mockMvc.perform(get("/cars/chooseDates")).andExpect(status().isForbidden());
            mockMvc.perform(get("/cars/showCars")).andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("CSRF protection")
    class Csrf {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        void postWithoutTokenIsRejected() throws Exception {
            mockMvc.perform(post("/employee/cars/deleteCar").param("carId", "1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "SUPER_ADMIN")
        void postWithTokenPassesCsrfCheck() throws Exception {
            mockMvc.perform(post("/admin/staff/toggle-status").param("userId", "1").with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("allRentals view guard")
    class AllRentalsViewGuard {

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void customerRequestingStaffViewIsSentBackToOwnRentals() throws Exception {
            mockMvc.perform(get("/rentals/allRentals").param("view", "emp"))
                    .andExpect(redirectedUrl("/rentals/allRentals?view=client"));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void customerRequestingManagerViewIsSentBackToOwnRentals() throws Exception {
            mockMvc.perform(get("/rentals/allRentals").param("view", "man"))
                    .andExpect(redirectedUrl("/rentals/allRentals?view=client"));
        }

        @Test
        @WithMockUser(roles = "SUPER_ADMIN")
        void superAdminSeesEveryRentalRatherThanAnEmptyCompanyScopedList() throws Exception {
            mockMvc.perform(get("/rentals/allRentals").param("view", "emp"))
                    .andExpect(status().isOk());

            verify(rentalService).getAllRentals(false, false, false, false, false);
            verify(staffService, never()).getCarRentalCompanyIdByUserId(any());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        void managerStillSeesOnlyTheirOwnCompany() throws Exception {
            when(staffService.getCarRentalCompanyIdByUserId(any())).thenReturn(1L);

            mockMvc.perform(get("/rentals/allRentals").param("view", "man"))
                    .andExpect(status().isOk());

            verify(rentalService).getRentalsForCompany(1L, false, false, false, false, false);
        }
    }
}
